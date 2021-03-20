/*   
Copyright (C) 2021  Jonathan Elsner

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package spacetime_diagram;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;

/**
 * A simple GUI for drawing accurate Spacetime diagrams and observing how these
 * diagrams change when the situation is observed at different speeds.
 * 
 * @author Jonathan Elsner
 */
public class SpacetimeDiagramGUI extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -1681071496023255137L;

    private static final Font MONOSPACE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    /**
     * Where the spacetime diagram is drawn.
     * 
     * @see Diagram
     */
    private Diagram graph;

    /**
     * Stores and monitors spacetime travellers (worldlines) and spacetime events.
     * 
     * @see SpacetimeEventListModel
     * @see SpacetimeEvent
     * @see SpacetimeTraveller
     */
    private SpacetimeEventListModel objects;

    /**
     * Construct the {@code GUI} and its subcomponents
     * 
     * @see SpacetimeDiagramGUI
     */
    public SpacetimeDiagramGUI() {
        // Call the super constructor and specify the window name
        super("Spacetime Diagram");

        // Create the list where we store all of the spacetime objects
        objects = new SpacetimeEventListModel();

        // Initialize the diagram with some preset items for testing
        // objects.add(new SpacetimeTraveller("foo", 0.1, 0, 0));
        // objects.add(new SpacetimeTraveller("bar", -0.6, 0, 50));
        // objects.add(new SpacetimeEvent("baz", 50, -50));

        // Set how the GUI closes
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ###### Construct the menu bar #######
        // #region menu_bar

        JMenuBar menuBar = new JMenuBar();
        this.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        menuBar.add(fileMenu);

        JMenuItem exportGraphItem = new JMenuItem("Export Graph as Image");
        exportGraphItem.setMnemonic('E');
        fileMenu.add(exportGraphItem);

        // Code for saving an image
        exportGraphItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            int returnVal = fileChooser.showSaveDialog(this);

            // If the user selects a place to save the image
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();

                BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = (Graphics2D) img.getGraphics();

                // Paint the image background white, since the Diagram doesn't do this
                // Otherwise, the image will come out transparent
                graphics.setColor(Color.white);
                graphics.fillRect(0, 0, graph.getWidth(), graph.getHeight());

                graph.paint(graphics);
                try {
                    ImageIO.write(img, "png", saveFile);
                } catch (Exception ex) {
                    // TODO: handle exception
                }
            }
        });

        // #endregion menu_bar

        // Initialize master GUI layout management
        this.setLayout(new GridBagLayout());
        GridBagConstraints masterGBC = new GridBagConstraints();
        masterGBC.gridx = masterGBC.gridy = 0;

        // ####### Configure Global Options ######
        // Create GUI controls for settings like the speed of the reference frame of the
        // Diagram
        // #region global_options

        JPanel globalOptions = new JPanel();

        // Configure top panel in sidebar
        globalOptions.setBorder(BorderFactory.createTitledBorder("Reference Frame Speed"));
        globalOptions.setLayout(new BoxLayout(globalOptions, BoxLayout.PAGE_AXIS));

        // Speed slider
        JSlider observerSpeed = new JSlider(-100, 100, 0);
        observerSpeed.setToolTipText("Change the speed of the observer drawing the spacetime diagram");

        observerSpeed.setMajorTickSpacing(20);
        observerSpeed.setMinorTickSpacing(10);
        observerSpeed.setPaintTicks(true);
        observerSpeed.setPaintLabels(true);
        observerSpeed.setSnapToTicks(true);
        observerSpeed.addChangeListener(e -> {
            graph.setReferenceFrameBeta(((JSlider) e.getSource()).getValue() / 100.0);
            graph.repaint();
        });

        // Labels on slider representing fraction of the speed of light
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = -100; i <= 100; i += 20) {
            labelTable.put(i, new JLabel("" + (i / 100.0)));
        }
        observerSpeed.setLabelTable(labelTable);

        globalOptions.add(observerSpeed);

        masterGBC.anchor = GridBagConstraints.PAGE_START;
        masterGBC.fill = GridBagConstraints.HORIZONTAL;
        masterGBC.weightx = 0.1;
        this.add(globalOptions, masterGBC);
        masterGBC.gridy++;

        // #endregion global_options

        // ###### Configure object list ######
        // The list of spacetime objects and buttons to add and remove them
        // #region object_list

        JPanel objectListPanel = new JPanel();
        objectListPanel.setBorder(BorderFactory.createTitledBorder("Spacetime Objects"));

        JList<SpacetimeEvent> objectList = new JList<>(objects);

        JScrollPane objectListScrollPane = new JScrollPane(objectList);
        objectListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        objectListPanel.setLayout(new GridBagLayout());
        GridBagConstraints listPnlGbc = new GridBagConstraints();
        listPnlGbc.gridx = 0;
        listPnlGbc.gridy = 0;
        listPnlGbc.weightx = listPnlGbc.weighty = 1;
        listPnlGbc.insets = new Insets(2, 2, 2, 2);
        listPnlGbc.fill = GridBagConstraints.BOTH;

        listPnlGbc.gridwidth = 3;
        objectListPanel.add(objectListScrollPane, listPnlGbc);
        listPnlGbc.gridwidth = 1;
        listPnlGbc.weighty = 0;
        listPnlGbc.gridy++;

        // Buttons to add and remove items from the list
        JButton addEventBtn = new JButton("Add Event");
        addEventBtn.setToolTipText("Add an event to the spacetime diagram");
        addEventBtn.addActionListener(e -> {
            objects.add(new SpacetimeEvent("New Event", 0, 0));
        });

        JButton addTravellerBtn = new JButton("Add traveller");
        addEventBtn.setToolTipText("Add a worldline to the spacetime diagram");
        addTravellerBtn.addActionListener(e -> {
            objects.add(new SpacetimeTraveller("New Traveller", 0, 0, 0));
        });

        JButton removeBtn = new JButton("Remove");
        removeBtn.setToolTipText("Remove the selected element");
        removeBtn.setEnabled(false);
        removeBtn.addActionListener(e -> {
            int i = objectList.getSelectedIndex();
            if (0 <= i && i < objects.size()) {
                objects.remove(i);
            }
        });

        // Disable remove button when no item is selected
        objectList.addListSelectionListener(e -> {
            removeBtn.setEnabled(objectList.getSelectedIndex() != -1);
        });

        // Add the buttons to the panel
        objectListPanel.add(addEventBtn, listPnlGbc);
        listPnlGbc.gridx++;

        objectListPanel.add(addTravellerBtn, listPnlGbc);
        listPnlGbc.gridx++;

        objectListPanel.add(removeBtn, listPnlGbc);

        // Add the object list panel to the GUI
        masterGBC.anchor = GridBagConstraints.CENTER;
        masterGBC.fill = GridBagConstraints.BOTH;
        masterGBC.weightx = masterGBC.weighty = 0.1;
        this.add(objectListPanel, masterGBC);
        masterGBC.weighty = 0;
        masterGBC.gridy++;

        // #endregion object_list

        // ####### Create Object details panel #####
        // Add components to change settings such as position, time, and speed for the
        // spacetime objects
        // #region object_details

        JPanel objSettingsPnl = new JPanel();
        objSettingsPnl.setBorder(BorderFactory.createTitledBorder("Selected Event/Traveller"));
        objSettingsPnl.setLayout(new GridBagLayout());

        GridBagConstraints objSettingsGbc = new GridBagConstraints();
        objSettingsGbc.gridx = objSettingsGbc.gridy = 0;
        objSettingsGbc.anchor = GridBagConstraints.FIRST_LINE_START;
        objSettingsGbc.fill = GridBagConstraints.HORIZONTAL;
        objSettingsGbc.insets = new Insets(2, 2, 2, 2);

        JLabel nameLabel = new JLabel("Name");
        JTextField nameField = new JTextField(10);

        nameField.setEnabled(false);
        nameField.setToolTipText("Rename the selected element");
        // Update object and object list when name changes
        nameField.addActionListener(e -> {
            objectList.getSelectedValue().setName(nameField.getText());
            objects.fireChangeEvent(objectList.getSelectedIndex());
        });

        nameLabel.setLabelFor(nameField);
        objSettingsPnl.add(nameLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        objSettingsGbc.weightx = 1;
        objSettingsPnl.add(nameField, objSettingsGbc);
        objSettingsGbc.weightx = 0;
        objSettingsGbc.fill = GridBagConstraints.NONE;
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel xLabel = new JLabel("x-Position");
        JTextField xField = new JTextField(20);

        xField.setEnabled(false);
        xField.setToolTipText("Set the x-coordinate of the selected element");
        xField.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, xField.getFont().getSize()));
        // Update object and Diagram when x value changes
        xField.addActionListener(e -> {
            try {
                double newX = Double.valueOf(xField.getText());
                objectList.getSelectedValue().setX(graph.getReferenceFrameBeta(), newX);

                graph.repaint();
            } catch (NumberFormatException ex) {
                xField.setText("" + objectList.getSelectedValue().getX(graph.getReferenceFrameBeta()));
            }
        });

        xLabel.setLabelFor(xField);
        objSettingsPnl.add(xLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        objSettingsPnl.add(xField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel tLabel = new JLabel("Time");
        JTextField tField = new JTextField(20);

        tField.setEnabled(false);
        tField.setToolTipText("Set the t-coordinate of the selected element");
        tField.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, tField.getFont().getSize()));
        // Update object and Diagram when time changes
        tField.addActionListener(e -> {
            try {
                double newT = Double.valueOf(tField.getText());
                objectList.getSelectedValue().setT(graph.getReferenceFrameBeta(), newT);

                graph.repaint();
            } catch (NumberFormatException ex) {
                tField.setText("" + objectList.getSelectedValue().getT(graph.getReferenceFrameBeta()));
            }
        });

        tLabel.setLabelFor(tField);
        objSettingsPnl.add(tLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        objSettingsPnl.add(tField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel betaLabel = new JLabel("Beta");
        JTextField betaField = new JTextField(20);

        betaField.setEnabled(false);
        betaField.setToolTipText("Set the beta value for the selected worldline");
        betaField.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, betaField.getFont().getSize()));
        // Update object and Diagram when speed changes
        betaField.addActionListener(e -> {
            SpacetimeTraveller traveller = (SpacetimeTraveller) objectList.getSelectedValue();

            try {
                double newBeta = Double.valueOf(betaField.getText());
                betaField.setText("" + traveller.setBeta(graph.getReferenceFrameBeta(), newBeta));

                graph.repaint();
            } catch (NumberFormatException ex) {
                betaField.setText("" + traveller.getBeta(graph.getReferenceFrameBeta()));
            }
        });

        betaLabel.setLabelFor(betaField);
        objSettingsPnl.add(betaLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        objSettingsPnl.add(betaField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        objSettingsGbc.fill = GridBagConstraints.VERTICAL;
        objSettingsGbc.weighty = 1;
        objSettingsPnl.add(Box.createVerticalGlue(), objSettingsGbc);

        // Update the values in the details panel if the object selection changes
        objectList.addListSelectionListener(e -> {
            SpacetimeEvent object = objectList.getSelectedValue();

            // Disable panel when no object is selected in the list
            if (object == null) {
                nameField.setEnabled(false);
                xField.setEnabled(false);
                tField.setEnabled(false);
                betaField.setEnabled(false);

                return;
            } else {
                nameField.setEnabled(true);
                xField.setEnabled(true);
                tField.setEnabled(true);
                betaField.setEnabled(true);
            }

            nameField.setText(object.getName());

            // TODO formatting
            xField.setText(String.valueOf(object.getX(graph.getReferenceFrameBeta())));
            tField.setText(String.valueOf(object.getT(graph.getReferenceFrameBeta())));

            // Only show the beta field if a traveller that can have a speed is selected
            if (object instanceof SpacetimeTraveller) {
                betaLabel.setVisible(true);
                betaField.setVisible(true);

                betaField.setText(String.valueOf(((SpacetimeTraveller) object).getBeta(graph.getReferenceFrameBeta())));
            } else {
                betaLabel.setVisible(false);
                betaField.setVisible(false);
            }
        });

        // Update the object details when the reference frame speed changes.
        // because relativity
        observerSpeed.addChangeListener(e -> {
            // TODO redundant
            SpacetimeEvent object = objectList.getSelectedValue();

            if (object == null) {
                return;
            }

            nameField.setText(object.getName());

            // TODO formatting
            xField.setText(String.valueOf(object.getX(graph.getReferenceFrameBeta())));
            tField.setText(String.valueOf(object.getT(graph.getReferenceFrameBeta())));

            // Only update beta field if a traveller with a speed is selected
            if (object instanceof SpacetimeTraveller) {
                betaField.setText(String.valueOf(((SpacetimeTraveller) object).getBeta(graph.getReferenceFrameBeta())));
            }
        });

        masterGBC.anchor = GridBagConstraints.PAGE_END;
        masterGBC.fill = GridBagConstraints.HORIZONTAL;
        masterGBC.weightx = 0.1;
        this.add(objSettingsPnl, masterGBC);
        masterGBC.gridy = 0;
        masterGBC.gridx++;

        // #endregion object_details

        // ###### Spacetime graph & panel ######
        // Configure how the Diagram is shown
        // #region diagram

        JPanel graphPnl = new JPanel();
        graphPnl.setBorder(BorderFactory.createTitledBorder("Graph"));
        graphPnl.setLayout(new GridBagLayout());

        graph = new Diagram(objects);

        // repaint the graph whenever the data in the list changes
        objects.addListDataListener(graph);

        GridBagConstraints graphGBC = new GridBagConstraints();
        graphGBC.fill = GridBagConstraints.BOTH;
        graphGBC.weightx = graphGBC.weighty = 1;
        graphPnl.add(graph, graphGBC);

        masterGBC.anchor = GridBagConstraints.CENTER;
        masterGBC.fill = GridBagConstraints.BOTH;
        masterGBC.weightx = masterGBC.weighty = 1;
        masterGBC.gridheight = GridBagConstraints.REMAINDER;
        this.add(graphPnl, masterGBC);

        // #endregion diagram

        // Auto-size gui & show it
        this.pack();
        this.setVisible(true);

        // Set minimum size so components don't get crushed
        this.setMinimumSize(this.getSize());
    }
}
