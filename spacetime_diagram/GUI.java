package spacetime_diagram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.AbstractList;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform.*;

public class GUI extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -1681071496023255137L;

    public HashMap<String, double[]> referenceFrames;

    private Diagram graph;

    private SpacetimeObjectList objects;

    public GUI() {
        super("Spacetime Diagram");

        referenceFrames = new HashMap<>();
        referenceFrames.put("Rest Frame", new double[] { 0, 0 });

        objects = new SpacetimeObjectList();

        // Set how the GUI closes
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the sidebar panel
        JPanel optionsPanel = new JPanel();
        JPanel globalOptions = new JPanel();
        JPanel itemEditor = new JPanel();

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
        ReferenceFrameDataModel frameChooserModel = new ReferenceFrameDataModel();
        JComboBox<String> refrenceFrameChooser = new JComboBox<String>(new ReferenceFrameDataModel());
        refrenceFrameChooser.setSelectedItem("Rest Frame");
        refrenceFrameChooser.setEditable(true);
        optionsPanel.add(refrenceFrameChooser);

        // Configure object list
        JPanel objectListPanel = new JPanel();
        objectListPanel.setBorder(BorderFactory.createTitledBorder("Spacetime Objects"));

        JList<SpacetimeObject> objectList = new JList<>(objects);

        objects.add(new SpacetimeTraveller("foo", 0.1, 0, 0));
        objects.add(new SpacetimeTraveller("bar", -0.6, 0, 50));
        objects.add(new SpacetimeObject("baz", 50, -50));

        objectListPanel.setLayout(new GridBagLayout());
        GridBagConstraints listPnlGbc = new GridBagConstraints();
        listPnlGbc.gridx = 0;
        listPnlGbc.gridy = 0;
        listPnlGbc.insets = new Insets(2, 2, 2, 2);
        listPnlGbc.fill = GridBagConstraints.BOTH;

        listPnlGbc.gridwidth = 3;
        objectListPanel.add(objectList, listPnlGbc);
        listPnlGbc.gridwidth = 1;
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

        // Add sidebar to window
        this.add(optionsPanel, BorderLayout.LINE_START);

        // Spacetime graph & panel

        JPanel graphPnl = new JPanel();
        graphPnl.setBorder(BorderFactory.createTitledBorder("Graph"));

        // Spacetime travellers
        ArrayList<SpacetimeObject> travellers = new ArrayList<>();
        travellers.add(new SpacetimeTraveller("foo", 0.1, 0, 0));
        travellers.add(new SpacetimeTraveller("bar", -0.6, 0, 50));
        travellers.add(new SpacetimeObject("baz", 50, -50));

        graph = new Diagram(travellers);

        graphPnl.add(graph);

        this.add(graphPnl, BorderLayout.LINE_END);

        // Auto-size gui & show it

        this.pack();
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new GUI();
    }

    private class ReferenceFrameDataModel implements ComboBoxModel<String> {
        private String selectedItem;

        private List<ListDataListener> dataListeners = new ArrayList<>();

        @Override
        public int getSize() {
            return referenceFrames.size();
        }

        @Override
        public String getElementAt(int index) {
            return referenceFrames.keySet().parallelStream().collect(Collectors.toList()).get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            dataListeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            dataListeners.add(l);
        }

        @Override
        public void setSelectedItem(Object anItem) {
            if (!(anItem instanceof String)) {
                throw new IllegalArgumentException("Selected item must be a string");
            } else if (!referenceFrames.containsKey(anItem)) {
                referenceFrames.put((String) anItem, new double[] { 0, 0 });
            }

            selectedItem = (String) anItem;
        }

        @Override
        public Object getSelectedItem() {
            return selectedItem;
        }
    }

    private class SpacetimeObjectList extends AbstractSequentialList<SpacetimeObject>
            implements ListModel<SpacetimeObject> {

        private ArrayList<SpacetimeObject> objects;
        private ArrayList<ListDataListener> listeners;

        public SpacetimeObjectList() {
            objects = new ArrayList<>();
            listeners = new ArrayList<>();
        }

        @Override
        public int getSize() {
            return objects.size();
        }

        @Override
        public SpacetimeObject getElementAt(int index) {
            return objects.get(index);
        }

        @Override
        public void addListDataListener(ListDataListener l) {
            listeners.add(l);
        }

        @Override
        public void removeListDataListener(ListDataListener l) {
            listeners.remove(l);
        }

        @Override
        public boolean add(SpacetimeObject obj) {
            if (objects.add(obj)) {
                ListDataEvent e = new ListDataEvent(obj, ListDataEvent.INTERVAL_ADDED, 0, objects.size() - 1);
                listeners.forEach(l -> l.intervalAdded(e));

                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean remove(Object obj) {
            if (objects.remove(obj)) {
                ListDataEvent e = new ListDataEvent(obj, ListDataEvent.INTERVAL_REMOVED, objects.size(),
                        objects.size());
                listeners.forEach(l -> l.intervalRemoved(e));

                return true;
            } else {
                return false;
            }
        }

        @Override
        public SpacetimeObject remove(int i) {
            SpacetimeObject o = objects.remove(i);

            if (o != null) {
                ListDataEvent e = new ListDataEvent(o, ListDataEvent.INTERVAL_ADDED, objects.size(), objects.size());
                listeners.forEach(l -> l.intervalRemoved(e));
            }

            return o;
        }

        @Override
        public SpacetimeObject get(int index) {
            return objects.get(index);
        }

        @Override
        public int size() {
            return objects.size();
        }

        @Override
        public ListIterator<SpacetimeObject> listIterator(int index) {
            return objects.listIterator(index);
        }

    }
}
