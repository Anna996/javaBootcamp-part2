package ajbc.mongodb.exercise.runner;

import java.util.Arrays;
import java.util.List;


import ajbc.mongodb.exercise.dao.ChairsDao;
import ajbc.mongodb.exercise.models.Chair;
import ajbc.mongodb.exercise.models.Measurment;
import ajbc.mongodb.exercise.models.Pillow;
import ajbc.mongodb.exercise.models.Pillow.Color;
import ajbc.mongodb.exercise.models.Pillow.Shape;

public class Runner {

	private static ChairsDao chairsDao;

	public static void main(String[] args) {

		chairsDao = new ChairsDao();
		
		addChair();
		addChairs();
		
		getChairById();
		getStools();
		getChairsByManufacurer();
		getChairsInPriceRange();
		getChairsByManufacurers();
		getChairsShorterThen();
		
		updateChair();
		updateChairAndGetBeforeUpdate();
		updateChairList();
		updateWithPillow();
		
		deleteChairById();
		deleteChairsByManufacurer();
		deleteChairsGteHeight();
		deleteCollection();
		
		chairsDao.close();
	}

	// create:
	
	public static void addChair() {
		Chair chair = new Chair(1000, "manufacturer A", "model B", true, 85.5f, new Measurment(100, 1.5, 0.8, 0.7));

		chairsDao.addChair(chair);
	}

	public static void addChairs() {
		List<Chair> chairs = Arrays.asList(
				new Chair(1001, "manufacturer A", "model C", true, 105, new Measurment(101, 1.7, 0.8, 0.8)),
				new Chair(1002, "manufacturer B", "model B", false, 98.99f, new Measurment(102, 1.5, 0.8, 1)),
				new Chair(1003, "manufacturer B", "model C", false, 125f, new Measurment(103, 1.8, 1.2, 0.7)));

		chairsDao.addChairs(chairs);
	}

	// read: 
	
	private static void printChairs(List<Chair> chairs) {
		chairs.forEach(System.out::println);
	}

	public static void getChairById() {
		Chair chair = chairsDao.getChairById(1000);
		System.out.println(chair);
	}

	public static void getStools() {
		System.out.println("Stools:");
		printChairs(chairsDao.getStools());
	}

	public static void getChairsByManufacurer() {
		String manufacturer = "manufacturer B";
		System.out.println("Chairs with manufacturer: " + manufacturer);
		printChairs(chairsDao.getChairsByManufacurer(manufacturer));
	}

	public static void getChairsInPriceRange() {
		int min = 100, max = 125;

		System.out.println("Chairs In Price Range between %d and %d".formatted(min, max));
		printChairs(chairsDao.getChairsInPriceRange(min, max));
	}

	public static void getChairsByManufacurers() {
		List<String> manufacturers = Arrays.asList("manufacturer B","manufacturer C");
		
		System.out.println("Chairs with manufacturers: %s".formatted(manufacturers.toString()));
		printChairs(chairsDao.getChairsByManufacurers(manufacturers));
	}

	public static void getChairsShorterThen() {
		double height = 1.7;
		
		System.out.println("Chairs under the height " + height);
		printChairs(chairsDao.getChairsShorterThen(height));
	}

	// update:
	
	public static void updateChair() {
		Chair chair = chairsDao.getChairById(1000);
		chair.setModelName("model X");
		Chair updated = chairsDao.update(chair);
		System.out.println(updated);
	}
	
	public static void updateChairAndGetBeforeUpdate() {
		Chair chair = chairsDao.getChairById(1000);
		chair.setModelName("model B");
		Chair oldVersion = chairsDao.updateAndGetBeforeUpdate(chair);
		System.out.println("before update: " + oldVersion);
	}
	
	public static void updateChairList() {
		List<Chair> chairs = Arrays.asList(
				chairsDao.getChairById(1000),
				chairsDao.getChairById(1001),
				chairsDao.getChairById(1002));
		
		chairs.get(0).setPrice(190);
		chairs.get(1).setPrice(190);
		chairs.get(2).setPrice(190);
		
		System.out.println("Updated list:");
		printChairs(chairsDao.update(chairs));
	}

	public static void updateWithPillow() {
		System.out.println("Update with pillow:");
		System.out.println(chairsDao.updateWithPillow(chairsDao.getChairById(1004), new Pillow(Shape.SQUARE, Color.RED)));
	}
	
	// delete:
	
	public static void deleteChairById() {
		Chair deleted = chairsDao.deleteChairById(1003);
		System.out.println(deleted);
	}
	
	public static void deleteChairsByManufacurer() {
		chairsDao.deleteChairsByManufacurer("manufacturer B");
	}
	
	public static void deleteChairsGteHeight() {
		chairsDao.deleteChairsGteHeight(1.5);
	}
	
	public static void deleteCollection() {
		chairsDao.deleteCollection();
	}
}
