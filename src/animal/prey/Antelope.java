package src.animal.prey;

import java.util.Iterator;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import src.Field;
import src.Location;
import src.Randomizer;
import src.animal.FieldObject;
import src.animal.Prey;
import src.animal.plants.Plant;


/**
 * A class describing an Antelope prey, one of the
 * actors in our simulation.
 */

public class Antelope extends Prey {

	// The age at which a Antelope can start to breed.
	private static final int BREEDING_AGE = Field.FULL_DAY_LENGTH * 1;
	// The age to which a Antelope can live.
	private static final int MAX_AGE = Field.FULL_DAY_LENGTH * 40;
	// The likelihood of a Antelope breeding.
	private static final double BREEDING_PROBABILITY = 0.6;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 2;
	// The food value of a single plant. In effect, this is the
	// number of steps an Antelope can go before it has to eat again.
	private static final int PLANT_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.1); // 1440 * 0.001 = 1.4
	
	private static final int MAX_FOOD = Field.FULL_DAY_LENGTH;
	
	// 	The probability that an animal gets infected upon contacting an infected being.
	private static final double PROB_GETS_INFECTED = 0.0001;

	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();


	public double getProbabilityGettingInfected() {
		return PROB_GETS_INFECTED;
	}
	
	/**
     * Create an Antelope. A antelope can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the antelope will have random age, hunger level and sickness parameters. If false, 
     * the antelope is a newborn, it will have FULL_DAY_LENGTH food level and it's not going to be sick. 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
	public Antelope(boolean randomAge, Field field, Location location)
	{
		super(field, location);
		if(randomAge) {
			age = rand.nextInt(MAX_AGE);
			foodLevel = rand.nextInt((int) Math.floor(Field.FULL_DAY_LENGTH * 0.1));
			this.isSick = rand.nextDouble() < 0.1;
		}
		else {
			age = 0;
			foodLevel = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.01);
			this.isSick = false;
		}
	}

	 /**
     * This is what the antelope does most of the time: it grazes for
     * plants. In the process, it might breed, die of hunger, die of disease, die of eaten,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newAntelopes A list to return newly born antelopes.
     */
	@Override
	public void act(List<FieldObject> newAntelopes, int stepCount) {
		incrementAge(MAX_AGE);
		incrementHunger();
		if(isAlive()) {
			giveBirth(newAntelopes);  
			if(!this.isSick) this.checkIfGetsInfected();
			if(!getField().isDayTime(stepCount)) return;
			// Move towards a source of food if found.
			Location newLocation = findFood();
			if(newLocation == null) {
				// No food found - try to move to a free location.
				newLocation = getField().freeGroundAdjacentLocation(getLocation());
			}
			// See if it was possible to move.
			if(newLocation != null) {
				setLocation(newLocation);
			}
			else {
				// Overcrowding.
				setDead();
			}
		}

	}
	 /**
	 * Check whether or not this Antelope is going to give birth at this step.
	 * New births will be made into free ground adjacent locations.
	 * @param newAntelopes A list to return newly born antelopes.
	 */
	private void giveBirth(List<FieldObject> newAntelopes)
	{
		if(this.isMale) return; //males don't giveBirth.
		Field field = getField();
		List<Location> adjacentLocations = field.adjacentLocations(getLocation());
		for (Location location : adjacentLocations) {
			FieldObject animal = (FieldObject) field.getObjectAt(location);
			if(animal != null && animal instanceof Antelope && ((Antelope)animal).isMale) {
				List<Location> free = field.getFreeGroundAdjacentLocations(getLocation());
				int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
				for(int b = 0; b < births && free.size() > 0; b++) {
					Location loc = free.remove(0);
					Antelope young = new Antelope(false, field, loc);
					newAntelopes.add(young);
				}
				return;
			}
		}
	}

	/**
	 * Look for plants adjacent to the current location.
	 * Only the first grass is eaten.
	 * @return Where food was found, or null if it wasn't.
	 */
	private Location findFood()
	{
		if(this.foodLevel > MAX_FOOD) return null;
		Field field = getField();
		List<Location> adjacent = field.adjacentLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		while(it.hasNext()) {
			Location where = it.next();
			FieldObject possiblePlant = (FieldObject) field.getPlantAt(where);
			FieldObject possibleAnimal = (FieldObject) field.getObjectAt(where);
			//Given a chance, and upon contact with a plant, the antelope might get sick.
			if(possiblePlant instanceof Plant && !this.isSick && rand.nextDouble() < 0.08) {
				this.isSick = true;
			}
			if(possiblePlant != null && possiblePlant.isAlive() && possiblePlant instanceof Plant && possibleAnimal == null) {
				possiblePlant.setDead();
				foodLevel += this.getFoodValue(possiblePlant);
				return where;
			}
		}
		return null;
	}

	private int getFoodValue(FieldObject fieldObject) {
		if(fieldObject instanceof Plant) {
			return PLANT_FOOD_VALUE;
		}
		throw new NoSuchElementException("Antelopes cannot eat " + fieldObject.getClass());
	}

}