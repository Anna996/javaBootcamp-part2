package storage_management_app.DbServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import storage_management_app.models.Item;
import storage_management_app.models.Location;

public class ItemLocationsDbService {
	private static ItemDbService itemDbService;
	private static LocationDbService locationDbService;
	private static final String TABLE_NAME = "ItemLocation";

	static {
		itemDbService = new ItemDbService();
		locationDbService = new LocationDbService();
	}

	public void addItemLocation(Connection connection, Item item, Location location) {

		if (item == null || location == null) {
			System.err.println("item and location can't be null");
			return;
		}

		Item itemDB = itemDbService.getItem(connection, item.getId());
		Location locationDB = locationDbService.getLocation(connection, location.getId());

		if (itemDB == null || locationDB == null) {
			return;
		}

		try (Statement statement = connection.createStatement()) {
			String values = String.format("%d , %d", item.getId(), location.getId());
			String query = String.format("insert into %s values(%s)", TABLE_NAME, values);

			int rows = statement.executeUpdate(query);
			if (rows == 0) {
				throw new SQLException();
			}
			
			connection.commit();
			System.out.println("ItemLocation was added to database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public List<Integer> getItemLocations(Connection connection, int itemId) {
		List<Integer> locationsId = new ArrayList<Integer>();
		String query = String.format("select LocationId from %s where ItemId = %d", TABLE_NAME, itemId);

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);) {

			while (resultSet.next()) {
				locationsId.add(resultSet.getInt(1));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (locationsId.isEmpty()) {
			System.err.println("ItemLocation [itemId: %d] does not exist in database".formatted(itemId));
		}

		return locationsId;
	}

	public void updateItemLocation(Connection connection, int itemId, int fromLocationId, int toLocationId) {
		List<Integer> locationsId = getItemLocations(connection, itemId);

		if (!locationsId.contains(fromLocationId)) {
			System.err.println("ItemLocation [itemId: %d , locationId: %d] does not exist in database".formatted(itemId,
					fromLocationId));
		}

		for (Integer id : locationsId) {
			if (id.equals(toLocationId)) {
				System.err.println("ItemLocation [itemId: %d , locationId: %d] already exists in database"
						.formatted(itemId, toLocationId));
				return;
			}
		}

		Location location = locationDbService.getLocation(connection, toLocationId);
		if (location == null) {
			return;
		}

		try (Statement statement = connection.createStatement()) {
			String query = String.format("update %s SET LocationId = %d where ItemId = %d AND LocationId = %d",
					TABLE_NAME, toLocationId, itemId, fromLocationId);

			statement.executeUpdate(query);
			connection.commit();
			System.out.println("ItemLocation was updated in database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void deleteItemLocation(Connection connection, int itemId, int locationId) {
		List<Integer> locationsId = getItemLocations(connection, itemId);

		if (locationsId.isEmpty()) {
			return;
		}

		if (!locationsId.contains(locationId)) {
			System.err.println("ItemLocation [itemId: %d , locationId: %d] does not exist in database".formatted(itemId,
					locationId));
			return;
		}

		try (Statement statement = connection.createStatement()) {
			String query = String.format("delete from %s where ItemId = %d AND LocationId = %d", TABLE_NAME, itemId,
					locationId);

			statement.executeUpdate(query);
			connection.commit();
			System.out.println("ItemLocation was deleted from database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void addListOfItemLocations(Connection connection, List<Item> items, List<Location> locations) {
		if (items.size() != locations.size()) {
			System.err.println("items and locations lists must be the same size");
			return;
		}

		String query = "insert into %s values(?, ?)".formatted(TABLE_NAME);

		try (PreparedStatement prepStatement = connection.prepareStatement(query)) {
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				Location location = locations.get(i);

				prepStatement.setInt(1, item.getId());
				prepStatement.setInt(2, location.getId());
				prepStatement.addBatch();
			}

			prepStatement.executeBatch();
			connection.commit();
			System.out.println("ItemLocations were added to database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}

	public void updateListOfItemLocations(Connection connection, List<Item> items, List<Location> locationsFrom,
			List<Location> locationsTo) {

		if (items.size() != locationsFrom.size() && items.size() != locationsTo.size()) {
			System.err.println("items and locations lists must be the same size");
			return;
		}

		String query = String.format("update %s SET LocationId = ? where ItemId = ? AND LocationId = ?", TABLE_NAME);

		try (PreparedStatement prepStatement = connection.prepareStatement(query)) {
			for (int i = 0; i < items.size(); i++) {
				Item item = items.get(i);
				Location locationFrom = locationsFrom.get(i);
				Location locationTo = locationsTo.get(i);

				prepStatement.setInt(1, locationTo.getId());
				prepStatement.setInt(2, item.getId());
				prepStatement.setInt(3, locationFrom.getId());
				prepStatement.addBatch();
			}

			prepStatement.executeBatch();
			connection.commit();
			System.out.println("ItemLocations were updated in database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
}
