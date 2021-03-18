package spacetime_diagram;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import LorentzTransform.SpacetimeObject;
import LorentzTransform.SpacetimeTraveller;

public class ObjectList extends JScrollPane {

    /**
     *
     */
    private static final long serialVersionUID = -7022691518044099639L;

    private JPanel insidePane;

    public ObjectList() {

        this.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        this.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        insidePane = new JPanel();
    }

    private JPanel createListing(SpacetimeObject sto) {
        JPanel listing = new JPanel();
        listing.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        listing.add(new JLabel(sto.getName()));

        if (sto instanceof SpacetimeTraveller) {
            SpacetimeTraveller traveller = (SpacetimeTraveller) sto;

            listing.add(new JLabel("\u03B2 = "));

            JTextField betaPane = new JTextField("0", 3);
            betaPane.addActionListener(evt -> traveller.setBeta(Double.valueOf(betaPane.getText())));
            listing.add(betaPane);
        }
    }
}
