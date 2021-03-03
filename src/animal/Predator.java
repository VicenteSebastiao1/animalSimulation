package src.animal;

import src.Field;
import src.Location;

/**
 * A class which inherits from Animal. Predator animals
 * are animals that eat prey.
 */

public abstract class Predator extends Animal {
	
	public Predator	(Field field, Location location)
    {
		super(field, location);
    }

}

