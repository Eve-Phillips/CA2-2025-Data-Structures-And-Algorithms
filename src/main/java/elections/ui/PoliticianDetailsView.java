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
import javafx.scene.layout.*;

public class PoliticianDetailsView extends BorderPane {

    private final ElectionSystemManager manager;
    private final String politicianName;
    private final Runnable onBack;

    private final ListView<CandidateEntry> electionsList = new ListView<>();

    public PoliticianDetailsView(ElectionSystemManager manager, String politicianName, Runnable onBack) {
        this.manager = manager;
        this.politicianName = politicianName;
        this.onBack = onBack;

        setPadding(new Insets(12));

        Politician p = manager.getPolitician(politicianName);

        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> onBack.run());

        Label title = new Label("Politician Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        VBox top = new VBox(6, new HBox(8, backBtn, title));
        setTop(top);

        if (p == null) {
            setCenter(new Label("Politician not found: " + politicianName));
            return;
        }

        // Left: photo + key fields
        ImageView photo = new ImageView();
        photo.setFitWidth(180);
        photo.setFitHeight(180);
        photo.setPreserveRatio(true);

        if (p.getImageUrl() != null && !p.getImageUrl().trim().isEmpty()) {
            try {
                photo.setImage(new Image(p.getImageUrl(), true));
            } catch (Exception ignored) { }
        }

        Label name = new Label(p.getName());
        name.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label party = new Label("Party: " + p.getParty());
        Label county = new Label("County: " + p.getCounty());
        Label dob = new Label("DOB: " + p.getDateOfBirth());

        VBox left = new VBox(8, photo, name, party, county, dob);
        left.setPadding(new Insets(0, 12, 0, 0));

        // Right: elections stood in
        Label stoodIn = new Label("Elections stood in (double-click to open):");
        stoodIn.setStyle("-fx-font-weight: bold;");

        electionsList.setPlaceholder(new Label("No candidacies yet."));
        electionsList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CandidateEntry ce, boolean empty) {
                super.updateItem(ce, empty);
                if (empty || ce == null) setText(null);
                else {
                    Election e = ce.getElection();
                    setText(e.getType() + " — " + e.getLocation() + " (" + e.getYear() + ")"
                            + " | Party then: " + ce.getPartyAtTheTime()
                            + " | Votes: " + ce.getVotes());
                }
            }
        });

        electionsList.setOnMouseClicked(evt -> {
            if (evt.getClickCount() == 2) {
                CandidateEntry ce = electionsList.getSelectionModel().getSelectedItem();
                if (ce != null) {
                    Election e = ce.getElection();
                    // Navigate by swapping center pane to election view
                    setCenter(new ElectionDetailsView(manager, e.getType(), e.getYear(), e.getLocation(),
                            () -> setCenter(buildPoliticianCenter(p))));
                }
            }
        });

        VBox right = new VBox(8, stoodIn, electionsList);

        HBox layout = new HBox(12, left, right);
        setCenter(layout);

        // Fill candidacies
        MyArray<CandidateEntry> cands = p.getCandidacies();
        electionsList.getItems().clear();
        for (int i = 0; i < cands.size(); i++) electionsList.getItems().add(cands.get(i));
    }

    private Pane buildPoliticianCenter(Politician p) {
        // Rebuild a simple center (used when returning from election details inside same view)
        VBox wrapper = new VBox(8);
        wrapper.getChildren().add(new Label("Returning..."));
        return wrapper;
    }
}