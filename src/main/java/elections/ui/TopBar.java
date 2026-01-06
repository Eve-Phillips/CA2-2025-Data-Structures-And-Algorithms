package elections.ui;

import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;

/**
 * Simple reusable top navigation bar for the application.
 *
 * Provides:
 * - Navigation between Politicians and Elections screens
 * - Quick-access buttons for adding new politicians, elections, and candidates
 */
public class TopBar extends HBox {

    /**
     * Constructs a top bar with navigation and action buttons.
     *
     * All behaviour is injected via Runnable callbacks so that this
     * UI component remains decoupled from application logic.
     */
    public TopBar(Runnable goPoliticians,
                  Runnable goElections,
                  Runnable addPolitician,
                  Runnable addElection,
                  Runnable addCandidate) {

        // Spacing between buttons for a clean, readable layout
        setSpacing(8);

        // Navigation buttons
        Button polBtn = new Button("Politicians");
        Button eleBtn = new Button("Elections");

        // Action buttons for creating new records
        Button addPol = new Button("+ Politician");
        Button addEle = new Button("+ Election");
        Button addCand = new Button("+ Candidate");

        // Navigation callbacks
        polBtn.setOnAction(e -> goPoliticians.run());
        eleBtn.setOnAction(e -> goElections.run());

        // Action callbacks
        addPol.setOnAction(e -> addPolitician.run());
        addEle.setOnAction(e -> addElection.run());
        addCand.setOnAction(e -> addCandidate.run());

        // Separator visually distinguishes navigation from creation actions
        getChildren().addAll(
                polBtn,
                eleBtn,
                new Separator(),
                addPol,
                addEle,
                addCand
        );
    }
}
