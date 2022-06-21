package ajbc.mongodb.exercise.models;

import java.util.Objects;

import org.bson.types.ObjectId;

import com.google.gson.annotations.SerializedName;

public class Chair {

	@SerializedName("_id")
	private ObjectId id;
	@SerializedName("chair_id")
	private int chairId;
	private String manufacturer;
	@SerializedName("model_name")
	private String modelName;
	@SerializedName("is_stool")
	private boolean isStool;
	private float price;
	private Measurment measurment;

	public Chair(int chairId, String manufacturer, String modelName, boolean isStool, float price,
			Measurment measurment) {
		this.chairId = chairId;
		this.manufacturer = manufacturer;
		this.modelName = modelName;
		this.isStool = isStool;
		this.price = price;
		this.measurment = measurment;
	}

	public Chair() {
	}

	public int getChairId() {
		return chairId;
	}

	public void setChairId(int chairId) {
		this.chairId = chairId;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public boolean isStool() {
		return isStool;
	}

	public void setStool(boolean isStool) {
		this.isStool = isStool;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Measurment getMeasurment() {
		return measurment;
	}

	public void setMeasurment(Measurment measurment) {
		this.measurment = measurment;
	}

	@Override
	public String toString() {
		return "Chair [chairId=" + chairId + ", manufacturer=" + manufacturer + ", modelName=" + modelName
				+ ", isStool=" + isStool + ", price=" + price + ", measurment=" + measurment + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(chairId, isStool, manufacturer, measurment, modelName, price);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chair other = (Chair) obj;
		return chairId == other.chairId && isStool == other.isStool && Objects.equals(manufacturer, other.manufacturer)
				&& Objects.equals(measurment, other.measurment) && Objects.equals(modelName, other.modelName)
				&& Float.floatToIntBits(price) == Float.floatToIntBits(other.price);
	}
}
