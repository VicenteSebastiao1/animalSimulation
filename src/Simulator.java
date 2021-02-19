package src;
import java.util.Random;

import src.animal.Animal;
import src.animal.FieldObject;
import src.animal.Fox;
import src.animal.Rabbit;
import src.animal.plants.Plant;
import src.animal.predators.Lion;
import src.animal.prey.Antelope;
import src.animal.prey.Zebra;
import src.fieldType.Ground;
import src.fieldType.Water;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a rectangular field
 * containing rabbits and foxes.
 * 

 */
public class Simulator
{
    // Constants representing configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 120;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 80;
    // The probability that a fox will be created in any given grid position.
    private static final double LION_CREATION_PROBABILITY = 0.02;
    // The probability that a rabbit will be created in any given grid position.
    private static final double ANTELOPE_CREATION_PROBABILITY = 0.08;    

    // List of animals in the field.
    private List<FieldObject> animals;
    // The current state of the field.
    private Field field;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH);
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        
        animals = new ArrayList<>();
        field = new Field(depth, width);

        // Create a view of the state of each location in the field.
        view = new SimulatorView(depth, width);
        Color green = new Color(0, 255, 0);
        view.setColor(Antelope.class, Color.ORANGE);
        view.setColor(Lion.class, Color.BLUE);
        view.setColor(Zebra.class, Color.BLACK);
        view.setColor(Ground.class, Color.RED);
        view.setColor(Water.class, Color.CYAN);
        view.setColor(Plant.class, green);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * (4000 steps).
     */
    public void runLongSimulation()
    {
        simulate(5000);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     * @param numSteps The number of steps to run for.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            delay(6);   // uncomment this to run more slowly
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;

        // Provide space for newborn animals.
        List<FieldObject> newAnimals = new ArrayList<>();        
        // Let all rabbits act.
        for(Iterator<FieldObject> it = animals.iterator(); it.hasNext(); ) {
            FieldObject fieldObject = it.next();
            fieldObject.act(newAnimals);
            if(! fieldObject.isAlive()) {
                it.remove();
            }
        }
               
        // Add the newly born foxes and rabbits to the main lists.
        animals.addAll(newAnimals);

        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        populate();
        fillFloorTypes();
        // Show the starting state in the view.
        view.showStatus(step, field);
    }
    
    /**
     * Randomly populate the field with foxes and rabbits.
     */
    private void populate()
    {
        Random rand = Randomizer.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(rand.nextDouble() <= LION_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Lion lion = new Lion(true, field, location);
                    animals.add(lion);
                }
                else if(rand.nextDouble() <= ANTELOPE_CREATION_PROBABILITY) {
                    Location location = new Location(row, col);
                    Antelope antelope = new Antelope(true, field, location);
                    animals.add(antelope);
                } else if(rand.nextDouble() <= ANTELOPE_CREATION_PROBABILITY) {
                	Location location = new Location(row, col);
                    Zebra zebra = new Zebra(true, field, location);
                    animals.add(zebra);
                } else if(rand.nextDouble() <= ANTELOPE_CREATION_PROBABILITY) {
                	Location location = new Location(row, col);
                    Plant plant = new Plant(field, location);
                    animals.add(plant);
                }
                // else leave the location empty.
            }
        }
    }
    
    private void fillFloorTypes() {
    	Random rand = Randomizer.getRandom();
        //field.clear();
        Double waterProb = 0.1d;
        Water water = new Water();
        Ground ground = new Ground();
        Class previousType;
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
            	waterProb = this.getWaterProbability(row, col);
                if(rand.nextDouble() <= waterProb) {
                    Location location = new Location(row, col);
                    field.setFloorType(water, location);
                    previousType = water.getClass();
                }
                else {
                    Location location = new Location(row, col);
                    field.setFloorType(ground, location);
                    previousType = ground.getClass();
                }
                //waterProb = getWaterProbability(previousType);
                // else leave the location empty.
            }
        }
    }
    

	
	private double getWaterProbability(int row, int col) {
		if(row == 0 && col == 0) return 0.1;
		int waterCount = 0;
		int groundCount = 0;
		if(row == 0) {
			Object floorTypeObject = field.getFloorTypeAt(row, col - 1);
			if(floorTypeObject instanceof Water) {
				waterCount++;
			} else {
				groundCount++;
			}
		}
		for(int i = 0; i < 3; i++) {
			if(row > 0 && col + i - 1 > 0 && col + i - 1 < field.getWidth()) {
				Object floorTypeObject = field.getFloorTypeAt(row - 1, col + i - 1);
				if(floorTypeObject instanceof Water) {
					waterCount++;
				} else {
					groundCount++;
				}
			}
		}
		return 0.1 + 0.15 * waterCount - 0.01 * groundCount;
//		if(row == 0) {
//			Object floorTypeObject = field.getFloorTypeAt(row, col - 1);
//			if(floorTypeObject instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			return 0.1 + 0.1 * waterCount - 0.01 * groundCount;
//		}
//		if(col == 0) {
//			Object floorTypeObject2 = field.getFloorTypeAt(row - 1, col);
//			Object floorTypeObject1 = field.getFloorTypeAt(row - 1, col + 1);
//			if(floorTypeObject1 instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			if(floorTypeObject2 instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			return 0.1 + 0.1 * waterCount - 0.01 * groundCount;
//		}
//		if(col == field.getWidth() - 1) {
//			Object floorTypeObject1 = field.getFloorTypeAt(row - 1, col - 1);
//			Object floorTypeObject2 = field.getFloorTypeAt(row - 1, col);
//			Object floorTypeObject4 = field.getFloorTypeAt(row, col - 1);
//			if(floorTypeObject1 instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			if(floorTypeObject2 instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			if(floorTypeObject4 instanceof Water) {
//				waterCount++;
//			} else {
//				groundCount++;
//			}
//			return 0.1 + 0.1 * waterCount - 0.01 * groundCount;
//		}
//		Object floorTypeObject1 = field.getFloorTypeAt(row - 1, col - 1);
//		Object floorTypeObject2 = field.getFloorTypeAt(row - 1, col);
//		Object floorTypeObject3 = field.getFloorTypeAt(row - 1, col + 1);
//		Object floorTypeObject4 = field.getFloorTypeAt(row, col - 1);
//		if(floorTypeObject1 instanceof Water) {
//			waterCount++;
//		} else {
//			groundCount++;
//		}
//		if(floorTypeObject2 instanceof Water) {
//			waterCount++;
//		} else {
//			groundCount++;
//		}
//		if(floorTypeObject3 instanceof Water) {
//			waterCount++;
//		} else {
//			groundCount++;
//		}
//		if(floorTypeObject4 instanceof Water) {
//			waterCount++;
//		} else {
//			groundCount++;
//		}
//		return 0.1 + 0.1 * waterCount - 0.01 * groundCount;
	}
    
    private double getWaterProbability(Field field, int row, int col) {
    	// Define the probability between this four values. Check the possibility that
    	// you are at a border
    	field.getFloorTypeAt(row -  1, col);
    	field.getFloorTypeAt(row, col - 1);
    	field.getFloorTypeAt(row - 1, col - 1);
    	field.getFloorTypeAt(row - 1, col + 1);
    	return 0.5;
    }
    
    /**
     * Pause for a given time.
     * @param millisec  The time to pause for, in milliseconds
     */
    private void delay(int millisec)
    {
        try {
            Thread.sleep(millisec);
        }
        catch (InterruptedException ie) {
            // wake up
        }
    }
}
