package src.animal;

import src.Field;
import src.Location;

/**
 * A class which inherits from animal. Ground animals
 * are animals that don't go into water. They live on ground.
 */

public abstract class GroundAnimal extends Animal {
	
	public GroundAnimal(Field field, Location location) {
		super(field, location);
	}

	protected static final String FIELD_TYPE = "Ground";

}
