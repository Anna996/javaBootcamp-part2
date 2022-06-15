package storage_management_app.Runner;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import Utils.ConnectionToDB;
import storage_management_app.DbServices.ItemDbService;
import storage_management_app.DbServices.ItemLocationsDbService;
import storage_management_app.DbServices.LocationDbService;
import storage_management_app.models.Item;
import storage_management_app.models.Location;

public class Runner {
	private static ItemDbService itemDbService;
	private static LocationDbService locationDbService;
	private static ItemLocationsDbService itemLocationDbService;

	public static void main(String[] args) {

		try (Connection connection = new ConnectionToDB().connect()) {
			connection.setAutoCommit(false);
			System.out.println("Connected to database.");

			itemDbService = new ItemDbService();
			locationDbService = new LocationDbService();
			itemLocationDbService = new ItemLocationsDbService();

			handleItem(connection);
			handleLocation(connection);
			handleItemLocation(connection);

			handleLists(connection);

		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	// Item

	public static void handleItem(Connection connection) {
		addItem(connection);
		getItem(connection);
		updateItem(connection);
		deleteItem(connection);
	}

	public static void addItem(Connection connection) {
		Item item = new Item(null, "toy", 20, LocalDate.of(2022, 05, 12), 1);
		itemDbService.addItem(connection, item);
	}

	public static void getItem(Connection connection) {
		Item item = itemDbService.getItem(connection, 1001);
		System.out.println(item);
	}

	public static void updateItem(Connection connection) {
		Item item = itemDbService.getItem(connection, 1004);
		item.setName("car toy");
		item.setQuantity(5);

		Item updated = itemDbService.updateItem(connection, item);
		System.out.println(updated);
	}

	public static void deleteItem(Connection connection) {
		Item item = itemDbService.deleteItem(connection, 1005);
		System.out.println(item);
	}

	// Location

	public static void handleLocation(Connection connection) {
		addLocation(connection);
		getLocation(connection);
		updateLocation(connection);
		deleteLocation(connection);
	}

	public static void addLocation(Connection connection) {
		Location location = new Location(null, "migdal", "b");
		locationDbService.addLocation(connection, location);
	}

	public static void getLocation(Connection connection) {
		Location location = locationDbService.getLocation(connection, 101);
		System.out.println(location);
	}

	public static void updateLocation(Connection connection) {
		Location location = locationDbService.getLocation(connection, 101);
		location.setAccessCode("b");
		Location updated = locationDbService.updateLocation(connection, location);
		System.out.println(location);
	}

	public static void deleteLocation(Connection connection) {
		Location location = locationDbService.deleteLocation(connection, 100);
		System.out.println(location);
	}

	// ItemLocation

	public static void handleItemLocation(Connection connection) {
		addItemLLocation(connection);
		getItemLLocation(connection);
		updateItemLLocation(connection);
		deleteItemLLocation(connection);
	}

	public static void addItemLLocation(Connection connection) {
		Item item = itemDbService.getItem(connection, 1001);
		Location location = locationDbService.getLocation(connection, 102);
		itemLocationDbService.addItemLocation(connection, item, location);
	}

	public static void getItemLLocation(Connection connection) {
		List<Integer> locationsId = itemLocationDbService.getItemLocations(connection, 1001);
		System.out.println(locationsId);
	}

	public static void updateItemLLocation(Connection connection) {
		itemLocationDbService.updateItemLocation(connection, 1001, 102, 101);
	}

	public static void deleteItemLLocation(Connection connection) {
		itemLocationDbService.deleteItemLocation(connection, 1001, 103);
	}

	
	// using of batch:

	public static void handleLists(Connection connection) {
		addListOfItems(connection);
		addListOfLocations(connection);
		addListOfItemLocations(connection);
		
		updateListOfItems(connection);
		updateListOfLocations(connection);
		updateListOfItemLocations(connection);
	}

	public static void addListOfItems(Connection connection) {
		List<Item> items = Arrays.asList(
				new Item(null, "yellow desk", 400.50f, LocalDate.of(2022, 05, 26), 1),
				new Item(null, "lamp", 85, LocalDate.of(2022, 06, 8), 4),
				new Item(null, "televition", 3000.99f, LocalDate.of(2022, 02, 5), 20));
		
		List<Item> dbItems = itemDbService.addListOfItems(connection, items);
		if(dbItems != null ) dbItems.forEach(System.out::println);
	}

	public static void addListOfLocations(Connection connection) {
		List<Location> locations = Arrays.asList(
				new Location(null, "tel aviv", "t"), 
				new Location(null, "eilat", "l"),
				new Location(null, "holon", "h"));
		
		List<Location> dbLocations = locationDbService.addListOfLocations(connection, locations);
		if(dbLocations != null) dbLocations.forEach(System.out::println);
	}

	public static void addListOfItemLocations(Connection connection) {
		List<Item> items = Arrays.asList(
				itemDbService.getItem(connection, 1010),
				itemDbService.getItem(connection, 1011),
				itemDbService.getItem(connection, 1012));
		
		List<Location> locations = Arrays.asList(
				locationDbService.getLocation(connection, 104),
				locationDbService.getLocation(connection, 105),
				locationDbService.getLocation(connection, 106)
				);
				
		itemLocationDbService.addListOfItemLocations(connection, items, locations);
	}

	public static void updateListOfItems(Connection connection) {
		List<Item> items = Arrays.asList(
				itemDbService.getItem(connection, 1010),
				itemDbService.getItem(connection, 1011),
				itemDbService.getItem(connection, 1012));
		
		items.get(0).setQuantity(-2);
		items.get(1).setQuantity(30);
		items.get(2).setQuantity(20);
		
		List<Item> dbItems = itemDbService.updateListOfItems(connection, items);
		if(dbItems != null ) dbItems.forEach(System.out::println);
	}

	public static void updateListOfLocations(Connection connection) {
		List<Location> locations = Arrays.asList(
				locationDbService.getLocation(connection, 104),
				locationDbService.getLocation(connection, 105),
				locationDbService.getLocation(connection, 106)
				);
		
		locations.get(0).setAccessCode("w");
		locations.get(1).setAccessCode("o");
		locations.get(2).setAccessCode("n");
		
		List<Location> dbLocations = locationDbService.updateListOfLocations(connection, locations);
		if(dbLocations != null) dbLocations.forEach(System.out::println);
	}

	public static void updateListOfItemLocations(Connection connection) {
		List<Item> items = Arrays.asList(
				itemDbService.getItem(connection, 1010),
				itemDbService.getItem(connection, 1011),
				itemDbService.getItem(connection, 1012));
		
		List<Location> locationsFrom = Arrays.asList(
				locationDbService.getLocation(connection, 104),
				locationDbService.getLocation(connection, 105),
				locationDbService.getLocation(connection, 106)
				);
		
		List<Location> locationsTo = Arrays.asList(
				locationDbService.getLocation(connection, 105),
				locationDbService.getLocation(connection, 106),
				locationDbService.getLocation(connection, 104)
				);
				
		itemLocationDbService.updateListOfItemLocations(connection, items, locationsFrom, locationsTo);
	}
}
