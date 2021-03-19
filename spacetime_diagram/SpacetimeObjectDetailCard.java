package spacetime_diagram;

import javax.swing.*;

import spacetime_diagram.LorentzTransform.SpacetimeObject;
import spacetime_diagram.LorentzTransform.SpacetimeTraveller;

import java.awt.*;

// This is an unnecessary and somewhat bloated thing I implemented because I thought it would fix a bug. But it didn't, but it's here now, so I'm not changing it.
public class SpacetimeObjectDetailCard extends JPanel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private JLabel betaLabel;

    private JTextField betaField;
    private JTextField xField;
    private JTextField tField;
    private JTextField nameField;

    private SpacetimeObject object;

    public SpacetimeObjectDetailCard(SpacetimeObject obj) {

        this.object = obj;

        this.setLayout(new GridBagLayout());

        GridBagConstraints objSettingsGbc = new GridBagConstraints();
        objSettingsGbc.gridx = objSettingsGbc.gridy = 0;
        objSettingsGbc.anchor = GridBagConstraints.FIRST_LINE_START;
        objSettingsGbc.fill = GridBagConstraints.HORIZONTAL;
        objSettingsGbc.insets = new Insets(2, 2, 2, 2);

        JLabel nameLabel = new JLabel("Name");
        nameLabel.setLabelFor(nameField);
        this.add(nameLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        nameField = new JTextField(10);
        objSettingsGbc.weightx = 1;
        this.add(nameField, objSettingsGbc);
        objSettingsGbc.weightx = 0;
        objSettingsGbc.fill = GridBagConstraints.NONE;
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel xLabel = new JLabel("x-Position");
        xLabel.setLabelFor(xField);
        this.add(xLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        xField = new JTextField(20);
        this.add(xField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel tLabel = new JLabel("Time");
        xLabel.setLabelFor(tField);
        this.add(tLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        tField = new JTextField(20);
        this.add(tField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        betaLabel = new JLabel("Beta");
        xLabel.setLabelFor(betaField);
        this.add(betaLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        betaField = new JTextField(20);
        this.add(betaField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        if (!(object instanceof SpacetimeTraveller)) {
            betaLabel.setVisible(false);
            betaField.setVisible(false);
        }

        objSettingsGbc.fill = GridBagConstraints.VERTICAL;
        objSettingsGbc.weighty = 1;
        this.add(Box.createVerticalGlue(), objSettingsGbc);
    }

    public void updateText(double beta) {
        nameField.setText(object.getName());

        // TODO formatting
        xField.setText(String.valueOf(object.getX(beta)));
        tField.setText(String.valueOf(object.getT(beta)));

        if (object instanceof SpacetimeTraveller) {
            betaField.setText(String.valueOf(((SpacetimeTraveller) object).getBeta(beta)));
        }
    }
}
