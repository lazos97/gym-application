
package main.helpers;
import javafx.collections.ObservableList;

@FunctionalInterface
public interface UIUpdater {
    void updateUI(ObservableList<String> rowData);
}
