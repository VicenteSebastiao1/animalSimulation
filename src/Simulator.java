package src;
import java.util.Random;

import src.animal.FieldObject;
import src.animal.plants.Plant;
import src.animal.predators.Crocodile;
import src.animal.predators.Hippo;
import src.animal.predators.Lion;
import src.animal.prey.Antelope;
import src.animal.prey.Giraffe;
import src.animal.prey.Zebra;
import src.fieldType.Ground;
import src.fieldType.Water;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * A predator-prey simulator, based on a rectangular field
 * containing plants and different animals.
 */
public class Simulator
{
	// Constants representing configuration information for the simulation.
	// The default width for the grid.
	private static final int DEFAULT_WIDTH = 120;
	// The default depth of the grid.
	private static final int DEFAULT_DEPTH = 80;

	//PREDATORS:
	// The probability that a Lion will be created in any given grid position.
	private static final double LION_CREATION_PROBABILITY = 0.02;
	// The probability that a Crocodile will be created in any given grid position.
	private static final double CROCODILE_CREATION_PROBABILITY = 0.015;
	// The probability that a Hippo will be created in any given grid position.
	private static final double HIPPO_CREATION_PROBABILITY = 0.015;

	//PREYS:
	// The probability that a Antelope will be created in any given grid position.
	private static final double ANTELOPE_CREATION_PROBABILITY = 0.01;
	// The probability that a Zebra will be created in any given grid position.
	private static final double ZEBRA_CREATION_PROBABILITY = 0.01;
	// The probability that a Giraffe will be created in any given grid position.
	private static final double GIRAFFE_CREATION_PROBABILITY = 0.01;





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

	public int getStep() {
		return this.step;
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
		Color crocgreen = new Color(74,67,0);
		// Assigning colors to the different animals:
		view.setColor(Antelope.class, Color.ORANGE);
		view.setColor(Giraffe.class, Color.YELLOW);
		view.setColor(Zebra.class, Color.BLACK);
		view.setColor(Lion.class, Color.BLUE);
		view.setColor(Hippo.class, Color.MAGENTA);
		view.setColor(Crocodile.class, crocgreen);
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
		simulate(40000);
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
//			delay(6);   // uncomment this to run more slowly
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
		field.randomizeRain();
		// Provide space for newborn animals.
		List<FieldObject> newAnimals = new ArrayList<>();        
		// Let all rabbits act.
		for(Iterator<FieldObject> it = animals.iterator(); it.hasNext(); ) {
			FieldObject fieldObject = it.next();
			fieldObject.act(newAnimals, step);
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
		field.clear();
		fillFloorTypes();
		populate();
		// Show the starting state in the view.
		view.showStatus(step, field);
	}

	/**
	 * Randomly populate the field with animals.
	 */
	private void populate() {
		Random rand = Randomizer.getRandom();
		for (int row = 0; row < field.getDepth(); row++) {
			for (int col = 0; col < field.getWidth(); col++) {
				if (field.getFloorTypeAt(row, col) instanceof Water) {
					if (rand.nextDouble() <= CROCODILE_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Crocodile crocodile = new Crocodile(true, field, location);
						animals.add(crocodile);

					} else if (rand.nextDouble() <= HIPPO_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Hippo hippo = new Hippo(true, field, location);
						animals.add(hippo);

					}
				} else {
					if (rand.nextDouble() <= LION_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Lion lion = new Lion(true, field, location);
						animals.add(lion);
					} else if (rand.nextDouble() <= ANTELOPE_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Antelope antelope = new Antelope(true, field, location);
						animals.add(antelope);
					} else if (rand.nextDouble() <= GIRAFFE_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Giraffe giraffe = new Giraffe(true, field, location);
						animals.add(giraffe);
					} else if (rand.nextDouble() <= ZEBRA_CREATION_PROBABILITY) {
						Location location = new Location(row, col);
						Zebra zebra = new Zebra(true, field, location);
						animals.add(zebra);						
					}
					Location location = new Location(row, col);
					Plant plant = new Plant(field, location);
					animals.add(plant);
				}
			}
		}
	}

	/**
	 * A method that spawns water or ground in the map,
	 * depending on which probability is greater. 
	 * 
	 */
	private void fillFloorTypes() {
		Random rand = Randomizer.getRandom();
		Double waterProb;
		Water water = new Water();
		Ground ground = new Ground();
		for(int row = 0; row < field.getDepth(); row++) {
			for(int col = 0; col < field.getWidth(); col++) {
				waterProb = this.getWaterProbability(row, col);
				if(rand.nextDouble() <= waterProb) {
					Location location = new Location(row, col);
					field.setFloorType(water, location);
				}
				else {
					Location location = new Location(row, col);
					field.setFloorType(ground, location);
				}
			}
		}
	}


	/**
	 * Checks the surrounding squares with each floor type and gives you the probability of the location given to be water,
	 * more water generates more water.
	 * @param row the row you want to check
	 * @param col the column you want to check
	 * @return a double between 0 and 1 with the probability of the current square of being water.
	 */
	private double getWaterProbability(int row, int col) {
		if(row == 0 && col == 0) return 0.01; // We set a low water probability to the top left corner.
		int waterCount = 0;    // counts the water squares nearby.
		int groundCount = 0;   // counts the ground squares nearby.
		if(col > 0) {
			Object floorTypeObject = field.getFloorTypeAt(row, col - 1);
			if(floorTypeObject instanceof Water) {
				waterCount++;
			} else {
				groundCount++;
			}
		}
		/**
		 * The first if condition makes sure don't go out of bounds.
		 * if the row is greater than 0, we check the 3 rows. we also
		 * check the columns. Example: If we are in the furtherst
		 * right column, if i = 2, then col + 2 - 1 would've been out of bounds. 
		 */		
		for(int i = 0; i < 3; i++) {  //checks from 0 to 2 providing the next 3 horizontal squares.
			if(row > 0 && col + i - 1 > 0 && col + i - 1 < field.getWidth()) { 
				Object floorTypeObject = field.getFloorTypeAt(row - 1, col + i - 1); //gets floor type for pevious row and column.
				if(floorTypeObject instanceof Water) {
					waterCount++;
				} else {
					groundCount++;
				}
			}
		}
		/*
		 * Math function to get water prob: For each step there is a 10% chance of a square being water.This prob is increased
		 * by 24% for every other surrounding water square that already exists factored with the number of water squares 
		 * around (waterCount).If there are ground squares around, there is a value corresponding to 0.04 factored with
		 * the number of ground squares around (groundCount) which decreases the probability. 
		 * 
		 */
		return 0.1 + 0.24 * waterCount - 0.04 * groundCount;
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
