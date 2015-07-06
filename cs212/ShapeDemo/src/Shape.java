/**
 * Simple abstract class to demonstrate inheritance.
 * @author Sophie Engle
 */
public abstract class Shape
{
	/** Stores the name of the shape. */
	protected String shapeName;

	/**
	 * Default constructor. Sets name of shape to "Shape".
	 */
	public Shape()
	{
		this("Shape");
	}

	/**
	 * Constructor. Requires name of shape.
	 * @param shapeName name of shape
	 */
	public Shape(String shapeName)
	{
		this.shapeName = shapeName;
	}

	/**
	 * Calculates and returns the area of the shape.
	 * @return area of shape
	 */
	public abstract double area();

	/**
	 * Returns the name (circle, square, etc.) of the shape.
	 * @return name of shape
	 */
	public String getType()
	{
		return this.shapeName;
	}

	/**
	 * Returns a <code>String</code> representation of this object.
	 * @return <code>String</code> representation of object
	 */
	@Override
	public String toString()
	{
		return getType();
	}
}
