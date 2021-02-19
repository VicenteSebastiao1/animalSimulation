package src.animal;

import src.Field;
import src.Location;

public abstract class FieldObject {
	
	// Whether the animal is alive or not.
    protected boolean alive;
    // The animal's field.
    protected Field field;
    // The animal's position in the field.
    protected Location location;
    
    public FieldObject(Field field, Location location) {
    	alive = true;
        this.field = field;
        setLocation(location);
    }
    
    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isAlive()
    {
        return alive;
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
