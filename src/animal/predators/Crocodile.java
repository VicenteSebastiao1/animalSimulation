package src.animal.predators;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import src.Field;
import src.Location;
import src.Randomizer;
import src.animal.FieldObject;
import src.animal.Predator;
import src.animal.Prey;
import src.animal.plants.Plant;
import src.animal.prey.Antelope;
import src.animal.prey.Giraffe;
import src.animal.prey.Zebra;

/**
 * A class describing an Crocodile predator, one of the
 * actors in our simulation.
 */

public class Crocodile extends Predator {
	
	// The age at which a Crocodile can start to breed.
	private static final int BREEDING_AGE = Field.FULL_DAY_LENGTH * 2;
	// The age to which a Crocodile can live.
	private static final int MAX_AGE = Field.FULL_DAY_LENGTH * 40;
	// The likelihood of a Crocodile breeding.
	private static final double BREEDING_PROBABILITY = 0.5;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 10;
	// The food value of a single prey. When eaten, a prey will provide the predator 
	// with enough food for a time period relative to the length of a full day. 
	private static final int ANTELOPE_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.75);
	private static final int GIRAFFE_FOOD_VALUE = Field.FULL_DAY_LENGTH;
	private static final int ZEBRA_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.5);

	// 	The probability that an animal gets infected upon contacting an infected being.
	private static final double PROB_GETS_INFECTED = 0.0015;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	public double getProbabilityGettingInfected() {
		return PROB_GETS_INFECTED;
	}
	
	/**
     * Create a Crocodile. A Crocodile can be created as a new born (age zero
     * and not hungry) or with a random age and food level.
     * 
     * @param randomAge If true, the crocodile will have random age, hunger level and sickness parameters. If false, 
     * the crocodile is a newborn, it will have GIRAFFE_FOOD_VALUE food level and it's not going to be sick. 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
	
	public Crocodile(boolean randomAge, Field field, Location location)
	{
		super(field, location);
		if(randomAge) {
			age = rand.nextInt(MAX_AGE);
			foodLevel = rand.nextInt(GIRAFFE_FOOD_VALUE);
			this.isSick = rand.nextDouble() < 0.1;
			if(this.isSick) {
				this.stepsBeingSick = rand.nextInt(1000);
			}
		}
		else {
			age = 0;
			foodLevel = GIRAFFE_FOOD_VALUE;
			this.isSick = false;
		}
	}
	 /**
     * This is what the crocodile does most of the time: it hunts for
     * prey. In the process, it might breed, die of hunger, die of disease,
     * or die of old age.
     * @param field The field currently occupied.
     * @param newCrocodiles A list to return newly born crocodiles.
     */
	@Override
	public void act(List<FieldObject> newCrocodiles, int stepCount) {
		incrementAge(MAX_AGE);
		incrementHunger();
		incrementStepsSick();
		if(isAlive()) {
			giveBirth(newCrocodiles);
			if(!this.isSick) checkIfGetsInfected();
			// The above actions happen even though the Crocodile sleeps
			if(!getField().isDayTime(stepCount)) return;
			// Move towards a source of food if found.
			Location newLocation = findFood();
			newLocation = getField().freeWaterAdjacentLocation(getLocation());
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
	 * Check whether or not this Crocodile is going to give birth at this step.
	 * New births will be made into free water adjacent locations.
	 * @param newCrocodiles A list to return newly born crocodiles.
	 */
	private void giveBirth(List<FieldObject> newCrocodiles)
	{
		if(this.isMale) return; //males don't giveBirth.
		Field field = getField();
		List<Location> adjacentLocations = field.adjacentLocations(getLocation());
		for (Location location : adjacentLocations) {
			FieldObject animal = (FieldObject) field.getObjectAt(location);
			if(animal != null && animal instanceof Crocodile && ((Crocodile)animal).isMale) {
				List<Location> free = field.getFreeGroundAdjacentLocations(getLocation());
				int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
				for(int b = 0; b < births && free.size() > 0; b++) {
					Location loc = free.remove(0);
					Crocodile young = new Crocodile(false, field, loc);
					newCrocodiles.add(young);
				}
				return;
			}
		}
	}

	/**
	 * Look for prey adjacent to the current location.
	 * Only the first live prey is eaten.
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
			FieldObject fieldObject = (FieldObject) field.getObjectAt(where);
			if(fieldObject != null && fieldObject.isAlive() && fieldObject instanceof Prey && !(fieldObject instanceof Plant)) {
				fieldObject.setDead();
				foodLevel += this.getFoodValue(fieldObject);
				return where;
			}
		}
		return null;
	}

	/**
	 * This method gets the food value from the global variables.
	 * This method assists the findFood method by letting it know how much
	 * food the crocodile recieved from eating a prey. 
	 */
	private int getFoodValue(FieldObject fieldObject) {
		if(fieldObject instanceof Zebra) {
			return ZEBRA_FOOD_VALUE;
		} else if(fieldObject instanceof Antelope) {
			return ANTELOPE_FOOD_VALUE;
		} else if(fieldObject instanceof Giraffe) {
			return GIRAFFE_FOOD_VALUE;
		}
		throw new NoSuchElementException("Crocodiles cannot eat " + fieldObject.getClass());
	}

}
