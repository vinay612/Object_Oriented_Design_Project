import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;        

public class GraphGUI {
    /** The graph to be displayed */
    private JGraph graph;

    /** Label for the input mode instructions */
    private JLabel instr;

    /** The input mode */
    InputMode mode = InputMode.ADD_NODES;

    /** Remembers point where last mousedown event occurred */
    Point pointUnderMouse;

    /** Remembers position of head node */
    Point headUnderMouse = null;

    /** Remembers position of tail node */
    Point tailUnderMouse = null;

    /**
     *  Schedules a job for the event-dispatching thread
     *  creating and showing this application's GUI.
     */
    public static void main(String[] args) {
        final GraphGUI GUI = new GraphGUI();
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    GUI.createAndShowGUI();
                }
            });
    }

    /** Sets up the GUI window */
    public void createAndShowGUI() {
        // Make sure we have nice window decorations.
        JFrame.setDefaultLookAndFeelDecorated(true);

        // Create and set up the window.
        JFrame frame = new JFrame("Graph GUI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add components
        createComponents(frame);

        // Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    /** Puts content in the GUI window */
    public void createComponents(JFrame frame) {
        // graph display
        Container pane = frame.getContentPane();
        pane.setLayout(new FlowLayout());
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        graph = new JGraph();
        PointMouseListener pml = new PointMouseListener();
        graph.addMouseListener(pml);
        graph.addMouseMotionListener(pml);
        panel1.add(graph);
        instr = new JLabel("Click to add new nodes , drag to move.");
        panel1.add(instr,BorderLayout.NORTH);
        pane.add(panel1);

        // controls
        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(4,2));

        JButton addNodeButton = new JButton("Add/Move Nodes");
        panel2.add(addNodeButton);
        addNodeButton.addActionListener(new AddNodeListener());
        
        JButton rmvNodeButton = new JButton("Remove Nodes");
        panel2.add(rmvNodeButton);
        rmvNodeButton.addActionListener(new RmvNodeListener());

        JButton addEdgeButton = new JButton("Add Edges");
        panel2.add(addEdgeButton);
        addEdgeButton.addActionListener(new AddEdgeListener());

        JButton rmvEdgeButton = new JButton("Remove Edges");
        panel2.add(rmvEdgeButton);
        rmvEdgeButton.addActionListener(new RmvEdgeListener());

        /*JButton resetButton = new JButton("Reset");
        panel2.add(resetButton);
        resetButton.addActionListener(new ResetListener());*/
        pane.add(panel2);
    }

    /** 
     * Returns a point found within the drawing radius of the given location, 
     * or null if none
     *
     *  x  the x coordinate of the location
     *  y  the y coordinate of the location
     *  @return  a point from the graph if there is one covering this location, 
     *  or a null reference if not
     */
    public Point findNearbyPoint(int x, int y) {
        Point pointFound = null;
        Graph<DisplayNode,DisplayEdge> g = graph.getGraph();
        for(int i=0; i<g.numNodes(); i++) {
            Point p = g.getNode(i).getData().getPosition();
            double d = p.distance((double)x, (double)y);
            if (d < 20) {
                pointFound = p;
            }
        }
        return pointFound;
    }

    /** Constants for recording the input mode */
    enum InputMode {
        ADD_NODES, RMV_NODES, ADD_EDGES, RMV_EDGES,
        }

    /** Listener for AddNode button */
    private class AddNodeListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_NODES;
            instr.setText("Click to add new node; drag to move");
        }
    }

    /** Listener for RmvNode button */
    private class RmvNodeListener implements ActionListener {
        /** Event handler for RmvNode button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.RMV_NODES;
            instr.setText("Click to remove node");
        }
    }

    /** Listener for AddEdge button */
    private class AddEdgeListener implements ActionListener {
        /** Event handler for AddEdge button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_EDGES;
            instr.setText("Click on two existing nodes to add a new edge");
        }
    }

    /** Listener for RmvEdge button */
    private class RmvEdgeListener implements ActionListener {
        /** Event handler for RmvEdge button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.RMV_EDGES;
            instr.setText("Click to remove edges");
        }
    }




    
    /* Listener for Reset button 
    private class ResetListener implements ActionListener {
         Event handler for Reset button 
        public void actionPerformed(ActionEvent e) {
            instr.setText("Graph has been Reset!");
            // resets all the edge colors to original color
            for (int i=0; i<graph.getGraph().numEdges(); i++) {
                graph.getGraph().getEdge(i).getData().setColor(new Color(232,111,117));
            }
            graph.repaint();
        }
    }*/

    /** Mouse listener for Pointgraph element */
    private class PointMouseListener extends MouseAdapter
        implements MouseMotionListener {

        /** Responds to click event depending on mode */
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            Point nearBy = findNearbyPoint(mouseX, mouseY);
            switch (mode) {
            case ADD_NODES:
                // If the click is not on top of an existing point, create a new one and add it to the graph.
                // Otherwise, emit a beep, as shown below:
                if(nearBy == null) {
                    Point newPoint = new Point(mouseX, mouseY);
                    String data = JOptionPane.showInputDialog("Please input data for node: ");
                    if (data != null) {
                        graph.getGraph().addNode(new DisplayNode(newPoint, data, new Color(40,188,237)));
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                break;
            case RMV_NODES:
                // If the click is on top of an existing node, remove it from the graph's list of nodes.
                // Otherwise, emit a beep.
                if(nearBy != null) {
                    for (int i=0; i<graph.getGraph().numNodes(); i++) {
                        if (graph.getGraph().getNode(i).getData().getPosition() == nearBy) {
                            graph.getGraph().removeNode(graph.getGraph().getNode(i));
                        }
                    }
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
                break;
            case ADD_EDGES:
                // If two nodes are clicked, add edge in between them
                if (nearBy != null) {
                    if(headUnderMouse == null) {
                        headUnderMouse = nearBy;
                    } else if ((headUnderMouse != null && tailUnderMouse == null) && (!nearBy.equals(headUnderMouse))) {
                        tailUnderMouse = nearBy;
                        Graph<DisplayNode,DisplayEdge>.Node head = null;
                        Graph<DisplayNode,DisplayEdge>.Node tail =  null;
                        for (int i=0; i<graph.getGraph().numNodes(); i++) {
                            if (graph.getGraph().getNode(i).getData().getPosition() == headUnderMouse) {
                                head = graph.getGraph().getNode(i);
                            } else if (graph.getGraph().getNode(i).getData().getPosition() == tailUnderMouse) {
                                tail = graph.getGraph().getNode(i);
                            }
                        }
                       String data = JOptionPane.showInputDialog("Please input name for edge: ");
                       // if (data != null && data.length() != 0) {
                        //    try {
                        //    int weight = Integer.parseInt(data);
                        if(head != null && tail != null)
                            graph.getGraph().addEdge(new DisplayEdge(data, new Color(232,111,117), 0), head, tail);
                        //    } catch (Exception f) {
                        //        JOptionPane.showMessageDialog(null,"Please enter a valid number for edge cost","ERROR!",JOptionPane.WARNING_MESSAGE);
                         //   }
                       //// } else {
                         //   JOptionPane.showMessageDialog(null,"Please enter a valid number for edge cost","ERROR!",JOptionPane.WARNING_MESSAGE);
                       // }
                        headUnderMouse = null;
                        tailUnderMouse = null;
                    }
                }
                break;
            case RMV_EDGES:
                // If two nodes are clicked, remove edge in between them
                if (nearBy != null) {
                    if(headUnderMouse == null) {
                        headUnderMouse = nearBy;
                    } else if (headUnderMouse != null && tailUnderMouse == null) {
                        tailUnderMouse = nearBy;
                        Graph<DisplayNode,DisplayEdge>.Node head = null;
                        Graph<DisplayNode,DisplayEdge>.Node tail =  null;
                        for (int i=0; i<graph.getGraph().numNodes(); i++) {
                            if (graph.getGraph().getNode(i).getData().getPosition() == headUnderMouse) {
                                head = graph.getGraph().getNode(i);
                            } else if (graph.getGraph().getNode(i).getData().getPosition() == tailUnderMouse) {
                                tail = graph.getGraph().getNode(i);
                            }
                        }
                        graph.getGraph().removeEdge(head, tail);
                        headUnderMouse = null;
                        tailUnderMouse = null;
                    }
                }
                break;
            }
            graph.repaint();
        }

        /** Records point under mousedown event in anticipation of possible drag */
        public void mousePressed(MouseEvent e) {
            // Record point under mouse, if any
            pointUnderMouse = findNearbyPoint(e.getX(), e.getY());
        }

        /** Responds to mouseup event */
        public void mouseReleased(MouseEvent e) {
            // Clear record of point under mouse, if any
            pointUnderMouse = null;
        }

        /** Responds to mouse drag event */
        public void mouseDragged(MouseEvent e) {
            // If mode allows point motion, and there is a point under the mouse, 
            // then change its coordinates to the current mouse coordinates & update display
            if (mode == InputMode.ADD_NODES && pointUnderMouse != null) {
                pointUnderMouse.setLocation(e.getX(), e.getY());
                graph.repaint();
            }
        }

        // Empty but necessary to comply with MouseMotionListener interface.
        public void mouseMoved(MouseEvent e) {}
    }
}