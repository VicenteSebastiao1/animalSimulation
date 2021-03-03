package src;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import src.fieldType.Ground;
import src.fieldType.Water;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * 

 */
public class Field
{
    // A random number generator for providing random locations.
    private static final Random rand = Randomizer.getRandom();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][][] field;
    private boolean isRaining; 
    private int daysUntilStopsRaining;
    private double rainingProb = 0.2;
    //A day real day has 1440 min so 1440 steps is the value we chose to represent a day.
    public static final int FULL_DAY_LENGTH = 1440; 

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width][3];
    }
    
    /**
     * A method to seperate day and night (not equinox)
     * by counting steps 
     * @param Steps counted
     */
    public boolean isDayTime(int stepCount) {
    	int dayTimeConstant = (int) Math.floor(FULL_DAY_LENGTH * 0.65);
    	return (stepCount % FULL_DAY_LENGTH) < dayTimeConstant;
    }
    
    /**
     * Randomizes the creation of raim.
     */
    public void randomizeRain() {
    	if(this.isRaining) {
    		daysUntilStopsRaining--;
    		this.isRaining = daysUntilStopsRaining > 0;
    	} else {
    		this.isRaining = rand.nextDouble() < rainingProb;
    		if(this.isRaining) {
    			daysUntilStopsRaining = rand.nextInt(50);
    		}
    	}
    	
    }
    
    public boolean isRaining() {
    	return this.isRaining;
    }
    
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        clearAnimalsAndPlants();
        clearFloorTypes();
    }
    

    public void clearAnimalsAndPlants()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col][1] = null;
                field[row][col][2] = null;
            }
        }
    }
    
    public void clearFloorTypes()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col][0] = null;
            }
        }
    }
    
    /**
     * Clear the given location.
     * @param location The location to clear.
     */
    public void clearAnimal(Location location)
    {
        field[location.getRow()][location.getCol()][1] = null;
    }
    
    public void clearPlant(Location location)
    {
        field[location.getRow()][location.getCol()][2] = null;
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }
    
    /**
     * Places a floor type on an index of the Grid.
     * @param Ground or Water type.
     * @param row Coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void setFloorType(Object floorType, int row, int col) {
    	setFloorType(floorType, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
    	field[location.getRow()][location.getCol()][1] = animal;
    }
    
    /**
     * Place a plant at the given location.
     * @param plant The plant to be placed.
     * @param location Where to place the plant.
     */
    
    public void placePlant(Object plant, Location location)
    {
    	field[location.getRow()][location.getCol()][2] = plant;
    }
    
    public void setFloorType(Object floorType, Location location)
    {
        field[location.getRow()][location.getCol()][0] = floorType;
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the plant at the given location, if any.
     * @param location Where in the field.
     * @return The plant at the given location, or null if there is none.
     */
    public Object getPlantAt(Location location)
    {
        return getPlantAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col][1];
    }
    
    /**
     * Return the plant at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The plant at the given location, or null if there is none.
     */
    public Object getPlantAt(int row, int col)
    {
        return field[row][col][2];
    }
    
    /**
     * Return the Floor Type at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The floorType at the given location, or null if there is none.
     */
    public Object getFloorTypeAt(Location location)
    {
        return getFloorTypeAt(location.getRow(), location.getCol());
    }
    
    
    public Object getFloorTypeAt(int row, int col)
    {
        return field[row][col][0];
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location randomAdjacentLocation(Location location)
    {
        List<Location> adjacent = adjacentLocations(location);
        return adjacent.get(0);
    }
    
    /**
     * Get a shuffled list of the free adjacent locations.
     * @param location Get locations adjacent to this.
     * @return A list of free adjacent locations.
     */
    public List<Location> getFreeAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Get a shuffled list of the free water type adjacent locations.
     * @param location Gets water type locations adjacent to this.
     * @return A list of free adjacent water locations.
     */
    public List<Location> getFreeWaterAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null && getFloorTypeAt(next) instanceof Water) {
                free.add(next);
            }
        }
        return free;
    }
    
    /**
     * Get a shuffled list of the free ground type adjacent locations.
     * @param location Gets ground type locations adjacent to this.
     * @return A list of free adjacent ground locations.
     */
    public List<Location> getFreeGroundAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getObjectAt(next) == null && getFloorTypeAt(next) instanceof Ground) {
                free.add(next);
            }
        }
        return free;
    }
    
    
    /**
     * Get a shuffled list of the free from plant covered ground type adjacent locations.
     * @param location Gets free from plant covered ground type locations adjacent to this.
     * @return A list of free adjacent free from plant covered ground locations.
     */
    public List<Location> getFreeFromPlantGroundAdjacentLocations(Location location)
    {
        List<Location> free = new LinkedList<>();
        List<Location> adjacent = adjacentLocations(location);
        for(Location next : adjacent) {
            if(getPlantAt(next) == null && getFloorTypeAt(next) instanceof Ground) {
                free.add(next);
            }
        }
        return free;
    }
    
    public boolean isWaterClose(Location location) {
    	return isWaterClose(location.getRow(), location.getCol());
    }
    
    /**
     * 
     * @param row The desired row.
     * @param col The desired column.
     * @return true if there is water close
     */
    public boolean isWaterClose(int row, int col) {
    	int length = 3;     //we defined water being close to a square if its a maximum 3 length away.
    	for (int i = 0; i < 2*length; i++) { // makes sure we  check both ways not just to the right.
    		int rowAux = row - length + i; //rowAux is the square we want to check if its water, if yes then its already true.
    		//The if condition below checks that we neither check above the first row or beneath lowest row. (out of map).
    		if(rowAux < 0 || rowAux >= this.getDepth() - 1) continue; 
			for (int j = 0; j < 2*length; j++) { //if we have a row that's in the map we go through the columns.
				int colAux = col - length + j;   
				if(colAux < 0 || colAux >= this.getWidth() - 1) continue; //checks that we are in bounds.
				Object fieldType = getFloorTypeAt(rowAux, colAux);
				if(fieldType instanceof Water) return true;  //if we found water, then returns true.
			}
		}
    	return false;
    }
    
    /**
     * Try to find a free location that is adjacent to the
     * given location. If there is none, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area.
     */
    public Location freeAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }
    
    /**
     * @param location The free ground location
     * @return a location of ground if it's free or null if there is none
     */
    public Location freeGroundAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeGroundAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }
    
    /**
     * @param location on grid.
     * @return a location of water if it's free or null if there is none
     */
    public Location freeWaterAdjacentLocation(Location location)
    {
        // The available free ones.
        List<Location> free = getFreeWaterAdjacentLocations(location);
        if(free.size() > 0) {
            return free.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Return a shuffled list of locations adjacent to the given one.
     * The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return A list of locations adjacent to that given.
     */
    public List<Location> adjacentLocations(Location location)
    {
        assert location != null : "Null location passed to adjacentLocations";
        // The list of locations to be returned.
        List<Location> locations = new LinkedList<>();
        if(location != null) {
            int row = location.getRow();
            int col = location.getCol();
            for(int roffset = -1; roffset <= 1; roffset++) {
                int nextRow = row + roffset;
                if(nextRow >= 0 && nextRow < depth) {
                    for(int coffset = -1; coffset <= 1; coffset++) {
                        int nextCol = col + coffset;
                        // Exclude invalid locations and the original location.
                        if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                            locations.add(new Location(nextRow, nextCol));
                        }
                    }
                }
            }
            
            // Shuffle the list. Several other methods rely on the list
            // being in a random order.
            Collections.shuffle(locations, rand);
        }
        return locations;
    }
    
    public List<Location> getAdjacentLocations(Location location) {
    	return adjacentLocations(location);
    }

    /**
     * Return the depth of the field.
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * Return the width of the field.
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
