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

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;

/**
 * Thanks to this post for how to create hyperlink-like buttons in a Swing:
 * {@link https://stackoverflow.com/questions/527719/how-to-add-hyperlink-in-jlabel}
 */
public class AboutPanel extends JPanel {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public AboutPanel() {
        setLayout(new GridBagLayout());

        JLabel authorLabel = new JLabel("Author:");
        JLabel sourceCodeLabel = new JLabel("Source Code:");
        JLabel copyrightLabel = new JLabel("Copyright:");
        JLabel copyrightYearLabel = new JLabel("Copyright Year:");
        JLabel inspirationLabel = new JLabel("Inspiration:");

        JButton authorBtn = constructHyperlinkBtn("Jonathan Elsner", "https://github.com/JEElsner");
        JButton sourceCodeBtn = constructHyperlinkBtn("GitHub", "https://github.com/JEElsner/Spacetime_Diagram");
        JButton copyrightBtn = constructHyperlinkBtn("GPL-3.0 License",
                "https://github.com/JEElsner/Spacetime_Diagram/blob/main/LICENSE");

        String chars = new String(new int[] { 104, 116, 116, 112, 115, 58, 47, 47, 121, 111, 117, 116, 117, 46, 98, 101,
                47, 100, 81, 119, 52, 119, 57, 87, 103, 88, 99, 81 }, 0, 28);
        JButton inspirationBtn = constructHyperlinkBtn("Rick", chars);
        inspirationBtn.setToolTipText(null);

        JLabel copyrightYear = new JLabel("2021");

        authorLabel.setLabelFor(authorBtn);
        sourceCodeLabel.setLabelFor(sourceCodeBtn);
        copyrightLabel.setLabelFor(copyrightBtn);
        copyrightYearLabel.setLabelFor(copyrightYear);
        inspirationLabel.setLabelFor(inspirationBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.LINE_END;

        add(authorLabel, gbc);
        gbc.gridy++;

        add(sourceCodeLabel, gbc);
        gbc.gridy++;

        add(copyrightLabel, gbc);
        gbc.gridy++;

        add(copyrightYearLabel, gbc);
        gbc.gridy++;

        add(inspirationLabel, gbc);
        gbc.gridy++;

        gbc.anchor = GridBagConstraints.LINE_START;
        gbc.gridx++;
        gbc.gridy = 0;

        add(authorBtn, gbc);
        gbc.gridy++;

        add(sourceCodeBtn, gbc);
        gbc.gridy++;

        add(copyrightBtn, gbc);
        gbc.gridy++;

        gbc.insets = authorBtn.getInsets();
        add(copyrightYear, gbc);
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridy++;

        add(inspirationBtn, gbc);
        gbc.gridy++;
    }

    /**
     * Open a URI using system methods
     * 
     * @param uri The URI to open
     */
    private void openURI(URI uri) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(uri);
            } else {
                // Yes, I am aware the catch block will catch this
                // I don't want the program to crash because of this dumb error. I just want to
                // make debugging easier
                throw new RuntimeException(
                        "Could not open URI since Desktop is not supported. TBH, I'm not really sure how we got here in a GUI if a Desktop is not supported.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private JButton constructHyperlinkBtn(String text, URI uri) {
        JButton btn = new JButton(text);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setForeground(Color.blue);
        btn.setOpaque(false);
        btn.setBorderPainted(false);
        btn.setToolTipText(uri.toString());

        btn.addActionListener(evt -> {
            openURI(uri);
        });

        return btn;
    }

    private JButton constructHyperlinkBtn(String text, String uri) {
        try {
            return constructHyperlinkBtn(text, new URI(uri));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }
}
