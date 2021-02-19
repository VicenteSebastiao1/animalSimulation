package src.animal.predators;

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
import src.animal.Predator;
import src.animal.Prey;
import src.animal.plants.Plant;
import src.animal.prey.Antelope;
import src.animal.prey.Giraffe;
import src.animal.prey.Zebra;

public class Crocodile extends Predator {
	// The age at which a Crocodile can start to breed.
    private static final int BREEDING_AGE = 30;
    // The age to which a Crocodile can live.
    private static final int MAX_AGE = 500;
    // The likelihood of a Crocodile breeding.
    private static final double BREEDING_PROBABILITY = 0.25;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 10;
    // The food value of a single rabbit. In effect, this is the
    // number of steps a Crocodile can go before it has to eat again.
    private static final int ANTELOPE_FOOD_VALUE = 2;
    private static final int GIRAFFE_FOOD_VALUE = 10;
    private static final int ZEBRA_FOOD_VALUE = 4;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();

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

	@Override
	public void act(List<FieldObject> newCrocodiles) {
		incrementAge(MAX_AGE);
        incrementHunger();
        incrementStepsSick();
        if(isAlive()) {
            giveBirth(newCrocodiles);            
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
    private void giveBirth(List<FieldObject> newCrocodiles)
    {
    	if(this.isMale) return;
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Crocodile young = new Crocodile(false, field, loc);
            newCrocodiles.add(young);
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
    	throw new NoSuchElementException("Crocodiles cannot eat " + fieldObject.getClass());
    }

}