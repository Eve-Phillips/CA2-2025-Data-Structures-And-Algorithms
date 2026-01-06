package elections;

import com.example.ca22025dataalgorithmsandstructures.persistence.PersistenceManagerXStream;
import elections.model.ElectionSystemManager;
import elections.ui.ElectionDetailsView;
import elections.ui.ElectionSearchView;
import elections.ui.PoliticianDetailsView;
import elections.ui.PoliticianSearchView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main JavaFX entry point for the Elections Information System.
 *
 * Responsibilities:
 * - Load saved application state on startup (or create a fresh manager if load fails)
 * - Handle simple screen navigation by swapping the Scene root
 * - Save application state on shutdown
 */
public class ElectionsApp extends Application {

    // Default file used to persist the system between runs
    private static final String SAVE_FILE = "elections.xml";

    // Central manager containing all politicians/elections/candidates
    private ElectionSystemManager manager;

    // Main application window used for swapping scenes
    private Stage stage;

    /**
     * Called by JavaFX when the application starts.
     * This sets up the primary stage, loads persisted state, and shows the initial screen.
     */
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Load the saved system state if it exists.
        // If loading fails (missing/corrupt file), fall back to a fresh manager so the app can still run.
        try {
            manager = PersistenceManagerXStream.load(SAVE_FILE);
        } catch (Exception e) {
            e.printStackTrace();
            manager = new ElectionSystemManager();
        }

        // Initial screen shown on startup
        showPoliticianSearch();

        primaryStage.setTitle("Elections Information System");
        primaryStage.show();
    }

    /**
     * Shows the politician search/browse screen.
     *
     * Navigation is handled by passing callbacks into the view:
     * - open politician details when a user selects a politician
     * - switch to elections screen when the navigation button is used
     */
    private void showPoliticianSearch() {
        PoliticianSearchView root = new PoliticianSearchView(
                manager,
                politician -> showPoliticianDetails(politician.getName()),
                this::showElectionSearch
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    /**
     * Shows the election search/browse screen.
     *
     * The OpenElection callback is triggered when the user opens an election result.
     */
    private void showElectionSearch() {
        ElectionSearchView root = new ElectionSearchView(
                manager,
                (type, year, location) -> showElectionDetails(type, year, location),
                this::showPoliticianSearch
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    /**
     * Shows the details screen for a specific politician (by name).
     */
    private void showPoliticianDetails(String politicianName) {
        PoliticianDetailsView root = new PoliticianDetailsView(
                manager,
                politicianName,
                this::showPoliticianSearch,
                this::showElectionSearch,
                (type, year, location) -> showElectionDetails(type, year, location)
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    /**
     * Shows the details screen for a specific election (type + year + location).
     */
    private void showElectionDetails(String type, int year, String location) {
        ElectionDetailsView root = new ElectionDetailsView(
                manager,
                type,
                year,
                location,
                this::showElectionSearch,
                this::showPoliticianSearch
        );
        stage.setScene(new Scene(root, 900, 600));
    }

    /**
     * Called by JavaFX during application shutdown.
     *
     * Saving here ensures the user does not have to click a "Save" button,
     * and the most recent system state is persisted between runs.
     */
    @Override
    public void stop() {
        try {
            PersistenceManagerXStream.save(manager, SAVE_FILE);
        } catch (Exception e) {
            // If saving fails, we print the error rather than crashing during shutdown
            e.printStackTrace();
        }
    }

    /**
     * Standard Java entry point. JavaFX will call start().
     */
    public static void main(String[] args) {
        launch(args);
    }
}
