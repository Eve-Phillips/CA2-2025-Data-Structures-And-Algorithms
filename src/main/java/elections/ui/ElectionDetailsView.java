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

public class ElectionDetailsView extends BorderPane {

    public ElectionDetailsView(ElectionSystemManager manager,
                               String type, int year, String location,
                               Runnable onBack) {

        setPadding(new Insets(12));

        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> onBack.run());

        Label title = new Label("Election Details");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        setTop(new VBox(6, new HBox(8, backBtn, title)));

        Election election = manager.getElection(type, year, location);
        if (election == null) {
            setCenter(new Label("Election not found: " + type + " " + location + " " + year));
            return;
        }

        Label meta = new Label(
                "Type: " + election.getType()
                        + " | Location: " + election.getLocation()
                        + " | Year: " + election.getYear()
                        + " | Winners/Seats: " + election.getNumberOfWinners()
        );
        meta.setStyle("-fx-font-weight: bold;");

        ListView<CandidateEntry> list = new ListView<>();
        list.setPlaceholder(new Label("No candidates recorded."));

        // Get sorted by votes desc using your manager helper
        MyArray<CandidateEntry> sorted = manager.getCandidatesSortedByVotes(type, year, location);

        list.getItems().clear();
        for (int i = 0; i < sorted.size(); i++) list.getItems().add(sorted.get(i));

        int winners = election.getNumberOfWinners();

        list.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(CandidateEntry ce, boolean empty) {
                super.updateItem(ce, empty);
                if (empty || ce == null) {
                    setText(null);
                    setStyle("");
                } else {
                    int idx = getIndex(); // position in sorted list
                    boolean isWinner = idx >= 0 && idx < winners;

                    String line = (idx + 1) + ". "
                            + ce.getPolitician().getName()
                            + " | Party then: " + ce.getPartyAtTheTime()
                            + " | Votes: " + ce.getVotes();

                    setText(line);

                    if (isWinner) {
                        setStyle("-fx-font-weight: bold; -fx-background-color: rgba(0,0,0,0.08);");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        setCenter(new VBox(10, meta, new Label("Candidates (sorted by votes desc):"), list));
    }
}
