package spacetime_diagram;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform.SpacetimeObject;
import spacetime_diagram.LorentzTransform.SpacetimeTraveller;

public class GUI extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -1681071496023255137L;

    public HashMap<String, double[]> referenceFrames;

    private Diagram graph;

    private SpacetimeObjectListModel objects;

    public GUI() {
        super("Spacetime Diagram");

        referenceFrames = new HashMap<>();
        referenceFrames.put("Rest Frame", new double[] { 0, 0 });

        objects = new SpacetimeObjectListModel();

        objects.add(new SpacetimeTraveller("foo", 0.1, 0, 0));
        objects.add(new SpacetimeTraveller("bar", -0.6, 0, 50));
        objects.add(new SpacetimeObject("baz", 50, -50));

        // Set how the GUI closes
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the sidebar panel
        JPanel optionsPanel = new JPanel();
        JPanel globalOptions = new JPanel();

        // Create the sidebar
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

        // Configure top panel in sidebar
        globalOptions.setBorder(BorderFactory.createTitledBorder("Options"));
        globalOptions.setLayout(new BoxLayout(globalOptions, BoxLayout.PAGE_AXIS));

        JRadioButton approxCRadio = new JRadioButton("C = 3*10^8m/s");
        JRadioButton exactCRadio = new JRadioButton("C = 299,792,458m/s");

        ButtonGroup cSpeedRadioGroup = new ButtonGroup();
        cSpeedRadioGroup.add(approxCRadio);
        cSpeedRadioGroup.add(exactCRadio);

        globalOptions.add(approxCRadio);
        globalOptions.add(exactCRadio);

        approxCRadio.setSelected(true);

        JCheckBox shiftViewerCheckBox = new JCheckBox("Shift Current Reference Frame world line to origin");
        globalOptions.add(shiftViewerCheckBox);

        JSlider observerSpeed = new JSlider(-100, 100, 0);
        observerSpeed.setMajorTickSpacing(20);
        observerSpeed.setMinorTickSpacing(10);
        observerSpeed.setPaintTicks(true);
        observerSpeed.setPaintLabels(true);
        observerSpeed.setSnapToTicks(true);
        observerSpeed.addChangeListener(e -> {
            graph.setReferenceFrameBeta(((JSlider) e.getSource()).getValue() / 100.0);
            graph.repaint();
        });

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        for (int i = -100; i <= 100; i += 20) {
            labelTable.put(i, new JLabel("" + (i / 100.0)));
        }
        observerSpeed.setLabelTable(labelTable);

        globalOptions.add(observerSpeed);

        optionsPanel.add(globalOptions);

        // Configure reference frame drop-down menu
        ReferenceFrameDataModel frameChooserModel = new ReferenceFrameDataModel(this);
        JComboBox<String> refrenceFrameChooser = new JComboBox<String>(new ReferenceFrameDataModel(this));
        refrenceFrameChooser.setSelectedItem("Rest Frame");
        refrenceFrameChooser.setEditable(true);
        optionsPanel.add(refrenceFrameChooser);

        // Configure object list
        JPanel objectListPanel = new JPanel();
        objectListPanel.setBorder(BorderFactory.createTitledBorder("Spacetime Objects"));

        JList<SpacetimeObject> objectList = new JList<>(objects);

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

        JButton addEventBtn = new JButton("Add Event");
        addEventBtn.addActionListener(e -> {
            objects.add(new SpacetimeObject("New Event", 0, 0));
        });

        JButton addTravellerBtn = new JButton("Add traveller");
        addTravellerBtn.addActionListener(e -> {
            objects.add(new SpacetimeTraveller("New Traveller", 0, 0, 0));
        });

        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(e -> {
            int i = objectList.getSelectedIndex();
            if (0 <= i && i < objects.size()) {
                objects.remove(i);
            }
        });

        objectListPanel.add(addEventBtn, listPnlGbc);
        listPnlGbc.gridx++;

        objectListPanel.add(addTravellerBtn, listPnlGbc);
        listPnlGbc.gridx++;

        objectListPanel.add(removeBtn, listPnlGbc);

        optionsPanel.add(objectListPanel);

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
        tField.addActionListener(e -> {
            try {
                double newT = Double.valueOf(tField.getText());
                objectList.getSelectedValue().setX(graph.getReferenceFrameBeta(), newT);

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

        objectList.addListSelectionListener(e -> {
            SpacetimeObject object = objectList.getSelectedValue();

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

            if (object instanceof SpacetimeTraveller) {
                betaLabel.setVisible(true);
                betaField.setVisible(true);

                betaField.setText(String.valueOf(((SpacetimeTraveller) object).getBeta(graph.getReferenceFrameBeta())));
            } else {
                betaLabel.setVisible(false);
                betaField.setVisible(false);
            }
        });

        observerSpeed.addChangeListener(e -> {
            // TODO redundant
            SpacetimeObject object = objectList.getSelectedValue();

            nameField.setText(object.getName());

            // TODO formatting
            xField.setText(String.valueOf(object.getX(graph.getReferenceFrameBeta())));
            tField.setText(String.valueOf(object.getT(graph.getReferenceFrameBeta())));

            if (object instanceof SpacetimeTraveller) {
                betaField.setText(String.valueOf(((SpacetimeTraveller) object).getBeta(graph.getReferenceFrameBeta())));
            }
        });

        optionsPanel.add(objSettingsPnl);

        // Add sidebar to window
        this.add(optionsPanel, BorderLayout.LINE_START);

        // Spacetime graph & panel

        JPanel graphPnl = new JPanel();
        graphPnl.setBorder(BorderFactory.createTitledBorder("Graph"));

        graph = new Diagram(objects);
        objects.addListDataListener(new ListDataListener() {

            @Override
            public void intervalAdded(ListDataEvent e) {
                graph.repaint();
            }

            @Override
            public void intervalRemoved(ListDataEvent e) {
                graph.repaint();
            }

            @Override
            public void contentsChanged(ListDataEvent e) {
                graph.repaint();
            }

        });

        graphPnl.add(graph);

        this.add(graphPnl, BorderLayout.LINE_END);

        // Auto-size gui & show it

        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }
}
