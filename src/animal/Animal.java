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
    protected final boolean isMale; // males don't give birth.
    protected boolean isSick;
    protected int stepsBeingSick = 0;
    public static final Random rand = Randomizer.getRandom();
    protected int foodLevel; //how much food the anima has
    protected static final int MAX_STEPS_SICK = Field.FULL_DAY_LENGTH * 7;
    protected static final int MAX_FOOD = Field.FULL_DAY_LENGTH; //how much food an animal can eat before being full.
    
    public abstract double getProbabilityGettingInfected();
    
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
    
    public boolean isSick() {
    	return this.isSick;
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
     * If a random double is less than the probability of being infected, upon contact
     * with a sick animal, this animal will become sick too.
     */
    protected void checkIfGetsInfected() {
		Field field = getField();
		List<Location> free = field.getAdjacentLocations(getLocation());
		for (Location where : free) {
			FieldObject fieldObject = (FieldObject) field.getObjectAt(where);
			if(fieldObject instanceof Animal && ((Animal)fieldObject).isSick() && rand.nextDouble() < this.getProbabilityGettingInfected()) {
				this.isSick = true;
				return;
			}
		}

	}
}
