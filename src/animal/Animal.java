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
     * Check whether or not this fox is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newFoxes A list to return newly born foxes.
     */
}
