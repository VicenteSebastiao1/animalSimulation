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
 * A class describing an zebra prey, one of the
 * actors in our simulation.
 */


public class Zebra extends Prey{
	// The age at which a Zebra can start to breed.
	private static final int BREEDING_AGE = Field.FULL_DAY_LENGTH * 1;
	// The age to which a Zebra can live.
	private static final int MAX_AGE = Field.FULL_DAY_LENGTH * 40;
	// The likelihood of a Zebra breeding.
	private static final double BREEDING_PROBABILITY = 0.10;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 1;
	// The food value of a single prey. In effect, this is the
	// number of steps a Zebra can go before it has to eat again.
	private static final int PLANT_FOOD_VALUE = (int) Math.floor(Field.FULL_DAY_LENGTH * 0.1); // 1440 * 0.001 = 1.4
	private static final int MAX_FOOD = Field.FULL_DAY_LENGTH;
	// Probability of getting sick on contact with sick animals.
	private static final double PROB_GETS_INFECTED = 0.002;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	public double getProbabilityGettingInfected() {
		return PROB_GETS_INFECTED;
	}

	public Zebra(boolean randomAge, Field field, Location location)
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

	@Override
	public void act(List<FieldObject> newZebras, int stepCount) {
		incrementAge(MAX_AGE);
		incrementHunger();
		incrementStepsSick();
		if(isAlive()) {
			giveBirth(newZebras);        
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
	 * Check whether or not this fox is to give birth at this step.
	 * New births will be made into free adjacent locations.
	 * @param newFoxes A list to return newly born foxes.
	 */
	private void giveBirth(List<FieldObject> newZebras)
	{
		if(this.isMale) return; //males don't giveBirth.
		Field field = getField();
		List<Location> adjacentLocations = field.adjacentLocations(getLocation());
		for (Location location : adjacentLocations) {
			FieldObject animal = (FieldObject) field.getObjectAt(location);
			if(animal != null && animal instanceof Zebra && ((Zebra)animal).isMale) {
				List<Location> free = field.getFreeGroundAdjacentLocations(getLocation());
				int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
				for(int b = 0; b < births && free.size() > 0; b++) {
					Location loc = free.remove(0);
					Zebra young = new Zebra(false, field, loc);
					newZebras.add(young);
				}
				return;
			}
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
			FieldObject fieldObject = (FieldObject) field.getPlantAt(where);
			FieldObject possibleAnimal = (FieldObject) field.getObjectAt(where);
			if(fieldObject instanceof Plant && !this.isSick && rand.nextDouble() < 0.08) {
				this.isSick = true;
			}
			if(fieldObject != null && fieldObject.isAlive() && fieldObject instanceof Plant && possibleAnimal == null) {
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
		throw new NoSuchElementException("Zebras cannot eat " + fieldObject.getClass());
	}

}