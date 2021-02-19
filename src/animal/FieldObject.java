package src.animal;

import java.util.List;
import java.util.Random;

import src.Field;
import src.Location;
import src.Randomizer;

public abstract class FieldObject {
	
	// Whether the animal is alive or not.
    protected boolean alive;
    // The animal's field.
    protected Field field;
    protected int age;	
    // The animal's position in the field.
    protected Location location;
    private static final Random rand = Randomizer.getRandom();
    
    public FieldObject(Field field, Location location) {
    	alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<FieldObject> newAnimals);
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
    }
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed(Integer breeding_age, double breeding_probability, int max_litter_size)
    {
        int births = 0;
        if(canBreed(breeding_age) && rand.nextDouble() <= breeding_probability) {
            births = rand.nextInt(max_litter_size) + 1;
        }
        return births;
    }
    

    
    /**
     * A fox can breed if it has reached the breeding age.
     */
    private boolean canBreed(Integer breeding_age)
    {
        return age >= breeding_age;
    }
    
    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Return the animal's location.
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }
	
	/**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    protected void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

}
