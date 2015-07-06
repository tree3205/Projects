
public class Square extends Rectangle
{
	public Square() {
		this(0.0);
	}

	public Square(double width) {
		super(width, width);
		shapeName = "Square";
	}
}
