package spacetime_diagram;

import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.ListIterator;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import spacetime_diagram.LorentzTransform.SpacetimeObject;

/**
 * Stores and tracks changes to the Spacetime objects shown in the diagram. This
 * custom list is used so that changes to this list fire updates in the GUI that
 * is used to display information about the objects in this list.
 * 
 * @apiNote using functions to manipulate the stored elements other than the
 *          ones implemented here may not properly fire {@code ListDataEvent}s
 *          as necessary.
 * 
 * @author Jonathan Elsner
 * @see SpacetimeObject
 * @see GUI
 * @see Diagram
 * @see ListDataEvent
 */
class SpacetimeObjectListModel extends AbstractSequentialList<SpacetimeObject> implements ListModel<SpacetimeObject> {

    /**
     * The actual list of objects this ListModel stores
     * 
     * @see SpacetimeObject
     * @see SpacetimeTraveller
     */
    private ArrayList<SpacetimeObject> objects;

    /**
     * The listeners waiting for changes to this list
     * 
     * @see ListDataListener
     */
    private ArrayList<ListDataListener> listeners;

    /**
     * Constructs the {@code SpacetimeObjectListModel}
     */
    public SpacetimeObjectListModel() {
        objects = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    /**
     * Returns the number of {@code SpacetimeObject}s in this list.
     * 
     * @return the number of {@code SpacetimeObject}s in this list
     */
    @Override
    public int getSize() {
        return objects.size();
    }

    /**
     * Returns the {@code SpacetimeObject} at the selected {@code index}
     * 
     * @param index The index at which to retrieve the item. Must be greater than
     *              {@code 0} and less than {@code getSize()}
     * 
     * @return the {@code SpacetimeObject} at the {@code index}
     * @throws IndexOutOfBoundsException if the index is out of range
     * 
     * @see SpacetimeObject
     */
    @Override
    public SpacetimeObject getElementAt(int index) {
        return objects.get(index);
    }

    /**
     * Add a {@code ListDataListener} for {@code ListDataEvents} on this list.
     * 
     * @param l The {@code ListDataListener} to add
     * 
     * @see ListDataListener
     * @see ListDataEvent
     */
    @Override
    public void addListDataListener(ListDataListener l) {
        listeners.add(l);
    }

    /**
     * Remove a {@code ListDataListener} for {@code ListDataEvents} on this list.
     * 
     * @param l The {@code ListDataListener} to remove
     * 
     * @see ListDataListener
     * @see ListDataEvent
     */
    @Override
    public void removeListDataListener(ListDataListener l) {
        listeners.remove(l);
    }

    /**
     * Appends the specified object at the end of this list and notify
     * {@code ListDataListener}s of this change.
     * 
     * @param obj The {@code SpacetimeObject} to add
     * 
     * @return {@code true} if the object was successfully added, {@code false}
     *         otherwise
     */
    @Override
    public boolean add(SpacetimeObject obj) {
        if (objects.add(obj)) {

            // Construct event and notify listeners
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, objects.size() - 1,
                    objects.size() - 1);
            listeners.forEach(l -> l.intervalAdded(e));

            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove the object from the list and notify {@code ListDataListener}s of the
     * change.
     * 
     * @param obj The object to remove
     * @return {@code true} if the removal was successful, {@code false} otherwise
     */
    @Override
    public boolean remove(Object obj) {
        int i = objects.indexOf(obj);

        if (objects.remove(obj)) {

            // Construct and notify listeners
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
            listeners.forEach(l -> l.intervalRemoved(e));

            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove the {@code SpacetimeObject} at the selected index.
     * 
     * @param i the index at which to remove an item.
     * @return the {@code SpacetimeObject} that was removed, or {@code null} if
     *         nothing was removed
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public SpacetimeObject remove(int i) {
        SpacetimeObject o = objects.remove(i);

        if (o != null) {
            // Construct event and notify listeners
            ListDataEvent e = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, i, i);
            listeners.forEach(l -> l.intervalRemoved(e));
        }

        return o;
    }

    /**
     * Let {@code ListDataListeners} know that an object in the list changed. Useful
     * for updating a JList that displays this data
     * 
     * @param object The object that was changed
     * 
     * @throws IllegalArgumentException if the specified object is not in this list
     * 
     * @see javax.swing.JList
     * @see GUI
     */
    public void fireChangeEvent(SpacetimeObject object) {
        if (!objects.contains(object)) {
            throw new IllegalArgumentException("Object is not in this list");
        }

        int i = objects.indexOf(object);
        fireChangeEvent(i);
    }

    /**
     * Let {@code ListDataListeners} know that an object in the list changed. Useful
     * for updating a JList that displays this data
     * 
     * @param index The index of the object that was changed
     * 
     * @throws IndexOutOfBoundsException if the index is out of range
     * 
     * @see javax.swing.JList
     * @see GUI
     */
    public void fireChangeEvent(int index) {
        if (index < 0 || size() <= index) {
            throw new IndexOutOfBoundsException(index);
        }

        ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, index, index);
        listeners.forEach(l -> l.contentsChanged(e));
    }

    /**
     * Get the element at the specified index
     * 
     * @param index The index at which to get the element
     * 
     * @return the {@code SpacetimeObject} at the selected index
     * 
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    @Override
    public SpacetimeObject get(int index) {
        return objects.get(index);
    }

    /**
     * Returns the number of {@code SpacetimeObjects}s in this list
     * 
     * @return the number of {@code SpacetimeObjects}s in this list
     */
    @Override
    public int size() {
        return objects.size();
    }

    /**
     * Returns a list iterator over the sequence (in order) starting at the
     * specified index.
     * 
     * @param index the index at which to start the iterator
     * @return The iterator starting at the specified index
     * 
     * @throws IndexOutOfBoundsException if the index specified is out of range
     * 
     * @see List.listIterator()
     */
    @Override
    public ListIterator<SpacetimeObject> listIterator(int index) {
        return objects.listIterator(index);
    }

}