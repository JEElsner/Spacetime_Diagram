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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Defines and implements how spacetime diagrams are saved and read from file
 * 
 * @author Jonathan Elsner
 */
public class EventIO {

    /**
     * The current version of the save file that is written to disk. Incremented by
     * one each time a change is made to the structure of the save file.
     */
    public static final int FILE_VERSION = 1;

    /**
     * List of old file versions used in order to support backwards compatibility.
     * 
     * You know, I'm not sure why I'm doing this, if I just increment the current
     * file version by one when a change is made.
     */
    public static final List<Integer> OLD_VERSIONS;

    static {
        // OLD_VERSIONS is constructed in this way so that it is unmodifiable
        OLD_VERSIONS = List.of();
    }

    /**
     * Save a collection of events for a spacetime diagram to the specified file
     * 
     * @param outFile the file in which to save the spacetime events
     * @param events  the spacetime events to save
     * @return {@code true} if successfully saved, {@code false} otherwise
     */
    public static boolean saveSpacetimeEvents(File outFile, Collection<SpacetimeEvent> events) {

        FileOutputStream fOutStream;
        try {
            fOutStream = new FileOutputStream(outFile);

            try (ObjectOutputStream objOutStream = new ObjectOutputStream(fOutStream)) {
                objOutStream.writeInt(FILE_VERSION);

                // Write number of events for easier reading
                objOutStream.writeInt(events.size());

                // Write all the events
                for (SpacetimeEvent evt : events) {
                    objOutStream.writeObject(evt);
                }

                // Since we got here, save was successfull, so return true and leave
                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        // if we got here, something bad happened, let caller know we messed up
        return false;
    }

    /**
     * Read a collection of spacetime events to create a spacetime diagram out of
     * them
     * 
     * @param f the file from which to read the spacetime diagrams
     * @return the collection of spacetime events in the file
     */
    public static Collection<SpacetimeEvent> readSpacetimeEvents(File f) {
        // Let's do a better job coding here with try-with-resources instead of whatever
        // mess I wrote in the save method
        try (FileInputStream fIn = new FileInputStream(f); ObjectInputStream objIn = new ObjectInputStream(fIn)) {
            int version = objIn.readInt();

            // make sure the version of the file is compatible
            if (version != FILE_VERSION) {
                return null;
            }

            // read the number of spacetime events stored
            int n = objIn.readInt();

            // read the events
            ArrayList<SpacetimeEvent> events = new ArrayList<>(n);
            for (int i = 0; i < n; i++) {
                events.add((SpacetimeEvent) objIn.readObject());
            }

            return events;
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }

        // If we got here, something bad happened, and we don't have the objects from
        // the file
        return null;
    }
}
