package elections.ui;

import elections.model.CandidateEntry;
import elections.model.Election;
import elections.model.ElectionSystemManager;
import elections.structures.MyArray;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * JavaFX screen for viewing one election and its candidates.
 *
 * This view:
 * - Loads the election by (type, year, location)
 * - Displays election metadata
 * - Lists candidates sorted by votes (descending)
 * - Allows deleting the election
 * - Allows removing a candidate from the election
 */
public class ElectionDetailsView extends BorderPane {

    // ListView renders candidate entries for the selected election
    private final ListView<CandidateEntry> list = new ListView<>();

    /**
     * Creates the election details view for a specific election identifier.
     *
     * @param manager             access to application data and operations
     * @param type                election type identifier
     * @param year                election year identifier
     * @param location            election location identifier
     * @param goBackToElections    callback to return to elections screen
     * @param goPoliticiansScreen  callback to switch to politicians screen
     */
    public ElectionDetailsView(ElectionSystemManager manager,
                               String type, int year, String location,
                               Runnable goBackToElections,
                               Runnable goPoliticiansScreen) {

        // Padding gives consistent spacing around the screen content
        setPadding(new Insets(12));

        // Shared navigation/action bar used across screens
        TopBar bar = new TopBar(
                goPoliticiansScreen,
                goBackToElections,
                () -> { Dialogs.showAddPolitician(manager); },
                () -> { Dialogs.showAddElection(manager); },
                () -> { Dialogs.showAddCandidate(manager); }
        );

        // Local back button for convenience (in addition to any top navigation)
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> goBackToElections.run());

        Label title = new Label("Election Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Fetch the election once and use it throughout the view
        Election election = manager.getElection(type, year, location);

        // If the election does not exist, show a clear message and exit early
        if (election == null) {
            setTop(new VBox(8, bar, new HBox(8, backBtn, title)));
            setCenter(new Label("Election not found: " + type + " " + location + " " + year));
            return;
        }

        // Button to delete the currently viewed election
        Button deleteElectionBtn = new Button("Delete Election");
        deleteElectionBtn.setOnAction(e -> {

            // Confirmation dialog to avoid accidental deletion
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Delete election '" + election.getType() + " — " + election.getLocation() + " (" + election.getYear() + ")'?\n"
                            + "This cannot be undone.",
                    ButtonType.YES, ButtonType.NO
            );
            confirm.setHeaderText("Confirm Deletion");

            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {

                    // Deletion is handled by the manager; this view only triggers it
                    boolean ok = manager.deleteElection(election.getType(), election.getYear(), election.getLocation());

                    if (!ok) {
                        new Alert(Alert.AlertType.ERROR, "Delete failed.").showAndWait();
                    } else {
                        // After successful deletion, navigate away from this screen
                        goBackToElections.run();
                    }
                }
            });
        });

        // Top section: navigation bar + heading row with actions
        setTop(new VBox(8, bar, new HBox(8, backBtn, title, deleteElectionBtn)));

        // Metadata label summarises the election details
        Label meta = new Label(
                "Type: " + election.getType()
                        + " | Location: " + election.getLocation()
                        + " | Year: " + election.getYear()
                        + " | Winners/Seats: " + election.getNumberOfWinners()
        );
        meta.setStyle("-fx-font-weight: bold;");

        // Shown when the election currently has no candidates recorded
        list.setPlaceholder(new Label("No candidates recorded."));

        // Button to remove whichever candidate is currently selected in the list
        Button removeCandidateBtn = new Button("Remove Selected Candidate");
        removeCandidateBtn.setOnAction(e -> {

            // Only allow removal if an item is selected
            CandidateEntry selected = list.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.INFORMATION, "Select a candidate first.").showAndWait();
                return;
            }

            String polName = selected.getPolitician().getName();

            // Confirmation dialog to avoid accidental removal
            Alert confirm = new Alert(
                    Alert.AlertType.CONFIRMATION,
                    "Remove '" + polName + "' from this election?\nThis cannot be undone.",
                    ButtonType.YES, ButtonType.NO
            );
            confirm.setHeaderText("Confirm Removal");

            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    // Deletion updates both sides of the relationship (election + politician) inside the manager
                    boolean ok = manager.deleteCandidate(
                            polName,
                            election.getType(),
                            election.getYear(),
                            election.getLocation()
                    );

                    if (!ok) {
                        new Alert(Alert.AlertType.ERROR, "Remove failed.").showAndWait();
                    } else {
                        // Refresh list so the UI reflects the new state immediately
                        refreshList(manager, election);
                    }
                }
            });
        });

        // Initial population of the list
        refreshList(manager, election);

        // Main content layout for the screen
        setCenter(new VBox(
                10,
                meta,
                new Label("Candidates (sorted by votes desc):"),
                list,
                removeCandidateBtn
        ));
    }

    /**
     * Reloads the candidate list from the manager and rebuilds the ListView contents.
     *
     * Candidates are retrieved in sorted order (votes descending) and then displayed
     * with a custom cell format. The top "winners" are highlighted based on the
     * election's numberOfWinners.
     */
    private void refreshList(ElectionSystemManager manager, Election election) {

        // Manager returns a sorted copy so the UI doesn't mutate the election's stored order
        MyArray<CandidateEntry> sorted = manager.getCandidatesSortedByVotes(
                election.getType(),
                election.getYear(),
                election.getLocation()
        );

        // Convert MyArray into the observable list used by JavaFX
        list.getItems().clear();
        for (int i = 0; i < sorted.size(); i++) {
            list.getItems().add(sorted.get(i));
        }

        // Used to determine which rows should be visually highlighted
        int winners = election.getNumberOfWinners();

        // Custom cell rendering controls what text is shown per candidate
        // and applies styling for winners.
        list.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CandidateEntry ce, boolean empty) {
                super.updateItem(ce, empty);

                if (empty || ce == null) {
                    // Important: reset text/style when the cell is reused by JavaFX
                    setText(null);
                    setStyle("");
                } else {
                    // ListCell#getIndex corresponds to the position in the ListView
                    int idx = getIndex();
                    boolean isWinner = idx >= 0 && idx < winners;

                    // Display includes ranking, name, party-at-the-time, and votes
                    String line = (idx + 1) + ". "
                            + ce.getPolitician().getName()
                            + " | Party then: " + ce.getPartyAtTheTime()
                            + " | Votes: " + ce.getVotes();

                    setText(line);

                    // Highlight the top N candidates based on the seat count
                    if (isWinner) {
                        setStyle("-fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.08);");
                    } else {
                        setStyle("");
                    }
                }
            }
        });
    }
}

