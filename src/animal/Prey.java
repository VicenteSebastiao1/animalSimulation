package src.animal;

import src.Field;
import src.Location;

/**
 * A class which inherits from Animal. Prey animals
 * that eat plants and are hunted by prey.
 */

public abstract class Prey extends Animal {
	
	public Prey	(Field field, Location location)
    {
		super(field, location);
    }

}
