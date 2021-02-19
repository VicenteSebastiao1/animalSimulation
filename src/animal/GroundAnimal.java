package src.animal;

import src.Field;
import src.Location;

public abstract class GroundAnimal extends Animal {
	
	public GroundAnimal(Field field, Location location) {
		super(field, location);
		// TODO Auto-generated constructor stub
	}

	protected static final String FIELD_TYPE = "Ground";

}
