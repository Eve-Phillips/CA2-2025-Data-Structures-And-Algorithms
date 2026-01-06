package elections.ui;

import elections.model.ElectionSystemManager;
import elections.model.Politician;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

/**
 * Small utility class containing JavaFX dialogs for adding data to the system.
 *
 * The methods here build modal forms, validate user input, and delegate the
 * actual creation logic to ElectionSystemManager.
 */
public final class Dialogs {
    // Utility class: no instances
    private Dialogs() {}

    /**
     * Shows a dialog allowing the user to add a new politician.
     *
     * Includes an optional image URL field with a small preview area,
     * so users can confirm the link is a direct image.
     */
    public static void showAddPolitician(ElectionSystemManager manager) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Politician");

        // Input fields for politician details
        TextField name = new TextField();
        TextField dob = new TextField();
        TextField party = new TextField();
        TextField county = new TextField();
        TextField url = new TextField();

        // Helpful hints for expected formats / defaults
        dob.setPromptText("YYYY-MM-DD");
        party.setPromptText("Independent if blank");
        url.setPromptText("Direct image URL (ends with .png/.jpg) OR file:/C:/...");

        // Image preview area (kept small to fit the dialog comfortably)
        ImageView preview = new ImageView();
        preview.setFitWidth(110);
        preview.setFitHeight(110);
        preview.setPreserveRatio(true);

        // Status label below the preview to provide feedback to the user
        Label previewStatus = new Label("Preview: (paste an image URL)");
        previewStatus.setWrapText(true);

        // Preview button triggers a load attempt without having to submit the dialog
        Button previewBtn = new Button("Preview");
        previewBtn.setOnAction(e -> tryPreview(url.getText(), preview, previewStatus));

        // Also attempt preview when the URL field loses focus (common UX pattern)
        url.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) tryPreview(url.getText(), preview, previewStatus);
        });

        // Form layout using a grid for consistent label/field alignment
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(4));

        grid.addRow(0, new Label("Name:"), name);
        grid.addRow(1, new Label("DOB:"), dob);
        grid.addRow(2, new Label("Party:"), party);
        grid.addRow(3, new Label("County:"), county);

        // HBox groups the URL field and its preview button on one row
        grid.addRow(4, new Label("Image URL:"), new HBox(8, url, previewBtn));

        // Preview area sits underneath the form fields
        grid.add(new Label("Image preview:"), 0, 5);
        grid.add(preview, 1, 5);
        grid.add(previewStatus, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // showAndWait returns the button the user pressed, allowing us to act only on OK
        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {

                // Basic validation: name is used as a unique key, so it must exist
                String n = name.getText().trim();
                if (n.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Name is required.").showAndWait();
                    return;
                }

                // If party is not provided, default to "Independent" for cleaner data
                String partyVal = party.getText().trim().isEmpty()
                        ? "Independent"
                        : party.getText().trim();

                // Delegate creation logic to the manager (keeps UI layer thin)
                boolean ok = manager.addPolitician(
                        n,
                        dob.getText().trim(),
                        partyVal,
                        county.getText().trim(),
                        url.getText().trim()
                );

                // Manager returns false when a politician with the same name already exists
                if (!ok) {
                    new Alert(Alert.AlertType.ERROR, "Politician already exists (same name).").showAndWait();
                }
            }
        });
    }

    /**
     * Shows a dialog allowing the user to edit an existing politician.
     *
     * For safety and simplicity, the politician name is treated as the unique key
     * and is not editable here (renaming would require re-keying in the hash table).
     *
     * @param manager system manager that performs the update
     * @param p       the politician to edit
     * @return true if the politician was updated, false otherwise
     */
    public static boolean showEditPolitician(ElectionSystemManager manager, Politician p) {
        if (p == null) return false;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Politician");

        // Input fields (pre-filled from the existing politician)
        TextField name = new TextField(p.getName());
        TextField dob = new TextField(p.getDateOfBirth());
        TextField party = new TextField(p.getParty());
        TextField county = new TextField(p.getCounty());
        TextField url = new TextField(p.getImageUrl());

        // Name is the key, so do not allow editing it in this simple update dialog
        name.setDisable(true);

        // Helpful hints for expected formats / defaults
        dob.setPromptText("YYYY-MM-DD");
        party.setPromptText("Independent if blank");
        url.setPromptText("Direct image URL (ends with .png/.jpg) OR file:/C:/...");

        // Image preview area
        ImageView preview = new ImageView();
        preview.setFitWidth(110);
        preview.setFitHeight(110);
        preview.setPreserveRatio(true);

        // Status label below the preview
        Label previewStatus = new Label("Preview: (paste an image URL)");
        previewStatus.setWrapText(true);

        // Try preview once using the current URL (if any)
        tryPreview(url.getText(), preview, previewStatus);

        // Preview button triggers a load attempt without having to submit the dialog
        Button previewBtn = new Button("Preview");
        previewBtn.setOnAction(e -> tryPreview(url.getText(), preview, previewStatus));

        // Also attempt preview when the URL field loses focus
        url.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused) tryPreview(url.getText(), preview, previewStatus);
        });

        // Form layout
        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(4));

        grid.addRow(0, new Label("Name:"), name);
        grid.addRow(1, new Label("DOB:"), dob);
        grid.addRow(2, new Label("Party:"), party);
        grid.addRow(3, new Label("County:"), county);
        grid.addRow(4, new Label("Image URL:"), new HBox(8, url, previewBtn));

        grid.add(new Label("Image preview:"), 0, 5);
        grid.add(preview, 1, 5);
        grid.add(previewStatus, 1, 6);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Run update only if the user clicks OK
        ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
        if (result != ButtonType.OK) return false;

        // If party is not provided, default to "Independent" for cleaner data
        String partyVal = party.getText().trim().isEmpty()
                ? "Independent"
                : party.getText().trim();

        // Delegate update logic to the manager
        boolean ok = manager.updatePolitician(
                p.getName(),                // original key
                p.getName(),                // newName (unchanged)
                dob.getText().trim(),
                partyVal,
                county.getText().trim(),
                url.getText().trim()
        );

        if (!ok) {
            new Alert(Alert.AlertType.ERROR, "Update failed.").showAndWait();
            return false;
        }

        return true;
    }

    /**
     * Tries to load and display an image in the preview control.
     *
     * This method is designed to be safe and user-friendly:
     * - it clears the preview if the URL is blank
     * - it catches exceptions so the dialog never crashes
     * - it uses backgroundLoading=true so the UI thread stays responsive
     */
    private static void tryPreview(String urlText, ImageView preview, Label status) {
        String u = urlText == null ? "" : urlText.trim();

        // Blank URL means "no preview"
        if (u.isEmpty()) {
            preview.setImage(null);
            status.setText("Preview: (paste an image URL)");
            return;
        }

        try {
            // backgroundLoading=true allows JavaFX to load asynchronously
            Image img = new Image(u, true);
            preview.setImage(img);

            // Image#isError indicates if loading failed (bad URL, blocked request, unsupported format, etc.)
            if (img.isError()) {
                status.setText("Preview failed. Make sure it’s a DIRECT image link (ends with .png/.jpg), not a webpage.");
            } else {
                // Some links "load" but remain blank due to hotlink protection or authentication requirements
                status.setText("Preview loaded (if it stays blank, the link may require auth / block hotlinking).");
            }
        } catch (Exception ex) {
            // Any unexpected failure is reported without exposing a stack trace in the UI
            preview.setImage(null);
            status.setText("Preview failed: " + ex.getClass().getSimpleName());
        }
    }

    /**
     * Shows a dialog allowing the user to add a new election.
     *
     * Year and winners are parsed as integers and validated.
     */
    public static void showAddElection(ElectionSystemManager manager) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Election");

        // Input fields for election details
        TextField type = new TextField();
        TextField location = new TextField();
        TextField year = new TextField();
        TextField winners = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        grid.addRow(0, new Label("Type:"), type);
        grid.addRow(1, new Label("Location:"), location);
        grid.addRow(2, new Label("Year:"), year);
        grid.addRow(3, new Label("Winners/Seats:"), winners);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {
                String t = type.getText().trim();
                String loc = location.getText().trim();

                // Required fields: election identity depends on type + location + year
                if (t.isEmpty() || loc.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR, "Type and Location are required.").showAndWait();
                    return;
                }

                try {
                    // Parse numeric input, showing a friendly error if parsing fails
                    int y = Integer.parseInt(year.getText().trim());
                    int w = Integer.parseInt(winners.getText().trim());

                    boolean ok = manager.addElection(t, loc, y, w);
                    if (!ok) {
                        new Alert(Alert.AlertType.ERROR, "Election already exists (same type/year/location).").showAndWait();
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Year and Winners must be numbers.").showAndWait();
                }
            }
        });
    }

    /**
     * Shows a dialog allowing the user to add a candidate entry to an election.
     *
     * The user must provide a politician name and an election identifier.
     * The manager validates that both exist and that duplicates are not created.
     */
    public static void showAddCandidate(ElectionSystemManager manager) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Candidate");

        // Inputs needed to locate the election and identify the politician
        TextField politicianName = new TextField();
        TextField electionType = new TextField();
        TextField electionLocation = new TextField();
        TextField electionYear = new TextField();

        // Election-specific candidate details
        TextField partyAtTime = new TextField();
        TextField votes = new TextField();

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        grid.addRow(0, new Label("Politician Name:"), politicianName);
        grid.addRow(1, new Label("Election Type:"), electionType);
        grid.addRow(2, new Label("Election Location:"), electionLocation);
        grid.addRow(3, new Label("Election Year:"), electionYear);
        grid.addRow(4, new Label("Party at the time:"), partyAtTime);
        grid.addRow(5, new Label("Votes:"), votes);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.OK) {

                // Trim values to avoid accidental duplicates caused by whitespace
                String pol = politicianName.getText().trim();
                String type = electionType.getText().trim();
                String loc = electionLocation.getText().trim();
                String party = partyAtTime.getText().trim();

                // Required inputs to identify which election and which politician
                if (pol.isEmpty() || type.isEmpty() || loc.isEmpty()) {
                    new Alert(Alert.AlertType.ERROR,
                            "Politician name, election type, and election location are required.")
                            .showAndWait();
                    return;
                }

                try {
                    // Parse numeric fields
                    int y = Integer.parseInt(electionYear.getText().trim());
                    int v = Integer.parseInt(votes.getText().trim());

                    boolean ok = manager.addCandidate(pol, type, y, loc, party, v);
                    if (!ok) {
                        new Alert(Alert.AlertType.ERROR,
                                "Failed to add candidate. Ensure the politician & election exist, and candidate isn't duplicate.")
                                .showAndWait();
                    }
                } catch (NumberFormatException ex) {
                    new Alert(Alert.AlertType.ERROR, "Election Year and Votes must be numbers.").showAndWait();
                }
            }
        });
    }
}
