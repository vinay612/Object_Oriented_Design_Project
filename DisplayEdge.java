import java.awt.*;
import javax.swing.*;

public class DisplayEdge {
	/** label */
	String label;

	/** color */
	Color color;

	/** weight */
	int weight;

	public DisplayEdge(String label, Color color, int weight) {
		this.label = label;
		this.color = color;
		this.weight = weight;
	}

	public String getLabel() {
		return label;
	}

	public Color getColor() {
		return color;
	}

	public int getWeight() {
		return weight;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}
}