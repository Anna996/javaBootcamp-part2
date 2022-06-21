package ajbc.mongodb.exercise.models;

import java.util.Objects;

public class Pillow {

	private Shape shape;
	private Color color;

	public Pillow(Shape shape, Color color) {
		this.shape = shape;
		this.color = color;
	}

	public Pillow() {
	}

	public Shape getShape() {
		return shape;
	}

	public void setShape(Shape shape) {
		this.shape = shape;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public enum Shape {
		SQUARE, RECTANGLE
	}

	public enum Color {
		RED, GREEN, BLUE, YELLOW, PURPLE, BROWN
	}
}
