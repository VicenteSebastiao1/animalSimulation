package src.animal.predators;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;

import src.Field;
import src.Location;
import src.Randomizer;
import src.Simulator;
import src.animal.Animal;
import src.animal.FieldObject;
import src.animal.Predator;
import src.animal.Prey;
import src.animal.plants.Plant;
import src.animal.prey.Antelope;
import src.animal.prey.Giraffe;
import src.animal.prey.Zebra;

public class Lion extends Predator {

	// The age at which a Lion can start to breed.
	private static final int BREEDING_AGE = Field.FULL_DAY_LENGTH * 15;
	// The age to which a Lion can live.
	private static final int MAX_AGE = Field.FULL_DAY_LENGTH * 150;
	// The likelihood of a Lion breeding.
	private static final double BREEDING_PROBABILITY = 0.08;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 2;
	// The food value of a single prey. In effect, this is the
	// number of steps a Lion can go before it has to eat again.
	private static final int ANTELOPE_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.75);
	private static final int GIRAFFE_FOOD_VALUE = Field.FULL_DAY_LENGTH;
	private static final int ZEBRA_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.5);
	private static final double PROB_GETS_INFECTED = 0.07;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	public double getProbabilityGettingInfected() {
		return PROB_GETS_INFECTED;
	}
	
	public Lion(boolean randomAge, Field field, Location location)
	{
		super(field, location);
		if(randomAge) {
			age = rand.nextInt(MAX_AGE);
			foodLevel = rand.nextInt(ANTELOPE_FOOD_VALUE);
			this.isSick = rand.nextDouble() < 0.1;
			if(this.isSick) {
				this.stepsBeingSick = rand.nextInt(1000);
			}
		}
		else {
			age = 0;
			foodLevel = ANTELOPE_FOOD_VALUE;
			this.isSick = false;
		}
	}

	@Override
	public void act(List<FieldObject> newLions, int stepCount) {
		incrementAge(MAX_AGE);
		incrementHunger();
		incrementStepsSick();
		if(isAlive()) {
			giveBirth(newLions);
			if(!this.isSick) this.checkIfGetsInfected();
			// The above actions happen even though the Lion sleeps
			if(getField().isDayTime(stepCount)) return;
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
	 * Check whether or not this fox is to give birth at this step.
	 * New births will be made into free adjacent locations.
	 * @param newFoxes A list to return newly born foxes.
	 */
	private void giveBirth(List<FieldObject> newLions)
	{
		if(this.isMale) return;
		// New foxes are born into adjacent locations.
		// Get a list of adjacent free locations.
		Field field = getField();
		List<Location> free = field.getFreeAdjacentLocations(getLocation());
		int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
		for(int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Lion young = new Lion(false, field, loc);
			newLions.add(young);
		}
	}

	/**
	 * Look for rabbits adjacent to the current location.
	 * Only the first live rabbit is eaten.
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
			if(fieldObject instanceof Plant && !this.isSick && rand.nextDouble() < 0.08) {
				this.isSick = true;
			}
			if(fieldObject != null && fieldObject.isAlive() && fieldObject instanceof Prey && !(fieldObject instanceof Plant)) {
				fieldObject.setDead();
				foodLevel += this.getFoodValue(fieldObject);
				return where;
			}
		}
		return null;
	}

	private int getFoodValue(FieldObject fieldObject) {
		if(fieldObject instanceof Zebra) {
			return ZEBRA_FOOD_VALUE;
		} else if(fieldObject instanceof Antelope) {
			return ANTELOPE_FOOD_VALUE;
		} else if(fieldObject instanceof Giraffe) {
			return GIRAFFE_FOOD_VALUE;
		}
		throw new NoSuchElementException("Lions cannot eat " + fieldObject.getClass());
	}

}