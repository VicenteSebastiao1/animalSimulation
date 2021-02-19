package src.animal;
import java.util.List;
import java.util.Random;

import src.Field;
import src.Location;
import src.Randomizer;

/**
 * A class representing shared characteristics of animals.
 * 
 */
public abstract class Animal extends FieldObject
{
    //protected static final String FIELD_TYPE;
    protected final boolean isMale;
    protected int age;	
    protected boolean isSick;
    protected int stepsBeingSick = 0;
    public static final Random rand = Randomizer.getRandom();
    protected int foodLevel;
    protected static final int MAX_STEPS_SICK = 1000;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
    	super(field, location);
        this.isMale = rand.nextBoolean();
    }
    
    
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<Animal> newAnimals); 
    
    
    /**
     * Return the animal's field.
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }
    
    protected void incrementAge(Integer max_age)
    {
        age++;
        if(age > max_age) {
            setDead();
        }
    }
    
    protected void incrementStepsSick() {
    	if(this.isSick) {
        	this.stepsBeingSick++;
        	if(this.stepsBeingSick > MAX_STEPS_SICK) setDead();
        }
    }
    
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
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
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
}
