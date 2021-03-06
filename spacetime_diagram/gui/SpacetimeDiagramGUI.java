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

package spacetime_diagram.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;

import spacetime_diagram.EventIO;
import spacetime_diagram.LorentzTransform;
import spacetime_diagram.SpacetimeEvent;
import spacetime_diagram.SpacetimeTraveller;

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

    public static final Font MONOSPACE_FONT = new Font(Font.MONOSPACED, Font.PLAIN, 12);

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
     * The unit conversion from the internal units to calculate special relativity,
     * to the units that are displayed to the user.
     */
    private double speedOfLight = 1;

    private SpacetimeEventOptionsPanel objSettingsPnl;

    /**
     * Construct the {@code GUI} and its subcomponents
     * 
     * @see SpacetimeDiagramGUI
     */
    public SpacetimeDiagramGUI() {
        // Call the super constructor and specify the window name
        super("Spacetime Diagram");

        // Set logo for window
        URL iconURL = getClass().getResource("/res/logo.png");
        ImageIcon icon = new ImageIcon(iconURL);
        this.setIconImage(icon.getImage());

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

        JMenuItem saveDiagramItem = new JMenuItem("Save Diagram");
        saveDiagramItem.setToolTipText("Save diagram to edit later");
        saveDiagramItem.setMnemonic('s');
        fileMenu.add(saveDiagramItem);
        saveDiagramItem.addActionListener(evt -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Spacetime Diagram File", "diagram"));

            int returnVal = fileChooser.showSaveDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();

                if (!saveFile.getName().endsWith(".diagram")) {
                    saveFile = new File(saveFile.getParentFile(), saveFile.getName() + ".diagram");
                }

                EventIO.saveSpacetimeEvents(saveFile, objects);
            }
        });

        JMenuItem openDiagramItem = new JMenuItem("Open Diagram");
        openDiagramItem.setToolTipText("Open diagram to continue editing");
        openDiagramItem.setMnemonic('o');
        fileMenu.add(openDiagramItem);
        openDiagramItem.addActionListener(evt -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(new FileNameExtensionFilter("Spacetime Diagram File", "diagram"));

            int returnVal = fileChooser.showOpenDialog(this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File openFile = fileChooser.getSelectedFile();

                Collection<SpacetimeEvent> events = EventIO.readSpacetimeEvents(openFile);
                objects.clear();
                objects.addAll(events);
            }
        });

        fileMenu.addSeparator();

        JMenuItem exportGraphItem = new JMenuItem("Export Graph as Image");
        exportGraphItem.setMnemonic('E');
        fileMenu.add(exportGraphItem);

        // Code for saving an image
        exportGraphItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setFileFilter(
                    new FileNameExtensionFilter("Image Files (.png, .jpg, .jpeg)", "png", "jpg", "jpeg"));

            int returnVal = fileChooser.showSaveDialog(this);

            // If the user selects a place to save the image
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File saveFile = fileChooser.getSelectedFile();

                int index = saveFile.getName().lastIndexOf('.');
                String ext = saveFile.getName().substring(index + 1);

                BufferedImage img = new BufferedImage(graph.getWidth(), graph.getHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = (Graphics2D) img.getGraphics();

                // Paint the image background white, since the Diagram doesn't do this
                // Otherwise, the image will come out transparent
                graphics.setColor(Color.white);
                graphics.fillRect(0, 0, graph.getWidth(), graph.getHeight());

                graph.paint(graphics);
                try {
                    ImageIO.write(img, ext, saveFile);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic('O');
        menuBar.add(optionsMenu);

        ButtonGroup cBtns = new ButtonGroup();

        JRadioButtonMenuItem exactCMenuOption = new JRadioButtonMenuItem("c = 299,792,458 m/s");
        exactCMenuOption.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, exactCMenuOption.getFont().getSize()));
        exactCMenuOption.addActionListener(e -> {
            speedOfLight = LorentzTransform.EXACT_C;
            objSettingsPnl.setDistanceConversion(speedOfLight);
        });
        cBtns.add(exactCMenuOption);
        optionsMenu.add(exactCMenuOption);

        JRadioButtonMenuItem approxCMenuOption = new JRadioButtonMenuItem("c = 3,000,000,000 m/s");
        approxCMenuOption.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, exactCMenuOption.getFont().getSize()));
        approxCMenuOption.addActionListener(e -> {
            speedOfLight = LorentzTransform.APPROXIMATE_C;
            objSettingsPnl.setDistanceConversion(speedOfLight);
        });
        cBtns.add(approxCMenuOption);
        optionsMenu.add(approxCMenuOption);

        JRadioButtonMenuItem c1MenuOption = new JRadioButtonMenuItem("c = 1 s/s");
        c1MenuOption.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, exactCMenuOption.getFont().getSize()));
        c1MenuOption.addActionListener(e -> {
            speedOfLight = LorentzTransform.C_1;
            objSettingsPnl.setDistanceConversion(speedOfLight);
        });
        c1MenuOption.setSelected(true);
        cBtns.add(c1MenuOption);
        optionsMenu.add(c1MenuOption);

        optionsMenu.addSeparator();

        JCheckBoxMenuItem drawLightConeCheckbox = new JCheckBoxMenuItem("Draw light cone from origin", false);
        drawLightConeCheckbox.setMnemonic('c');
        drawLightConeCheckbox.addActionListener(e -> {
            graph.setDrawLightCone(drawLightConeCheckbox.getState());
        });
        optionsMenu.add(drawLightConeCheckbox);

        JCheckBoxMenuItem labelsOnGraphCheckbox = new JCheckBoxMenuItem("Labels elements on graph", true);
        labelsOnGraphCheckbox.setMnemonic('l');
        labelsOnGraphCheckbox.addActionListener(evt -> {
            graph.setDrawLabels(labelsOnGraphCheckbox.getState());
        });
        optionsMenu.add(labelsOnGraphCheckbox);

        optionsMenu.addSeparator();

        JMenuItem aboutBtn = new JMenuItem("About...");
        aboutBtn.setToolTipText("About the program");
        aboutBtn.addActionListener(evt -> {
            JDialog dialog = new JDialog(this, "About", true);

            dialog.add(new AboutPanel());
            dialog.setSize(new Dimension(300, 300));
            dialog.setVisible(true);
        });
        optionsMenu.add(aboutBtn);

        // #endregion menu_bar

        // Initialize master GUI layout management
        this.setLayout(new GridBagLayout());
        GridBagConstraints masterGBC = new GridBagConstraints();
        masterGBC.gridx = masterGBC.gridy = 0;

        // ####### Configure Global Options ######
        // Create GUI controls for settings like the speed of the reference frame of the
        // Diagram
        // #region global_options

        JPanel globalOptions = new JPanel(new GridBagLayout());

        // Configure top panel in sidebar
        globalOptions.setBorder(BorderFactory.createTitledBorder("Reference Frame Speed"));

        GridBagConstraints globalOpsGBC = new GridBagConstraints();
        globalOpsGBC.anchor = GridBagConstraints.PAGE_START;
        globalOpsGBC.gridx = globalOpsGBC.gridy = 0;
        globalOpsGBC.weightx = 0.2;
        globalOpsGBC.fill = GridBagConstraints.HORIZONTAL;

        JTextField observerBetaField = new JTextField("0.000");
        observerBetaField.setToolTipText("Change the speed of the observer drawing the spacetime diagram");
        observerBetaField.setFont(MONOSPACE_FONT.deriveFont(Font.PLAIN, observerBetaField.getFont().getSize()));
        globalOptions.add(observerBetaField, globalOpsGBC);
        globalOpsGBC.gridx++;
        globalOpsGBC.weightx = 0.8;

        // Speed slider
        JSlider observerSpeed = new JSlider(-100, 100, 0);
        observerSpeed.setToolTipText("Change the speed of the observer drawing the spacetime diagram");

        observerSpeed.setMajorTickSpacing(20);
        observerSpeed.setMinorTickSpacing(10);
        observerSpeed.setPaintTicks(true);
        observerSpeed.setPaintLabels(true);
        // observerSpeed.setSnapToTicks(true);
        observerSpeed.addChangeListener(e -> {
            observerBetaField.setText(String.valueOf(observerSpeed.getValue() / 100.0));
            graph.setReferenceFrameBeta(((JSlider) e.getSource()).getValue() / 100.0);
            graph.repaint();
        });

        observerBetaField.addActionListener(evt -> {
            try {
                double newValue = Double.valueOf(observerBetaField.getText());

                if (Math.abs(newValue) <= 1) {
                    observerSpeed.setValue((int) (newValue * 100));
                    graph.setReferenceFrameBeta(newValue);
                    repaint();
                } else {
                    observerBetaField.setText(String.valueOf(observerSpeed.getValue() / 100.0));
                }
            } catch (NumberFormatException ex) {
                observerBetaField.setText(String.valueOf(observerSpeed.getValue() / 100.0));
            }
        });

        // Labels on slider representing fraction of the speed of light
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = -100; i <= 100; i += 20) {
            labelTable.put(i, new JLabel("" + (i / 100.0)));
        }
        observerSpeed.setLabelTable(labelTable);

        globalOptions.add(observerSpeed, globalOpsGBC);

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

        objSettingsPnl = new SpacetimeEventOptionsPanel();
        observerSpeed.addChangeListener(evt -> objSettingsPnl.setReferenceFrameBeta(observerSpeed.getValue() / 100.0));
        objectList.addListSelectionListener(evt -> objSettingsPnl.setCurrentEvent(objectList.getSelectedValue()));
        objSettingsPnl.addActionListener(evt -> {
            if (evt.getActionCommand() == "name") {
                objects.fireChangeEvent(objectList.getSelectedValue());
            } else {
                graph.repaint();
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

    /**
     * Returns the speed of light used in the GUI
     * 
     * @return the speed of light used in the GUI
     */
    public double getSpeedOfLight() {
        return speedOfLight;
    }
}
