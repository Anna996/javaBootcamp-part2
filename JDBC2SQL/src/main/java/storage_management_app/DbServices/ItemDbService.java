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

public class ItemDbService {
	private static final String TABLE_NAME = "Item";

	public void addItem(Connection connection, Item item) {
		if (item == null) {
			System.err.println("Item is null");
			return;
		}

		try (Statement statement = connection.createStatement()) {
			String values = String.format("'%s', %f, '%s', %d", item.getName(), item.getUnitPrice(),
					item.getPurchaceDate(), item.getQuantity());

			String query = String.format("insert into %s values (%s)", TABLE_NAME, values);

			int rows = statement.executeUpdate(query);

			if (rows == 0) {
				throw new SQLException();
			}
			
			connection.commit();
			System.out.println("Item was added to database");
			
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

	public Item getItem(Connection connection, int id) {
		Item item = null;
		String query = "select * from " + TABLE_NAME + " where ItemId = " + id;

		try (Statement statement = connection.createStatement(); 
				ResultSet resultSet = statement.executeQuery(query)) {

			if (resultSet.next()) {
				int index = 2;
				String name = resultSet.getString(index++);
				float unitPrice = resultSet.getFloat(index++);
				LocalDate purchaceDate = resultSet.getDate(index++).toLocalDate();
				int quantity = resultSet.getInt(index++);

				item = new Item(id, name, unitPrice, purchaceDate, quantity);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return item;
	}

	public Item updateItem(Connection connection, Item item) {
		if (item == null) {
			System.err.println("item is null");
			return null;
		}

		Item fromDB = getItem(connection, item.getId());

		if (fromDB == null) {
			System.err.println("item does not exist in database");
			return null;
		}

		if (fromDB.equals(item)) {
			System.out.println("item is already updated in database");
		} else {
			try (Statement statement = connection.createStatement()) {
				String values = String.format("Name = '%s', UnitPrice = %f, PurchaseDate ='%s', Quantity = %d",
						item.getName(), item.getUnitPrice(), item.getPurchaceDate(), item.getQuantity());
				String query = String.format("update %s SET %s where ItemId = %d", TABLE_NAME, values, item.getId());

				statement.executeUpdate(query);
				connection.commit();
				System.out.println("item was updated in database");
				fromDB = getItem(connection, item.getId());

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

	public Item deleteItem(Connection connection, int id) {
		Item fromDB = getItem(connection, id);

		if (fromDB == null) {
			System.err.println("item does not exist in database");
			return null;
		}

		try (Statement statement = connection.createStatement()) {
			String query = String.format("delete from %s where ItemId = %d", TABLE_NAME, id);

			statement.executeUpdate(query);
			connection.commit();
			System.out.println("item was deleted from database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return fromDB;
	}

	public List<Item> addListOfItems(Connection connection, List<Item> items) {
		List<Item> dbItems = null;
		String query = String.format("insert into %s values (?, ?, ?, ?)", TABLE_NAME);

		try (PreparedStatement preStatement = connection.prepareStatement(query)) {
			
			for (Item item : items) {
				preStatement.setString(1, item.getName());
				preStatement.setFloat(2, item.getUnitPrice());
				preStatement.setString(3, item.getPurchaceDate().toString());
				preStatement.setInt(4, item.getQuantity());
				preStatement.addBatch();
			}

			int[] rows = preStatement.executeBatch();

			if(rows.length != items.size()) {
				throw new SQLException();
			}
			
			connection.commit();
			dbItems = getLastItems(connection, rows.length);
			System.out.println("Items were added to database");

		} catch (SQLException e) {
			System.err.println("Something went wrong...");
			System.err.println(e.getMessage());
			try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

		return dbItems;
	}

	private List<Item> getLastItems(Connection connection, int count) {
		List<Item> dbItems = null;
		String lastItems = String.format("select top(%d) * from %s order by ItemId desc", count, TABLE_NAME);

		try (Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(lastItems);) {

			dbItems = new ArrayList<Item>();
			while (resultSet.next()) {
				int index = 1;
				int id = resultSet.getInt(index++);
				String name = resultSet.getString(index++);
				float unitPrice = resultSet.getFloat(index++);
				LocalDate purchaceDate = resultSet.getDate(index++).toLocalDate();
				int quantity = resultSet.getInt(index++);

				dbItems.add(new Item(id, name, unitPrice, purchaceDate, quantity));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dbItems;
	}

	public List<Item> updateListOfItems(Connection connection, List<Item> items) {

		String values = "Name = ?, UnitPrice = ?, PurchaseDate = ?, Quantity = ?";
		String query = String.format("update %s SET %s where ItemId = ?", TABLE_NAME, values);

		try (PreparedStatement preStatement = connection.prepareStatement(query)) {
			Item fromDB = null;
			List<Item> updatedList = new ArrayList<Item>();

			for (Item item : items) {
				fromDB = getItem(connection, item.getId());

				if (fromDB.equals(item)) {
					System.out.println("item [" + item.getId() + "] is already updated in database");
				} else {
					preStatement.setString(1, item.getName());
					preStatement.setFloat(2, item.getUnitPrice());
					preStatement.setString(3, item.getPurchaceDate().toString());
					preStatement.setInt(4, item.getQuantity());
					preStatement.setInt(5, item.getId());
					preStatement.addBatch();
					updatedList.add(item);
				}
			}

			preStatement.executeBatch();
			connection.commit();
			System.out.println("Items were updated in database");
			return updatedList.stream().map(item -> getItem(connection, item.getId())).collect(Collectors.toList());

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
