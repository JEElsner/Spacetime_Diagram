package spacetime_diagram;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform.SpacetimeObject;

class SpacetimeObjectListModel extends AbstractSequentialList<SpacetimeObject> implements ListModel<SpacetimeObject> {

    private ArrayList<SpacetimeObject> objects;
    private ArrayList<ListDataListener> listeners;

    public SpacetimeObjectListModel() {
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
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, objects.size() - 2,
                    objects.size() - 1);
            listeners.forEach(l -> l.intervalAdded(e));

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean remove(Object obj) {
        int i = objects.indexOf(obj);

        if (objects.remove(obj)) {
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
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
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
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