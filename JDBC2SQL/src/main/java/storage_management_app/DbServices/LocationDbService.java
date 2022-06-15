package storage_management_app.DbServices;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import storage_management_app.models.Item;
import storage_management_app.models.Location;

public class LocationDbService {
	private static final String TABLE_NAME = "Location";

	public void addLocation(Connection connection, Location location) {
		try (Statement statement = connection.createStatement()) {
			String values = String.format("'%s', '%s'", location.getName(), location.getAccessCode());
			String query = String.format("insert into %s values(%s)", TABLE_NAME, values);

			int rows = statement.executeUpdate(query);
			
			if (rows == 0) {
				throw new SQLException();
			}
			
			connection.commit();
			System.out.println("Location was added to database");

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

	public Location getLocation(Connection connection, int id) {
		Location location = null;
		String query = String.format("select * from %s where LocationId = %d", TABLE_NAME, id);
		
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query);) {

			if (resultSet.next()) {
				int index = 2;
				String name = resultSet.getString(index++);
				String accessCode = resultSet.getString(index);
				location = new Location(id, name, accessCode);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (location == null) {
			System.err.println("location [" + id + "] does not exist in database");
		}

		return location;
	}

	public Location updateLocation(Connection connection, Location location) {
		if (location == null) {
			return null;
		}

		Location fromDB = getLocation(connection, location.getId());

		if (fromDB == null) {
			System.err.println("location does not exist in database");
			return null;
		}

		if (fromDB.equals(location)) {
			System.err.println("location is already updated in database");
		} else {
			try (Statement statement = connection.createStatement()) {
				String values = String.format("Name = '%s', AccessCode = '%s'", location.getName(),
						location.getAccessCode());
				String query = String.format("update %s SET %s where LocationId = %d", TABLE_NAME, values,
						location.getId());

				statement.executeUpdate(query);
				connection.commit();
				System.out.println("location was updated in database");
				fromDB = getLocation(connection, location.getId());

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

		return fromDB;
	}

	public Location deleteLocation(Connection connection, int id) {
		Location fromDB = getLocation(connection, id);

		if (fromDB != null) {

			try (Statement statement = connection.createStatement()) {
				String query = String.format("delete from %s where LocationId = %d", TABLE_NAME, id);

				statement.executeUpdate(query);
				connection.commit();
				System.out.println("location was deleted from database");

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

		return fromDB;
	}

	public List<Location> addListOfLocations(Connection connection, List<Location> locations) {
		List<Location> dbLocations = null;
		String query = String.format("insert into %s values (?, ?)", TABLE_NAME);

		try (PreparedStatement preStatement = connection.prepareStatement(query)) {

			for (Location location : locations) {
				preStatement.setString(1, location.getName());
				preStatement.setString(2, location.getAccessCode());
				preStatement.addBatch();
			}

			int[] rows = preStatement.executeBatch();

			connection.commit();
			System.out.println("Locations were added to database");
			dbLocations = getLastLocations(connection, rows.length);

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return dbLocations;
	}

	private List<Location> getLastLocations(Connection connection, int count) {
		List<Location> dbLocations = null;
		String lastItems = String.format("select top(%d) * from %s order by LocationId desc", count, TABLE_NAME);
		
		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(lastItems);) {

			dbLocations = new ArrayList<Location>();
			while (resultSet.next()) {
				int index = 1;
				int id = resultSet.getInt(index++);
				String name = resultSet.getString(index++);
				String accessCode = resultSet.getString(index);

				dbLocations.add(new Location(id, name, accessCode));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbLocations;
	}

	public List<Location> updateListOfLocations(Connection connection, List<Location> locations) {
		String values = "Name = ?, AccessCode = ?";
		String query = String.format("update %s SET %s where LocationId = ?", TABLE_NAME, values);

	
		try (PreparedStatement preStatement = connection.prepareStatement(query)) {
			Location fromDB = null;
			List<Location> listToUpdate = new ArrayList<Location>();

			for (Location location : locations) {
				fromDB = getLocation(connection, location.getId());

				if (fromDB.equals(location)) {
					System.out.println("location [" + location.getId() + "] is already updated in database");
				} else {
					preStatement.setString(1, location.getName());
					preStatement.setString(2, location.getAccessCode());
					preStatement.setInt(3, location.getId());
					preStatement.addBatch();
					listToUpdate.add(location);
				}
			}

			preStatement.executeBatch();
			connection.commit();
			System.out.println("locations were updated in database");
			return listToUpdate.stream().map(location -> getLocation(connection, location.getId())).collect(Collectors.toList());

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return null;
	}
}
