import java.awt.*;
import javax.swing.*;

public class JGraph extends JComponent {
	/** graph field */
	private Graph<DisplayNode,DisplayEdge> graph;

	public JGraph() {
		graph = new Graph<DisplayNode,DisplayEdge>();
	}
	/**
     *  Paints a red circle forty pixels in diameter at each point.
     *
     *   g The graphics object to draw with
     */
    public void paintComponent(Graphics g) {
    	int nodeSize = graph.numNodes();
    	int edgeSize = graph.numEdges();
        // iterate through the edges and color them
        for (int i=0; i<edgeSize; i++) {
            Graph<DisplayNode,DisplayEdge>.Edge edge= graph.getEdge(i);
            DisplayNode n1 = edge.getHead().getData();
            DisplayNode n2 = edge.getTail().getData(); 
            Color col = edge.getData().getColor();
            Point p1 = n1.getPosition();
            Point p2 = n2.getPosition();
            int edge1X = (int)p1.getX();
            int edge1Y = (int)p1.getY();
            int edge2X = (int)p2.getX();
            int edge2Y = (int)p2.getY();
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(3));
            g2.setColor(col);
            g2.drawLine(edge1X, edge1Y, edge2X, edge2Y);
            int x = (edge1X + edge2X) / 2 + 10;
            int y = (edge1Y + edge2Y) / 2 + 10;
            g2.setColor(Color.black);
            g2.drawString(edge.getData().getLabel(),x,y);
        }

        // iterate through nodes and color them
    	for (int i=0; i<nodeSize; i++) {
    		Graph<DisplayNode,DisplayEdge>.Node node = graph.getNode(i);
    		DisplayNode data = node.getData();
    		Point p = data.getPosition();
    		int nodeX = (int)p.getX();
    		int nodeY = (int)p.getY();
    		g.setColor(data.getColor());
    		g.fillOval(nodeX-20, nodeY-20, 40, 40);
            g.setColor(Color.white);
            g.drawString(node.getData().getLabel(), nodeX-5, nodeY+5);
    	}
    }

    /** Accessor for graph */
    public Graph<DisplayNode, DisplayEdge> getGraph() {
        return graph;
    }

    /**
     *  The component will look bad if it is sized smaller than this
     *
     *  @return The minimum dimension
     */
    public Dimension getMinimumSize() {
        return new Dimension(700,500);
    }

    /**
     *  The component will look best at this size
     *
     *  @return The preferred dimension
     */
    public Dimension getPreferredSize() {
        return new Dimension(700,500);
    }
}