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

public class Zebra extends Prey{
// The age at which a Zebra can start to breed.
    private static final int BREEDING_AGE = 1;
    // The age to which a Zebra can live.
    private static final int MAX_AGE = 50;
    // The likelihood of a Zebra breeding.
    private static final double BREEDING_PROBABILITY = 0.08;
    // The maximum number of births.
    private static final int MAX_LITTER_SIZE = 1;
    // The food value of a single prey. In effect, this is the
    // number of steps a Zebra can go before it has to eat again.
    private static final int PLANT_FOOD_VALUE = 5;
    // Probability of getting sick on contact with sick animals.
    private static final double PROB_GETS_INFECTED = 0.8;
    // A shared random number generator to control breeding.
    private static final Random rand = Randomizer.getRandom();


    public Zebra(boolean randomAge, Field field, Location location)
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
public void act(List<FieldObject> newZebras) {
incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            giveBirth(newZebras);            
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

private void checkIfGetsInfected() {
    Field field = getField();
    List<Location> free = field.getFreeAdjacentLocations(getLocation());
    for (Location where : free) {
        FieldObject fieldObject = (FieldObject) field.getObjectAt(where);
        if(fieldObject instanceof Animal && ((Animal)fieldObject).isSick() && rand.nextDouble() < PROB_GETS_INFECTED) {
        this.isSick = true;
        return;
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
        // New foxes are born into adjacent locations.
        // Get a list of adjacent free locations.
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed(BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            Antelope young = new Antelope(false, field, loc);
            newZebras.add(young);
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
    throw new NoSuchElementException("Zebras cannot eat " + fieldObject.getClass());
    }

}