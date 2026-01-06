package elections.ui;

import elections.model.ElectionSystemManager;
import elections.model.Politician;
import elections.structures.MyArray;
import elections.structures.Sort;
import elections.util.SampleData;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * JavaFX screen for searching and browsing politicians.
 *
 * Users can:
 * - Filter by name substring, party, and/or county
 * - View all politicians sorted by name
 * - Double-click a result to open the politician details screen
 * - Load sample data for quick testing/demo purposes
 */
public class PoliticianSearchView extends BorderPane {

    // Manager provides access to data and operations (add/search/list)
    private final ElectionSystemManager manager;

    // Callback used to open the details view for a selected politician
    private final Consumer<Politician> onOpenPolitician;

    // Navigation callback to switch to the elections screen
    private final Runnable goElectionsScreen;

    // Filter inputs
    private final TextField nameField = new TextField();
    private final TextField partyField = new TextField();
    private final TextField countyField = new TextField();

    // List of results displayed on screen
    private final ListView<Politician> resultsList = new ListView<>();

    /**
     * Constructs the politician search view.
     *
     * @param manager           system manager providing access to the data model
     * @param onOpenPolitician  called when a politician result is opened (double-click)
     * @param goElectionsScreen navigation callback to switch to elections screen
     */
    public PoliticianSearchView(ElectionSystemManager manager,
                                Consumer<Politician> onOpenPolitician,
                                Runnable goElectionsScreen) {
        this.manager = manager;
        this.onOpenPolitician = onOpenPolitician;
        this.goElectionsScreen = goElectionsScreen;

        // Keep consistent spacing around the UI
        setPadding(new Insets(12));

        // Shared top bar provides navigation and quick-add actions
        TopBar bar = new TopBar(
                () -> {}, // already on politicians
                goElectionsScreen,
                () -> { Dialogs.showAddPolitician(manager); showAll(); }, // refresh after add
                () -> { Dialogs.showAddElection(manager); },
                () -> { Dialogs.showAddCandidate(manager); }
        );

        Label title = new Label("Search Politicians");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Filters are optional; leaving them blank means "no filter for that field"
        nameField.setPromptText("Name contains (optional)");
        partyField.setPromptText("Party (optional)");
        countyField.setPromptText("County (optional)");

        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");
        Button sampleBtn = new Button("Load Sample Data");

        // Filter row holds inputs and actions together
        HBox filters = new HBox(8, nameField, partyField, countyField, searchBtn, showAllBtn, sampleBtn);
        filters.setPadding(new Insets(8, 0, 8, 0));

        // Placeholder shown when there are no results
        resultsList.setPlaceholder(new Label("No results"));

        // Custom row formatting for each politician in the results list
        resultsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Politician p, boolean empty) {
                super.updateItem(p, empty);

                if (empty || p == null) {
                    setText(null);
                } else {
                    setText(p.getName() + " — " + p.getParty() + " (" + p.getCounty() + ")");
                }
            }
        });

        // Double-click opens the selected politician using the provided callback
        resultsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Politician selected = resultsList.getSelectionModel().getSelectedItem();
                if (selected != null) onOpenPolitician.accept(selected);
            }
        });

        // Wire up buttons
        searchBtn.setOnAction(e -> doSearch());
        showAllBtn.setOnAction(e -> showAll());

        // Sample data is useful for demos/testing without manual entry
        sampleBtn.setOnAction(e -> {
            SampleData.load(manager);
            showAll();
            new Alert(Alert.AlertType.INFORMATION, "Sample data loaded.").showAndWait();
        });

        // Build the top layout and place the results list in the center
        VBox top = new VBox(8, bar, title, filters);
        setTop(top);
        setCenter(resultsList);

        // Display current contents on first load
        showAll();
    }

    /**
     * Loads and displays all politicians, sorted alphabetically by name.
     */
    private void showAll() {
        MyArray<Politician> all = manager.getAllPoliticians();

        // Sorting here keeps the UI consistent for users
        Sort.sortPoliticiansByName(all);

        fillResults(all);
    }

    /**
     * Applies filters from the UI inputs and displays matching politicians.
     *
     * Filtering is performed manually over MyArray to keep the logic explicit
     * and compatible with the project's custom data structures.
     */
    private void doSearch() {
        String namePart = safe(nameField.getText());
        String party = safe(partyField.getText());
        String county = safe(countyField.getText());

        // Start from the complete dataset and filter in a single pass
        MyArray<Politician> all = manager.getAllPoliticians();
        MyArray<Politician> filtered = new MyArray<>();

        for (int i = 0; i < all.size(); i++) {
            Politician p = all.get(i);

            // Name filter is substring-based and case-insensitive
            if (!namePart.isEmpty() &&
                    !p.getName().toLowerCase().contains(namePart.toLowerCase())) continue;

            // Party and county filters are exact matches, case-insensitive
            if (!party.isEmpty() && !p.getParty().equalsIgnoreCase(party)) continue;
            if (!county.isEmpty() && !p.getCounty().equalsIgnoreCase(county)) continue;

            filtered.add(p);
        }

        // Present filtered results in a predictable order
        Sort.sortPoliticiansByName(filtered);
        fillResults(filtered);
    }

    /**
     * Copies values from a MyArray into the JavaFX ListView.
     *
     * ListView uses an ObservableList internally, so we manually add items.
     */
    private void fillResults(MyArray<Politician> data) {
        resultsList.getItems().clear();
        for (int i = 0; i < data.size(); i++) {
            resultsList.getItems().add(data.get(i));
        }
    }

    /**
     * Utility method for safely trimming user input.
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
