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
 * A class describing an Giraffe prey, one of the
 * actors in our simulation.
 */

public class Giraffe extends Prey{
	// The age at which a this giraffe can start to breed.
	private static final int BREEDING_AGE = Field.FULL_DAY_LENGTH * 2;
	// The age to which a giraffe can live.
	private static final int MAX_AGE = Field.FULL_DAY_LENGTH * 40;
	// The likelihood of a giraffe breeding.
	private static final double BREEDING_PROBABILITY = 0.15;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 2;
	// The food value of a single prey. In effect, this is the
	// number of steps a Giraffe can go before it has to eat again.
	private static final int PLANT_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.1); // 1440 * 0.001 = 1.4
	
	private static final int MAX_FOOD = Field.FULL_DAY_LENGTH;
	
	// 	The probability that an animal gets infected upon contacting an infected being.
	private static final double PROB_GETS_INFECTED = 0.0006;
	
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();
	

	public double getProbabilityGettingInfected() {
		return PROB_GETS_INFECTED;
	}
	
	/**
     * Create an Giraffe. A Giraffe can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the Giraffe will have random age, hunger level and sickness parameters. If false, 
     * the Giraffe is a newborn, it will have FULL_DAY_LENGTH food level and it's not going to be sick. 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
	
	public Giraffe(boolean randomAge, Field field, Location location)
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
     * This is what the Giraffe does most of the time: it grazes for
     * plants. In the process, it might breed, die of hunger, die of disease, die of eaten,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newGiraffes A list to return newly born Giraffes.
     */
	
	@Override
	public void act(List<FieldObject> newGiraffes, int stepCount) {
		incrementAge(MAX_AGE);
		incrementHunger();
		incrementStepsSick();
		if(isAlive()) {
			giveBirth(newGiraffes);   
			if(!this.isSick) this.checkIfGetsInfected();
			// Move towards a source of food if found.
			if(!getField().isDayTime(stepCount)) return;
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
	 * Check whether or not this Giraffe is going to give birth at this step.
	 * New births will be made into free ground adjacent locations.
	 * @param newGiraffes A list to return newly born Giraffes.
	 */
	
	private void giveBirth(List<FieldObject> newGiraffes)
	{
		if(this.isMale) return; //males don't giveBirth.
		Field field = getField();
		List<Location> adjacentLocations = field.adjacentLocations(getLocation());
		for (Location location : adjacentLocations) {
			FieldObject animal = (FieldObject) field.getObjectAt(location);
			if(animal != null && animal instanceof Giraffe && ((Giraffe)animal).isMale) {
				List<Location> free = field.getFreeGroundAdjacentLocations(getLocation());
				int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
				for(int b = 0; b < births && free.size() > 0; b++) {
					Location loc = free.remove(0);
					Giraffe young = new Giraffe(false, field, loc);
					newGiraffes.add(young);
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
			FieldObject fieldObject = (FieldObject) field.getPlantAt(where);
			if(fieldObject != null && fieldObject.isAlive() && fieldObject instanceof Plant) {
				fieldObject.setDead();
				foodLevel += this.getFoodValue(fieldObject);
				return where;
			}
		}
		return null;
	}

	private int getFoodValue(FieldObject fieldObject) {
		if(fieldObject instanceof Plant) {
			return PLANT_FOOD_VALUE;
		}
		throw new NoSuchElementException("Giraffes cannot eat " + fieldObject.getClass());
	}

}
