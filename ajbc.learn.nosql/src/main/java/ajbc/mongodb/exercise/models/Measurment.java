package ajbc.mongodb.exercise.models;

import java.util.Objects;


import com.google.gson.annotations.SerializedName;

public class Measurment {

	@SerializedName("measurment_id")
	private int measurmentId;
	private double height;
	private double width;
	private double depth;

	public Measurment(int measurmentId, double height, double width, double depth) {
		this.measurmentId = measurmentId;
		this.height = height;
		this.width = width;
		this.depth = depth;
	}

	public Measurment() {
	}

	public int getMeasurmentId() {
		return measurmentId;
	}

	public void setMeasurmentId(int measurmentId) {
		this.measurmentId = measurmentId;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public double getDepth() {
		return depth;
	}

	public void setDepth(double depth) {
		this.depth = depth;
	}

	@Override
	public String toString() {
		return "Measurment [measurmentId=" + measurmentId + ", height=" + height + ", width=" + width + ", depth="
				+ depth + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(depth, height, measurmentId, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Measurment other = (Measurment) obj;
		return Double.doubleToLongBits(depth) == Double.doubleToLongBits(other.depth)
				&& Double.doubleToLongBits(height) == Double.doubleToLongBits(other.height)
				&& measurmentId == other.measurmentId
				&& Double.doubleToLongBits(width) == Double.doubleToLongBits(other.width);
	}
}
