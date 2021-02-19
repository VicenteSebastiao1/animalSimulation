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
import src.animal.predators.Lion;

public class Antelope extends Prey {
	
	// The age at which a Antelope can start to breed.
    private static final int BREEDING_AGE = 5;
    // The age to which a Antelope can live.
    private static final int MAX_AGE = 40;
    // The likelihood of a Antelope breeding.
    private static final double BREEDING_PROBABILITY = 0.15;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 2;
    // The food value of a single prey. In effect, this is the
    // number of steps an Antelope can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 6;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();
	

    public Antelope(boolean randomAge, Field field, Location location)
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
	public void act(List<Animal> newAntelopes) {
		incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newAntelopes);            
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
    private void giveBirth(List<Animal> newFoxes)
    {
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Antelope young = new Antelope(false, field, loc);
            newFoxes.add(young);
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
    	throw new NoSuchElementException("Antelopes cannot eat " + fieldObject.getClass());
    }

}