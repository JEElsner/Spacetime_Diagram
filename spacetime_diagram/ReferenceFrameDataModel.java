package spacetime_diagram;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

class ReferenceFrameDataModel implements ComboBoxModel<String> {
    /**
     *
     */
    private final GUI gui;

    /**
     * @param gui
     */
    ReferenceFrameDataModel(GUI gui) {
        this.gui = gui;
    }

    private String selectedItem;

    private List<ListDataListener> dataListeners = new ArrayList<>();

    @Override
    public int getSize() {
        return this.gui.referenceFrames.size();
    }

    @Override
    public String getElementAt(int index) {
        return this.gui.referenceFrames.keySet().parallelStream().collect(Collectors.toList()).get(index);
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
        } else if (!this.gui.referenceFrames.containsKey(anItem)) {
            this.gui.referenceFrames.put((String) anItem, new double[] { 0, 0 });
        }

        selectedItem = (String) anItem;
    }

    @Override
    public Object getSelectedItem() {
        return selectedItem;
    }
}