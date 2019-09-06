import java.awt.*;
import javax.swing.*;     

public class DisplayNode {
	/** position */
	Point position;

	/** label */
	String label;

	/** color */
	Color color;

	public DisplayNode(Point position, String label, Color color) {
		this.position = position;
		this.label = label;
		this.color = color;
	} 

	public Point getPosition() {
		return position;
	}

	public String getLabel() {
		return label;
	}

	public Color getColor() {
		return color;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}