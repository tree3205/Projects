
public class Rectangle extends Shape
{
	private double width;
	private double height;

	public Rectangle() {
		this(0.0, 0.0);
	}

	public Rectangle(double width, double height) {
		super("Rectangle");
		this.width = width;
		this.height = height;
	}

	@Override
	public double area() {
		return width * height;
	}

	@Override
	public String toString() {
		return String.format("%5.2fw x %5.2fh", width, height);
	}

	public double width() {
		return width;
	}

	public double height() {
		return height;
	}
}

