package elections.ui;

import elections.model.ElectionSystemManager;
import elections.model.Politician;
import elections.structures.MyArray;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class PoliticianSearchView extends BorderPane {

    private final ElectionSystemManager manager;
    private final Consumer<Politician> onOpenPolitician;

    private final TextField nameField = new TextField();
    private final TextField partyField = new TextField();
    private final TextField countyField = new TextField();

    private final ListView<Politician> resultsList = new ListView<>();

    public PoliticianSearchView(ElectionSystemManager manager, Consumer<Politician> onOpenPolitician) {
        this.manager = manager;
        this.onOpenPolitician = onOpenPolitician;

        setPadding(new Insets(12));

        Label title = new Label("Search Politicians");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        nameField.setPromptText("Name contains (optional)");
        partyField.setPromptText("Party (optional)");
        countyField.setPromptText("County (optional)");

        Button searchBtn = new Button("Search");
        Button showAllBtn = new Button("Show All");

        HBox filters = new HBox(8, nameField, partyField, countyField, searchBtn, showAllBtn);
        filters.setPadding(new Insets(8, 0, 8, 0));

        resultsList.setPlaceholder(new Label("No results"));
        resultsList.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Politician p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setText(null);
                } else {
                    setText(p.getName() + " — " + p.getParty() + " (" + p.getCounty() + ")");
                }
            }
        });

        resultsList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                Politician selected = resultsList.getSelectionModel().getSelectedItem();
                if (selected != null) onOpenPolitician.accept(selected);
            }
        });

        searchBtn.setOnAction(e -> doSearch());
        showAllBtn.setOnAction(e -> showAll());

        VBox top = new VBox(6, title, filters);
        setTop(top);
        setCenter(resultsList);

        // initial view
        showAll();
    }

    private void showAll() {
        MyArray<Politician> all = manager.getAllPoliticians();
        // you already have name sort; use it for consistent results
        elections.structures.Sort.sortPoliticiansByName(all);
        fillResults(all);
    }

    private void doSearch() {
        String namePart = nameField.getText() == null ? "" : nameField.getText().trim();
        String party = partyField.getText() == null ? "" : partyField.getText().trim();
        String county = countyField.getText() == null ? "" : countyField.getText().trim();

        // Start with all and filter manually (avoids needing extra manager methods right now)
        MyArray<Politician> all = manager.getAllPoliticians();
        MyArray<Politician> filtered = new MyArray<>();

        for (int i = 0; i < all.size(); i++) {
            Politician p = all.get(i);

            if (!namePart.isEmpty() && !p.getName().toLowerCase().contains(namePart.toLowerCase())) continue;
            if (!party.isEmpty() && !p.getParty().equalsIgnoreCase(party)) continue;
            if (!county.isEmpty() && !p.getCounty().equalsIgnoreCase(county)) continue;

            filtered.add(p);
        }

        elections.structures.Sort.sortPoliticiansByName(filtered);
        fillResults(filtered);
    }

    private void fillResults(MyArray<Politician> data) {
        resultsList.getItems().clear();
        for (int i = 0; i < data.size(); i++) {
            resultsList.getItems().add(data.get(i));
        }
    }
}

