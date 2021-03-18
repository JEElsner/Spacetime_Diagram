package spacetime_diagram;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform.*;

public class GUI extends JFrame {
    /**
     *
     */
    private static final long serialVersionUID = -1681071496023255137L;

    public HashMap<String, double[]> referenceFrames;

    public GUI() {
        super("Spacetime Diagram");

        referenceFrames = new HashMap<>();
        referenceFrames.put("Rest Frame", new double[] { 0, 0 });

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel optionsPanel = new JPanel();
        JPanel globalOptions = new JPanel();
        JPanel itemEditor = new JPanel();

        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.PAGE_AXIS));

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

        optionsPanel.add(globalOptions);

        ReferenceFrameDataModel frameChooserModel = new ReferenceFrameDataModel();
        JComboBox<String> refrenceFrameChooser = new JComboBox<String>(new ReferenceFrameDataModel());
        refrenceFrameChooser.setSelectedItem("Rest Frame");
        refrenceFrameChooser.setEditable(true);
        optionsPanel.add(refrenceFrameChooser);

        itemEditor.setBorder(BorderFactory.createTitledBorder("Reference Frame Information"));

        itemEditor.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;
        gbc.insets = new Insets(0, 0, 0, 10);

        itemEditor.add(new JLabel("\u03B2 ="), gbc);
        gbc.gridx++;

        JTextField betaTextField = new JTextField("0", 3);
        itemEditor.add(betaTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;

        itemEditor.add(new JLabel("Intersect Point:"), gbc);
        gbc.gridx++;

        gbc.insets = new Insets(0, 0, 0, 0);
        itemEditor.add(new JLabel("t = "), gbc);
        gbc.gridx++;
        gbc.insets = new Insets(0, 0, 0, 10);

        JTextField refFrameTTextField = new JTextField("0", 3);
        itemEditor.add(refFrameTTextField, gbc);
        gbc.gridx++;

        gbc.insets = new Insets(0, 0, 0, 0);
        itemEditor.add(new JLabel("x = "), gbc);
        gbc.gridx++;

        JTextField refFrameXTextField = new JTextField("0", 3);
        itemEditor.add(refFrameXTextField, gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 10);

        optionsPanel.add(itemEditor);

        this.add(optionsPanel, BorderLayout.LINE_START);

        JPanel graphPnl = new JPanel();
        graphPnl.setBorder(BorderFactory.createTitledBorder("Graph"));

        // Spacetime travellers
        ArrayList<SpacetimeObject> travellers = new ArrayList<>();
        travellers.add(new SpacetimeTraveller("foo", 0.1, 0, 0));
        travellers.add(new SpacetimeTraveller("bar", -0.6, 0, 50));
        travellers.add(new SpacetimeObject("baz", 50, -50));

        graphPnl.add(new Diagram(travellers));

        this.add(graphPnl, BorderLayout.LINE_END);

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
}
