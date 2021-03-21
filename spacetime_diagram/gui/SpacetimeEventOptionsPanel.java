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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import spacetime_diagram.LorentzTransform;
import spacetime_diagram.SpacetimeEvent;
import spacetime_diagram.SpacetimeTraveller;

/**
 * A specialized panel to show the properties of a {@code SpacetimeEvent}
 * 
 * @author Jonathan Elsner
 * 
 * @see SpacetimeEvent
 */
public class SpacetimeEventOptionsPanel extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The {@code ActionListener}s listening for actions in this panel
     * 
     * @see ActionListener
     */
    private List<ActionListener> actionListeners;

    /**
     * The {@code SpacetimeEvent} whose properties are shown in this panel
     * 
     * @see SpacetimeEvent
     */
    private SpacetimeEvent currentEvent;

    /**
     * The fraction of the speed of light at which the observer observing the
     * represented event is travelling.
     * 
     * @see LorentzTransform
     */
    private double referenceFrameBeta;

    /**
     * The field storing the current event's name
     */
    private JTextField nameField;

    /**
     * The field storing the current event's x-coordinate
     */
    private JTextField xField;

    /**
     * The field storing the current event's t-coordinate
     */
    private JTextField tField;

    /**
     * The field storing the current event's speed as a fraction of the speed of
     * light, if the current event is a SpacetimeTraveller
     */
    private JTextField betaField;

    /**
     * The label for the {@code betaField}
     */
    private JLabel betaLabel;

    /**
     * Constructs a SpacetimeEventOptionsPanel
     */
    public SpacetimeEventOptionsPanel() {
        // Create array for ActionListeners
        actionListeners = new ArrayList<>();

        // Construct visual elements of panel

        this.setBorder(BorderFactory.createTitledBorder("Selected Event/Traveller"));
        this.setLayout(new GridBagLayout());

        GridBagConstraints objSettingsGbc = new GridBagConstraints();
        objSettingsGbc.gridx = objSettingsGbc.gridy = 0;
        objSettingsGbc.anchor = GridBagConstraints.FIRST_LINE_START;
        objSettingsGbc.fill = GridBagConstraints.HORIZONTAL;
        objSettingsGbc.insets = new Insets(2, 2, 2, 2);

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField(10);

        nameField.setEnabled(false);
        nameField.setToolTipText("Rename the selected element");
        nameField.addActionListener(evt -> {
            currentEvent.setName(nameField.getText());
            ActionEvent lEvt = new ActionEvent(this, evt.getID(), "name");
            actionListeners.forEach(l -> l.actionPerformed(lEvt));
        });

        nameLabel.setLabelFor(nameField);
        this.add(nameLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        objSettingsGbc.weightx = 1;
        this.add(nameField, objSettingsGbc);
        objSettingsGbc.weightx = 0;
        objSettingsGbc.fill = GridBagConstraints.NONE;
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel xLabel = new JLabel("x-Position");
        xField = new JTextField(20);
        xField.setEnabled(false);
        xField.setToolTipText("Set the x-coordinate of the selected element");
        xField.setFont(SpacetimeDiagramGUI.MONOSPACE_FONT.deriveFont(Font.PLAIN, xField.getFont().getSize()));
        xField.addActionListener(e -> {
            try {
                double newX = Double.valueOf(xField.getText());
                currentEvent.setX(referenceFrameBeta, newX);

                ActionEvent lEvt = new ActionEvent(this, e.getID(), "x");
                actionListeners.forEach(l -> l.actionPerformed(lEvt));
            } catch (NumberFormatException ex) {
                xField.setText("" + currentEvent.getX(referenceFrameBeta));
            }
        });

        xLabel.setLabelFor(xField);
        this.add(xLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        this.add(xField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        JLabel tLabel = new JLabel("Time");
        tField = new JTextField(20);

        tField.setEnabled(false);
        tField.setToolTipText("Set the t-coordinate of the selected element");
        tField.setFont(SpacetimeDiagramGUI.MONOSPACE_FONT.deriveFont(Font.PLAIN, tField.getFont().getSize()));
        tField.addActionListener(e -> {
            try {
                double newT = Double.valueOf(tField.getText());
                currentEvent.setT(referenceFrameBeta, newT);

                ActionEvent lEvt = new ActionEvent(this, e.getID(), "t");
                actionListeners.forEach(l -> l.actionPerformed(lEvt));
            } catch (NumberFormatException ex) {
                tField.setText("" + currentEvent.getT(referenceFrameBeta));
            }
        });

        tLabel.setLabelFor(tField);
        this.add(tLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        this.add(tField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        betaLabel = new JLabel("Beta");
        betaField = new JTextField(20);

        betaField.setEnabled(false);
        betaField.setToolTipText("Set the beta value for the selected worldline");
        betaField.setFont(SpacetimeDiagramGUI.MONOSPACE_FONT.deriveFont(Font.PLAIN, betaField.getFont().getSize()));
        betaField.addActionListener(e -> {
            SpacetimeTraveller traveller = (SpacetimeTraveller) currentEvent;

            try {
                double newBeta = Double.valueOf(betaField.getText());
                betaField.setText("" + traveller.setBeta(referenceFrameBeta, newBeta));

                ActionEvent lEvt = new ActionEvent(this, e.getID(), "beta");
                actionListeners.forEach(l -> l.actionPerformed(lEvt));
            } catch (NumberFormatException ex) {
                betaField.setText("" + traveller.getBeta(referenceFrameBeta));
            }
        });

        betaLabel.setLabelFor(betaField);
        this.add(betaLabel, objSettingsGbc);
        objSettingsGbc.gridx++;

        this.add(betaField, objSettingsGbc);
        objSettingsGbc.gridx = 0;
        objSettingsGbc.gridy++;

        objSettingsGbc.fill = GridBagConstraints.VERTICAL;
        objSettingsGbc.weighty = 1;
        this.add(Box.createVerticalGlue(), objSettingsGbc);
    }

    /**
     * Make sure that the text in the fields is accurate for the current event and
     * reference frame speed
     */
    private void updateValues() {
        if (currentEvent == null) {
            return;
        }

        nameField.setText(currentEvent.getName());

        // TODO formatting
        xField.setText(String.valueOf(currentEvent.getX(referenceFrameBeta)));
        tField.setText(String.valueOf(currentEvent.getT(referenceFrameBeta)));

        // Only update beta field if a traveller with a speed is selected
        if (currentEvent instanceof SpacetimeTraveller) {
            betaField.setText(String.valueOf(((SpacetimeTraveller) currentEvent).getBeta(referenceFrameBeta)));
        }
    }

    /**
     * Set the {@code SpacetimeEvent} represented by this panel and update the panel
     * fields
     * 
     * @param evt The {@code SpacetimeEvent} to be represented by this panel
     */
    public void setCurrentEvent(SpacetimeEvent evt) {
        currentEvent = evt;

        // Disable panel when no object is selected in the list
        if (currentEvent == null) {
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

            updateValues();
        }

        // Only show the beta field if a traveller that can have a speed is selected
        if (currentEvent instanceof SpacetimeTraveller) {
            betaLabel.setVisible(true);
            betaField.setVisible(true);
        } else {
            betaLabel.setVisible(false);
            betaField.setVisible(false);
        }
    }

    /**
     * Returns the event represented by this panel
     * 
     * @return The {@code SpacetimeEvent} represented by this panel
     */
    public SpacetimeEvent getCurrentEvent() {
        return currentEvent;
    }

    /**
     * Sets the speed of the reference frame viewing the object represented by this
     * panel.
     * 
     * @param beta The speed of the observer as a fraction of the speed of light
     * 
     * @throws IllegalArgumentException if {@code beta > 1}, i.e. faster than the
     *                                  speed of light
     */
    public void setReferenceFrameBeta(double beta) {
        if (Math.abs(beta) > 1) {
            throw new IllegalArgumentException("Beta > 1: " + beta);
        }

        referenceFrameBeta = beta;
        updateValues();
    }

    /**
     * Returns the speed of the reference frame viewing the represented object
     * 
     * @return the fraction of the speed of light at which the observer is
     *         travelling
     */
    public double getReferenceFrameBeta() {
        return referenceFrameBeta;
    }

    /**
     * Adds an {@code ActionListener} to the panel
     * 
     * @param l The {@code ActionListener} to add
     * 
     * @see ActionListener
     */
    public void addActionListener(ActionListener l) {
        actionListeners.add(l);
    }

    /**
     * Removes the specified {@code ActionListener} from this panel
     * 
     * @param l The {@code ActionListener} to remove
     */
    public void removePropertyChangeListener(ActionListener l) {
        actionListeners.remove(l);
    }
}
