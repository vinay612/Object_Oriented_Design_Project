import java.util.*;   
import java.io.*;   
import static java.awt.Color.red;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.FileWriter;
import java.io.BufferedReader; 
import java.io.FileReader;
import java.io.IOException;

public class GraphGUI {

    private static final String filePath = "obj.bin";
    public static int Red = 0;
    public static int Blue = 0;
    public static int Green = 0;
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
     * Schedules a job for the event-dispatching thread creating and showing this
     * application's GUI.
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
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        graph = new JGraph();
        PointMouseListener pml = new PointMouseListener();
        graph.addMouseListener(pml);
        graph.addMouseMotionListener(pml);
        panel.add(graph);
        instr = new JLabel("Click to add new nodes , drag to move.");
        panel.add(instr, BorderLayout.NORTH);
        pane.add(panel);

        // controls
        MenuBar menu = new MenuBar();
        Menu panel1 = new Menu("Options");

        MenuItem setColorButton = new MenuItem("Set Pen Color");
        panel1.add(setColorButton);
        setColorButton.addActionListener(new setColorListener());

        MenuItem addNodeButton = new MenuItem("Add/Move Nodes");
        panel1.add(addNodeButton);
        addNodeButton.addActionListener(new AddNodeListener());

        MenuItem clrNodeButton = new MenuItem("Color Nodes");
        panel1.add(clrNodeButton);
        clrNodeButton.addActionListener(new ClrNodeListener());

        MenuItem rmvNodeButton = new MenuItem("Remove Nodes");
        panel1.add(rmvNodeButton);
        rmvNodeButton.addActionListener(new RmvNodeListener());

        MenuItem addEdgeButton = new MenuItem("Add Edges");
        panel1.add(addEdgeButton);
        addEdgeButton.addActionListener(new AddEdgeListener());

        MenuItem clrEdgeButton = new MenuItem("Color Edge");
        panel1.add(clrEdgeButton);
        clrEdgeButton.addActionListener(new ClrEdgeListener());

        MenuItem rmvEdgeButton = new MenuItem("Remove Edges");
        panel1.add(rmvEdgeButton);
        rmvEdgeButton.addActionListener(new RmvEdgeListener());

        MenuItem resetButton = new MenuItem("Reset");
        panel1.add(resetButton);
        resetButton.addActionListener(new ResetListener());
        

        Menu panel2 = new Menu("File");

        MenuItem graphSaveButton = new MenuItem("Save Graph");
        panel2.add(graphSaveButton);
        graphSaveButton.addActionListener(new graphSaveListener());

        MenuItem graphUploadButton = new MenuItem("Open Existing Graph"); 
        panel2.add(graphUploadButton);
        graphUploadButton.addActionListener(new graphUploadListener());

        menu.add(panel2);
        menu.add(panel1);

        frame.setMenuBar(menu);
    }

    /**
     * Returns a point found within the drawing radius of the given location, or
     * null if none
     *
     * x the x coordinate of the location y the y coordinate of the location
     * 
     * @return a point from the graph if there is one covering this location, or a
     *         null reference if not
     */
    public Point findNearbyPoint(int x, int y) {
        Point pointFound = null;
        Graph<DisplayNode, DisplayEdge> g = graph.getGraph();
        for (int i = 0; i < g.numNodes(); i++) {
            Point p = g.getNode(i).getData().getPosition();
            double d = p.distance((double) x, (double) y);
            if (d < 20) {
                pointFound = p;
            }
        }
        return pointFound;
    }

    /** Constants for recording the input mode */
    enum InputMode {
        ADD_NODES, RMV_NODES, CLR_NODES, ADD_EDGES, RMV_EDGES, CLR_EDGES
    }

    private class graphSaveListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            try {
                String filename = JOptionPane.showInputDialog("Please input name for graph :(Please give extension as sg) ");
                FileWriter fout = new FileWriter(filename);

                fout.write(graph.getGraph().numNodes() + "\n");

                for (int i = 0; i < graph.getGraph().numNodes(); i++) {
                    Graph<DisplayNode, DisplayEdge>.Node node = graph.getGraph().getNode(i);
                    DisplayNode data = node.getData();
                    Point p = data.getPosition();
                    Color col = data.getColor();
                    fout.write((int) p.getX() + "," + (int) p.getY() + "," + data.getLabel() + "," + col.getRed() + "," + col.getGreen() + "," + col.getBlue()
                            + "\n");
                }

                fout.write(graph.getGraph().numEdges() + "\n");

                for (int i = 0; i < graph.getGraph().numEdges(); i++) {
                    Graph<DisplayNode, DisplayEdge>.Edge edge = graph.getGraph().getEdge(i);
                    Graph<DisplayNode, DisplayEdge>.Node n1 = edge.getHead();
                    Graph<DisplayNode, DisplayEdge>.Node n2 = edge.getTail();
                    int head_index = 0, tail_index = 0;
                    for (int j = 0; j < graph.getGraph().numNodes(); j++) {
                        if(graph.getGraph().getNode(j) == n1){
                            head_index = j;
                        }
                        else if (graph.getGraph().getNode(j) == n2) {
                            tail_index = j;
                        }
                    }
                    Color col = edge.getData().getColor();
                    String label = edge.getData().getLabel();
                    fout.write(head_index + "," + tail_index + "," + label + "," + col.getRed() + "," + col.getGreen() + "," + col.getBlue() + "\n");
                }


                fout.close();
            } catch(IOException ex){
                System.out.println(ex);
            }
        }
    }

    private class graphUploadListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            try {
                String filepath;
                for (int i=0; i<graph.getGraph().numNodes(); i++) {
                    graph.getGraph().removeNode(graph.getGraph().getNode(i));
                }
                for (int i=0; i<graph.getGraph().numEdges(); i++) {
                    graph.getGraph().removeEdge(graph.getGraph().getEdge(i));
                }
                graph.repaint();
                for (int i=0; i<graph.getGraph().numNodes(); i++) {
                    graph.getGraph().removeNode(graph.getGraph().getNode(i));
                }
                graph.repaint();   
                //Using JFileChooser for opening File Explorer
                JFileChooser fc=new JFileChooser();    
                int j=fc.showOpenDialog(null);       
                File f=fc.getSelectedFile();    
                filepath=f.getPath(); 
                
                FileReader fr = new FileReader(filepath); 
                BufferedReader br = new BufferedReader(fr);
                int numNodes = Integer.parseInt(br.readLine());
                for(int i = 0; i < numNodes; i++){
                    String[] arrSplit = br.readLine().split(",");
                    Point newPoint = new Point(Integer.parseInt(arrSplit[0]), Integer.parseInt(arrSplit[1]));
                    graph.getGraph().addNode(new DisplayNode(newPoint, arrSplit[2], new Color(Integer.parseInt(arrSplit[3]), Integer.parseInt(arrSplit[4]), Integer.parseInt(arrSplit[5]))));
                }
                int numEdges = Integer.parseInt(br.readLine());
                for(int i = 0; i < numEdges; i++){
                    String[] arrSplit = br.readLine().split(",");
                    graph.getGraph().addEdge(new DisplayEdge(arrSplit[2], new Color(Integer.parseInt(arrSplit[3]), Integer.parseInt(arrSplit[4]), Integer.parseInt(arrSplit[5])), 0), graph.getGraph().getNode(Integer.parseInt(arrSplit[0])), graph.getGraph().getNode(Integer.parseInt(arrSplit[1])));
                }
                br.close();
                fr.close();

                graph.repaint();
                
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private class setColorListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            JPanel pane = new JPanel();
            pane.setLayout(new GridLayout(0, 2, 2, 2));

            JTextField _red = new JTextField(5);
            JTextField _blue = new JTextField(5);
            JTextField _green = new JTextField(5);

            pane.add(new JLabel("R"));
            pane.add(_red);  
            pane.add(new JLabel("G"));
            pane.add(_green);
            pane.add(new JLabel("B"));
            pane.add(_blue);

            int option = JOptionPane.showConfirmDialog(graph, pane, "Please fill all the fields", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.YES_OPTION) {

                String red = _red.getText();
                String green = _green.getText();
                String blue = _blue.getText();

                try {
                    Red = Integer.parseInt(red);
                    Green = Integer.parseInt(green);
                    Blue = Integer.parseInt(blue);
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }
            }
        }
    }
    /** Listener for AddNode button */
    private class AddNodeListener implements ActionListener {
        /** Event handler for AddNode button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.ADD_NODES;
            instr.setText("Click to add new node; drag to move");
        }
    }

    /**Listener for Coloring Nodes */
    private class ClrNodeListener implements ActionListener {
        /** Event handler for ClrNode button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.CLR_NODES;
            instr.setText("Click to color existing node");
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

    private class ClrEdgeListener implements ActionListener {
        /** Event handler for RmvEdge button */
        public void actionPerformed(ActionEvent e) {
            mode = InputMode.CLR_EDGES;
            instr.setText("Click to color edges");
        }
    }



    
    /* Listener for Reset button */
    private class ResetListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            instr.setText("Graph has been Reset!");
            // resets all the edge colors to original color
            for (int i=0; i<graph.getGraph().numNodes(); i++) {
                graph.getGraph().removeNode(graph.getGraph().getNode(i));
            }
            for (int i=0; i<graph.getGraph().numEdges(); i++) {
                graph.getGraph().removeEdge(graph.getGraph().getEdge(i));
            }
            graph.repaint();
            for (int i=0; i<graph.getGraph().numNodes(); i++) {
                graph.getGraph().removeNode(graph.getGraph().getNode(i));
            }
            graph.repaint();
        }
    }

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
                        graph.getGraph().addNode(new DisplayNode(newPoint, data, new Color(Red, Blue, Green)));
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
            case CLR_NODES:
                // If the click is on top of an existing node, color.
                // Otherwise, emit a beep.
                if(nearBy != null) {
                    int _Red = 0;
                    int _Blue = 0;
                    int _Green = 0;
                    for (int i=0; i<graph.getGraph().numNodes(); i++) {
                        if (graph.getGraph().getNode(i).getData().getPosition() == nearBy) {
                            JPanel pane = new JPanel();
                            pane.setLayout(new GridLayout(0, 2, 2, 2));

                            JTextField _red = new JTextField(5);
                            JTextField _blue = new JTextField(5);
                            JTextField _green = new JTextField(5);

                            pane.add(new JLabel("R"));
                            pane.add(_red);  
                            pane.add(new JLabel("G"));
                            pane.add(_green);
                            pane.add(new JLabel("B"));
                            pane.add(_blue);

                            int option = JOptionPane.showConfirmDialog(graph, pane, "Please fill all the fields", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                            if (option == JOptionPane.YES_OPTION) {

                                String red = _red.getText();
                                String blue = _blue.getText();
                                String green = _green.getText();

                                try {
                                    _Red = Integer.parseInt(red);
                                    _Blue = Integer.parseInt(blue);
                                    _Green = Integer.parseInt(green);
                                } catch (NumberFormatException nfe) {
                                    nfe.printStackTrace();
                                }

                                graph.getGraph().getNode(i).getData().setColor(new Color(_Red, _Green, _Blue));
                            }
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
                            graph.getGraph().addEdge(new DisplayEdge(data, new Color(Red, Green, Blue), 0), head, tail);
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
            case CLR_EDGES:
                // If two nodes are clicked, remove edge in between them
                if (nearBy != null) {

                    int _Red = 0;
                    int _Blue = 0;
                    int _Green = 0;
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
                        JPanel pane = new JPanel();
                        pane.setLayout(new GridLayout(0, 2, 2, 2));

                        JTextField _red = new JTextField(5);
                        JTextField _blue = new JTextField(5);
                        JTextField _green = new JTextField(5);

                        pane.add(new JLabel("R"));
                        pane.add(_red);  
                        pane.add(new JLabel("B"));
                        pane.add(_blue);
                        pane.add(new JLabel("G"));
                        pane.add(_green);

                        int option = JOptionPane.showConfirmDialog(graph, pane, "Please fill all the fields", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

                        if (option == JOptionPane.YES_OPTION) {

                            String red = _red.getText();
                            String blue = _blue.getText();
                            String green = _green.getText();

                            try {
                                _Red = Integer.parseInt(red);
                                _Blue = Integer.parseInt(blue);
                                _Green = Integer.parseInt(green);
                            } catch (NumberFormatException nfe) {
                                nfe.printStackTrace();
                            }

                        graph.getGraph().getEdgeRef(head, tail).getData().setColor(new Color(_Red, _Blue, _Green));
                        }
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