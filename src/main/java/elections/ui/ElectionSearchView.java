package elections.ui;

import elections.model.Election;
import elections.model.ElectionSystemManager;
import elections.structures.MyArray;
import elections.structures.Sort;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JavaFX screen for searching and browsing elections.
 *
 * Users can:
 * - Filter elections by type and/or year
 * - Sort results by year (ascending/descending)
 * - Double-click a result to open the election details screen
 */
public class ElectionSearchView extends BorderPane {

    /**
     * Callback interface for opening a specific election from a search result.
     * Using a functional interface keeps navigation logic outside the view.
     */
    @FunctionalInterface
    public interface OpenElection {
        void open(String type, int year, String location);
    }

    // Core manager providing access to elections and search data
    private final ElectionSystemManager manager;

    // Navigation callback to open a selected election
    private final OpenElection onOpenElection;

    // Navigation callback to switch screens (politicians screen)
    private final Runnable goPoliticiansScreen;

    // UI controls for filtering and sorting
    private final TextField typeField = new TextField();
    private final TextField yearField = new TextField();
    private final ComboBox<String> sortBox = new ComboBox<>();

    // ListView to display election results
    private final ListView<Election> resultsList = new ListView<>();

    /**
     * Constructs the election search view.
     *
     * @param manager            access to stored elections
     * @param onOpenElection     called when a result is opened (double-click)
     * @param goPoliticiansScreen navigation callback to switch to politicians screen
     */
    public ElectionSearchView(ElectionSystemManager manager,
                              OpenElection onOpenElection,
                              Runnable goPoliticiansScreen) {
        this.manager = manager;
        this.onOpenElection = onOpenElection;
        this.goPoliticiansScreen = goPoliticiansScreen;

        // Padding around the main layout
        setPadding(new Insets(12));

        // Shared top bar used across screens for navigation and quick actions
        TopBar bar = new TopBar(
                goPoliticiansScreen,
                () -> {}, // already on elections
                () -> { Dialogs.showAddPolitician(manager); },
                () -> { Dialogs.showAddElection(manager); showAll(); }, // refresh after adding
                () -> { Dialogs.showAddCandidate(manager); }
        );

        Label title = new Label("Search Elections");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Optional filter inputs: leaving them blank means "don't filter by that field"
        typeField.setPromptText("Type (optional) e.g. General");
        yearField.setPromptText("Year (optional) e.g. 2024");

        // Sorting options control how results are presented
        sortBox.getItems().addAll("Year (Ascending)", "Year (Descending)");
        sortBox.getSelectionModel().select(0);

        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");

        // Filter row: fields + sort selector + action buttons
        HBox filters = new HBox(8, typeField, yearField, sortBox, searchBtn, showAllBtn);
        filters.setPadding(new Insets(8, 0, 8, 0));

        // Placeholder text when there are no results to display
        resultsList.setPlaceholder(new Label("No results"));

        // Custom cell display controls how each Election appears in the results list
        resultsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Election e, boolean empty) {
                super.updateItem(e, empty);

                if (empty || e == null) {
                    setText(null);
                } else {
                    setText(e.getType() + " — " + e.getLocation()
                            + " (" + e.getYear() + "), winners: " + e.getNumberOfWinners());
                }
            }
        });

        // Double-click to open the selected election
        resultsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Election selected = resultsList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    onOpenElection.open(selected.getType(), selected.getYear(), selected.getLocation());
                }
            }
        });

        // Wire up actions
        searchBtn.setOnAction(e -> doSearch());
        showAllBtn.setOnAction(e -> showAll());

        VBox top = new VBox(8, bar, title, filters);
        setTop(top);
        setCenter(resultsList);

        // Populate the list on first load so the screen is not empty
        showAll();
    }

    /**
     * Displays all elections currently stored, applying the selected sort option.
     */
    private void showAll() {
        MyArray<Election> all = manager.getAllElections();
        applySort(all);
        fillResults(all);
    }

    /**
     * Applies filter criteria from the UI fields and displays matching elections.
     *
     * Filtering is done manually over MyArray to keep the logic explicit and
     * compatible with the custom data structures used in the project.
     */
    private void doSearch() {
        String type = safe(typeField.getText());
        String yearStr = safe(yearField.getText());

        // Year is optional; parse only if a value is provided
        Integer year = null;
        if (!yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException ex) {
                new Alert(Alert.AlertType.ERROR, "Year must be a number.").showAndWait();
                return;
            }
        }

        // Start with all elections and apply filters in a single pass
        MyArray<Election> all = manager.getAllElections();
        MyArray<Election> filtered = new MyArray<>();

        for (int i = 0; i < all.size(); i++) {
            Election e = all.get(i);

            // Type filter (case-insensitive) if the user provided a value
            if (!type.isEmpty() && !e.getType().equalsIgnoreCase(type)) continue;

            // Year filter if the user provided a year
            if (year != null && e.getYear() != year) continue;

            filtered.add(e);
        }

        applySort(filtered);
        fillResults(filtered);
    }

    /**
     * Sorts the provided list of elections based on the current sort selection.
     *
     * Sorting is applied to the existing MyArray in-place.
     */
    private void applySort(MyArray<Election> arr) {
        String selected = sortBox.getSelectionModel().getSelectedItem();

        if ("Year (Descending)".equals(selected)) {
            Sort.sortElectionsByYearDesc(arr);
        } else {
            // Default is ascending if the selection is missing or unknown
            Sort.sortElectionsByYearAsc(arr);
        }
    }

    /**
     * Copies the data from MyArray into the JavaFX ListView.
     *
     * ListView uses an ObservableList internally, so we manually transfer elements.
     */
    private void fillResults(MyArray<Election> data) {
        resultsList.getItems().clear();
        for (int i = 0; i < data.size(); i++) {
            resultsList.getItems().add(data.get(i));
        }
    }

    /**
     * Utility method for safely trimming strings from text fields.
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }
}

