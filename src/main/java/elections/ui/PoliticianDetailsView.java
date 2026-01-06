package elections.ui;

import elections.model.CandidateEntry;
import elections.model.Election;
import elections.model.ElectionSystemManager;
import elections.model.Politician;
import elections.structures.MyArray;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.InputStream;

/**
 * JavaFX screen that displays a single politician's details.
 *
 * This view shows:
 * - Basic politician details (name, party, county, DOB)
 * - A photo loaded from a URL (or a local placeholder if missing/invalid)
 * - The list of elections the politician has stood in (CandidateEntry list)
 */
public class PoliticianDetailsView extends BorderPane {

    // ListView for displaying the politician's candidacies (one row per election entry)
    private final ListView<CandidateEntry> electionsList = new ListView<>();

    /**
     * Constructs the politician details view for a selected politician.
     *
     * @param manager              system manager providing access to data and operations
     * @param politicianName       name used to look up the politician
     * @param goBackToPoliticians  navigation callback to return to politician list/search
     * @param goElectionsScreen    navigation callback to go to elections screen
     * @param openElection         callback to open election details when a candidacy is double-clicked
     */
    public PoliticianDetailsView(ElectionSystemManager manager,
                                 String politicianName,
                                 Runnable goBackToPoliticians,
                                 Runnable goElectionsScreen,
                                 ElectionSearchView.OpenElection openElection) {

        // Apply padding so the content does not touch the window edge
        setPadding(new Insets(12));

        // Shared top bar with navigation and shortcuts for creating new records
        TopBar bar = new TopBar(
                goBackToPoliticians,
                goElectionsScreen,
                () -> { Dialogs.showAddPolitician(manager); },
                () -> { Dialogs.showAddElection(manager); },
                () -> { Dialogs.showAddCandidate(manager); }
        );

        // Local back button for convenience
        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> goBackToPoliticians.run());

        Label title = new Label("Politician Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Fetch the politician once and use the same object throughout the view
        Politician p = manager.getPolitician(politicianName);

        // If the politician does not exist, show a message and stop building the view
        if (p == null) {
            setTop(new VBox(8, bar, new HBox(8, backBtn, title)));
            setCenter(new Label("Politician not found: " + politicianName));
            return;
        }

        // --- Image + status ---
        ImageView photo = new ImageView();
        photo.setFitWidth(180);
        photo.setFitHeight(180);
        photo.setPreserveRatio(true);

        // Load a local placeholder image first so the UI has something to show immediately
        Image placeholder = loadPlaceholder();
        if (placeholder != null) photo.setImage(placeholder);

        // Label used to explain whether the image loaded successfully or why it failed
        Label imageStatus = new Label();
        imageStatus.setWrapText(true);

        // Basic politician details shown next to the photo
        Label name = new Label(p.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label party = new Label("Party: " + p.getParty());
        Label county = new Label("County: " + p.getCounty());
        Label dob = new Label("DOB: " + p.getDateOfBirth());

        // Helper to refresh the UI from the current politician object
        Runnable refreshUI = () -> {
            // Refresh text fields
            name.setText(p.getName());
            party.setText("Party: " + p.getParty());
            county.setText("County: " + p.getCounty());
            dob.setText("DOB: " + p.getDateOfBirth());

            // Refresh image
            String u = p.getImageUrl() == null ? "" : p.getImageUrl().trim();
            if (u.isEmpty()) {
                if (placeholder != null) photo.setImage(placeholder);
                imageStatus.setText("No image URL set (using placeholder).");
            } else {
                try {
                    // backgroundLoading=false here so we can check isError immediately
                    Image img = new Image(u, 180, 180, true, true, false);

                    if (img.isError()) {
                        if (placeholder != null) photo.setImage(placeholder);

                        Exception ex = img.getException();
                        imageStatus.setText("Image FAILED (using placeholder)\nReason: "
                                + (ex == null
                                ? "Unknown error"
                                : ex.getClass().getSimpleName() + ": " + ex.getMessage()));
                    } else {
                        photo.setImage(img);
                        imageStatus.setText("Image loaded OK");
                    }
                } catch (Exception ex) {
                    if (placeholder != null) photo.setImage(placeholder);
                    imageStatus.setText("Image ERROR (using placeholder): " + ex.getMessage());
                }
            }
        };

        // Run once at startup so the screen is consistent with the politician record
        refreshUI.run();

        // Edit button: opens an edit dialog and then refreshes this screen
        Button editBtn = new Button("Edit Politician");
        editBtn.setOnAction(e -> {
            boolean updated = Dialogs.showEditPolitician(manager, p);
            if (updated) {
                refreshUI.run();
            }
        });

        // Delete button: deletion is blocked if the politician is still referenced by candidate entries
        Button deleteBtn = new Button("Delete Politician");
        deleteBtn.setOnAction(e -> {

            // Prevent deletion while still attached to elections, to avoid dangling references
            if (p.getCandidacies().size() > 0) {
                new Alert(Alert.AlertType.WARNING,
                        "Cannot delete this politician while they have candidacies.\n" +
                                "Delete their candidate entries first (from the election details).")
                        .showAndWait();
                return;
            }

            // Confirm deletion to avoid accidental data loss
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Delete politician '" + p.getName() + "'?\nThis cannot be undone.",
                    ButtonType.YES, ButtonType.NO);
            confirm.setHeaderText("Confirm Deletion");

            confirm.showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    boolean ok = manager.deletePolitician(p.getName());

                    if (!ok) {
                        new Alert(Alert.AlertType.ERROR, "Delete failed.").showAndWait();
                    } else {
                        // After deletion, return to the list screen
                        goBackToPoliticians.run();
                    }
                }
            });
        });

        // Top section: navigation + heading row (with edit + delete)
        setTop(new VBox(8, bar, new HBox(8, backBtn, title, editBtn, deleteBtn)));

        // Left column: image + details
        VBox left = new VBox(8, photo, imageStatus, name, party, county, dob);
        left.setPadding(new Insets(0, 12, 0, 0));

        Label stoodIn = new Label("Elections stood in (double-click to open):");
        stoodIn.setStyle("-fx-font-weight: bold;");

        // Show helpful text when there are no candidacies to display
        electionsList.setPlaceholder(new Label("No candidacies yet."));

        // Custom cell display controls how each CandidateEntry appears in the list
        electionsList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(CandidateEntry ce, boolean empty) {
                super.updateItem(ce, empty);

                if (empty || ce == null) {
                    setText(null);
                } else {
                    Election e = ce.getElection();
                    setText(e.getType() + " — " + e.getLocation() + " (" + e.getYear() + ")"
                            + " | Party then: " + ce.getPartyAtTheTime()
                            + " | Votes: " + ce.getVotes());
                }
            }
        });

        // Double-clicking a candidacy opens the related election details
        electionsList.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                CandidateEntry ce = electionsList.getSelectionModel().getSelectedItem();
                if (ce != null) {
                    Election e = ce.getElection();
                    openElection.open(e.getType(), e.getYear(), e.getLocation());
                }
            }
        });

        // Right column: list of candidacies
        VBox right = new VBox(8, stoodIn, electionsList);

        // Main layout: left (details) + right (candidacies)
        setCenter(new HBox(12, left, right));

        // Populate the elections list from the politician's candidacies
        MyArray<CandidateEntry> cands = p.getCandidacies();
        electionsList.getItems().clear();
        for (int i = 0; i < cands.size(); i++) {
            electionsList.getItems().add(cands.get(i));
        }
    }

    /**
     * Loads a bundled placeholder image from the resources folder.
     *
     * Returning null allows the UI to still function even if the resource is missing.
     */
    private Image loadPlaceholder() {
        String path = "/images/placeholder.png"; // adjust if needed

        try (InputStream in = getClass().getResourceAsStream(path)) {
            if (in == null) return null;
            return new Image(in);
        } catch (Exception e) {
            // If anything goes wrong loading the placeholder, fail gracefully
            return null;
        }
    }
}