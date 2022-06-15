package storage_management_app.models;

import java.time.LocalDate;
import java.util.Objects;

public class Item {
	private Integer id;
	private String name;
	private float unitPrice;
	private LocalDate purchaceDate;
	private int quantity;
	private static int DEFAULT_UNIT_PRICE = 20;

	public Item(String name) {
		this(null, name, 0, null, 0);
	}

	public Item(Integer id, String name, float unitPrice, LocalDate purchaceDate, int quantity) {
		setID(id);
		setName(name);
		setUnitPrice(unitPrice);
		setPurchaceDate(purchaceDate);
		setQuantity(quantity);
	}

	public void setID(Integer id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUnitPrice(float unitPrice) {
		this.unitPrice = unitPrice > 0 ? unitPrice : DEFAULT_UNIT_PRICE;
	}

	public void setPurchaceDate(LocalDate purchaceDate) {
		this.purchaceDate = purchaceDate != null && purchaceDate.isBefore(LocalDate.now()) ? purchaceDate : null;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity >= 0 ? quantity : 0;
	}

	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public float getUnitPrice() {
		return unitPrice;
	}

	public LocalDate getPurchaceDate() {
		return purchaceDate;
	}

	public int getQuantity() {
		return quantity;
	}

	@Override
	public String toString() {
		return "Item [id=" + id + ", name=" + name + ", unitPrice=" + unitPrice + ", purchaceDate=" + purchaceDate
				+ ", quantity=" + quantity + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		return Objects.equals(id, other.id) && Objects.equals(name, other.name)
				&& Objects.equals(purchaceDate, other.purchaceDate) && quantity == other.quantity
				&& Float.floatToIntBits(unitPrice) == Float.floatToIntBits(other.unitPrice);
	}
}
