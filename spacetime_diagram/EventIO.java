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

public class EventIO {

    public static final int FILE_VERSION = 1;

    public static final List<Integer> OLD_VERSIONS;

    static {
        OLD_VERSIONS = List.of();
    }

    public static boolean saveSpacetimeEvents(File outFile, Collection<SpacetimeEvent> events) {

        FileOutputStream fOutStream;
        try {
            fOutStream = new FileOutputStream(outFile);

            try (ObjectOutputStream objOutStream = new ObjectOutputStream(fOutStream)) {
                objOutStream.writeInt(FILE_VERSION);
                objOutStream.writeInt(events.size());

                for (SpacetimeEvent evt : events) {
                    objOutStream.writeObject(evt);
                }

                return true;
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return false;
    }

    public static Collection<SpacetimeEvent> readSpacetimeEvents(File f) {
        try (FileInputStream fIn = new FileInputStream(f); ObjectInputStream objIn = new ObjectInputStream(fIn)) {
            int version = objIn.readInt();

            if (version != FILE_VERSION) {
                return null;
            }

            int n = objIn.readInt();

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

        return null;
    }
}
