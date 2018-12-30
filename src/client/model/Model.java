package client.model;

import javax.swing.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Model {
    private OutlineListModel outlineListModel;

    public ListModel<String> getOutlineListModel() {
        return outlineListModel;
    }

    public static class OutlineListModel extends AbstractListModel<String> {
        private List<String> outlineList = new ArrayList<>();

        public void setOutlineList(List<String> outlineList) {
            this.outlineList = outlineList;
        }

        @Override
        public int getSize() {
            return outlineList.size();
        }

        @Override
        public String getElementAt(int index) {
            return outlineList.get(index);
        }
    }
}
