package src.animal.plants;

import java.util.List;

import src.Field;
import src.Location;
import src.animal.Animal;
import src.animal.FieldObject;
import src.animal.predators.Hippo;
import src.fieldType.Water;

public class Plant extends FieldObject{

	public Plant(Field field, Location location)
	{
		super(field, location);
		setLocation(location);
	}
	
	@Override
	protected void setLocation(Location newLocation)
    {
        location = newLocation;
        field.placePlant(this, newLocation);
    }

	@Override
	public void act(List<FieldObject> newPlants, int stepCount) {
		this.reproduce(newPlants);
	}
	
	@Override
	public void setDead()
    {
        alive = false;
        if(location != null) {
            field.clearPlant(location);
            location = null;
            field = null;
        }
    }

	public void reproduce(List<FieldObject> newPlants) {
		if(!isAlive()) return;
		Field field = getField();
		List<Location> free = field.getFreeFromPlantGroundAdjacentLocations(getLocation());
		double breedingProbability = 0.005;
		if(field.isWaterClose(this.location)) {
			breedingProbability += 0.1;
		}
		if(field.isRaining()) {
			breedingProbability += 0.001;
		}
		// TODO Don't reproduce plants on water
		int births = breed(0, breedingProbability, 5);
		for(int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Plant plant = new Plant(field, loc);
			newPlants.add(plant);
		}
	}
}