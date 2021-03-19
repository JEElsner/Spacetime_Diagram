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

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * The main class of this program, where the GUI is created and shown.
 * 
 * @author Jonathan Elsner
 */
public class Main {
    /**
     * Create and run the Spacetime Diagram {@code GUI}
     * 
     * @param args Command-line arguments that do not change the behavior of the
     *             program
     * @see SpacetimeDiagramGUI
     * @see Diagram
     */
    public static void main(String[] args) {

        // Try to change the skin of the GUI to look like the OS the user is running.
        // If this fails, the GUI *should* still appear, but it will look like the
        // cross-platform Java skin.
        // Printing errors to std err should be fine for this
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        // Create and show the GUI
        SwingUtilities.invokeLater(SpacetimeDiagramGUI::new);
    }
}
