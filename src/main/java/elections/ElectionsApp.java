package elections;

import com.example.ca22025dataalgorithmsandstructures.persistence.PersistenceManagerXStream;
import elections.model.ElectionSystemManager;
import elections.ui.PoliticianDetailsView;
import elections.ui.PoliticianSearchView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ElectionsApp extends Application {

    private static final String SAVE_FILE = "elections.xml";

    private ElectionSystemManager manager;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        // Load snapshot (or start fresh)
        try {
            manager = PersistenceManagerXStream.load(SAVE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            manager = new ElectionSystemManager();
        }

        showPoliticianSearch();

        primaryStage.setTitle("Elections Information System");
        primaryStage.show();
    }

    private void showPoliticianSearch() {
        PoliticianSearchView root = new PoliticianSearchView(
                manager,
                politician -> showPoliticianDetails(politician.getName())
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    private void showPoliticianDetails(String politicianName) {
        PoliticianDetailsView root = new PoliticianDetailsView(
                manager,
                politicianName,
                () -> showPoliticianSearch()
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    @Override
    public void stop() {
        try {
            PersistenceManagerXStream.save(manager, SAVE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
