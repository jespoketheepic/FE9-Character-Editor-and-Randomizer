package contents.gui.components;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

// Creates a CellFactory in a more readable form
// Extend this class for alternate implementations
public class CellFactory<T> implements Callback<ListView<T>, ListCell<T>> {

    @Override
    public ListCell<T> call(ListView<T> param) {

        // call() generates the Cells for the ListView
        return new ListCell<T>(){

            // updateItem is responsible for how the cell will be displayed
            // so we override it with how we want the cell to be displayed:
            @Override
            protected void updateItem(T item, boolean empty) {

                // Do the normal Cell.updateItem() things
                super.updateItem(item, empty);

                // This class only follows the defaults, extend it and override this method with changes right here
            }
        };
    }
}
