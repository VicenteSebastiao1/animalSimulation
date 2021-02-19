package src.animal.prey;

import static src.animal.Animal.rand;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

import src.Field;
import src.Location;
import src.Randomizer;
import src.animal.Animal;
import src.animal.FieldObject;
import src.animal.Prey;
import src.animal.plants.Plant;

public class Giraffe extends Prey{
	// The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 8;
    // The age to which a Zebra can live.
    private static final int MAX_AGE = 75;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single prey. In effect, this is the
    // number of steps a Giraffe can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 10;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
	

    public Giraffe(boolean randomAge, Field field, Location location)
    {
        super(field, location);
        if(randomAge) {
            age = rand.nextInt(MAX_AGE);
            foodLevel = rand.nextInt(PLANT_FOOD_VALUE);
            this.isSick = rand.nextDouble() < 0.1;
        }
        else {
            age = 0;
            foodLevel = PLANT_FOOD_VALUE;
            this.isSick = false;
        }
    }

	@Override
	public void act(List<Animal> newGiraffes) {
		incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newGiraffes);            
            // Move towards a source of food if found.
            Location newLocation = findFood();
            if(newLocation == null) { 
                // No food found - try to move to a free location.
                newLocation = getField().freeAdjacentLocation(getLocation());
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
    private void giveBirth(List<Animal> newGiraffes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Giraffe young = new Giraffe(false, field, loc);
            newGiraffes.add(young);
        }
    }
    
    /**
     * Look for rabbits adjacent to the current location.
     * Only the first live rabbit is eaten.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood()
    {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            FieldObject fieldObject = (FieldObject) field.getObjectAt(where);
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
    	throw new NoSuchElementException("Giraffe cannot eat " + fieldObject.getClass());
    }

}