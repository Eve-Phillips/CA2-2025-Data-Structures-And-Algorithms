package elections.util;

import elections.model.ElectionSystemManager;

/**
 * Provides a small set of pre-defined data for quick testing and demos.
 *
 * This avoids having to manually create politicians, elections, and candidates
 * every time the application is run from a fresh save file.
 */
public final class SampleData {

    // Utility class: no instances
    private SampleData() {}

    /**
     * Loads sample politicians, elections, and candidate entries into the manager.
     *
     * This method is designed to be safe to run multiple times:
     * - Politicians are "upserted" (insert if missing, update if present)
     * - Elections and candidates use the manager's add methods, which already block duplicates
     */
    public static void load(ElectionSystemManager m) {
        if (m == null) return;

        // -------------------------
        // Politicians (fictional, DiceBear avatars)
        // -------------------------
        // Using "upsert" means existing saved politicians can be updated with improved
        // details (especially image URLs) without creating duplicates.
        upsertPolitician(m,
                "Aoife Byrne", "1984-03-12", "Fine Gael", "Dublin",
                "https://api.dicebear.com/9.x/avataaars/png?seed=Amaya"
        );

        upsertPolitician(m,
                "Cian Murphy", "1979-11-02", "Fianna Fail", "Cork",
                "https://api.dicebear.com/9.x/avataaars/png?seed=Aiden"
        );

        upsertPolitician(m,
                "Niamh Walsh", "1991-06-21", "Sinn Fein", "Galway",
                "https://api.dicebear.com/7.x/initials/png?seed=Niamh%20Walsh"
        );

        upsertPolitician(m,
                "Eoin ORourke", "1968-01-09", "Labour", "Waterford",
                "https://api.dicebear.com/9.x/avataaars/png?seed=Liliana"
        );

        upsertPolitician(m,
                "Saoirse Keane", "1988-09-30", "Green Party", "Limerick",
                "https://api.dicebear.com/9.x/avataaars/png?seed=Luis"
        );

        upsertPolitician(m,
                "Declan Hayes", "1972-04-14", "Independent", "Kerry",
                "https://api.dicebear.com/9.x/avataaars/png?seed=Jack"
        );

        // -------------------------
        // Elections
        // -------------------------
        // If an election already exists (same type/year/location), manager.addElection returns false.
        // This keeps repeated runs from creating duplicates.
        m.addElection("General", "Ireland", 2024, 4);
        m.addElection("Local", "Dublin", 2023, 2);
        m.addElection("Local", "Waterford", 2022, 1);
        m.addElection("European", "Ireland", 2019, 3);

        // -------------------------
        // Candidates
        // -------------------------
        // Candidate entries link a politician to an election and store election-specific fields.
        // manager.addCandidate blocks duplicates within the same election.
        m.addCandidate("Aoife Byrne",   "General",  2024, "Ireland",   "Fine Gael",   52000);
        m.addCandidate("Cian Murphy",   "General",  2024, "Ireland",   "Fianna Fail", 48100);
        m.addCandidate("Niamh Walsh",   "General",  2024, "Ireland",   "Sinn Fein",   46500);
        m.addCandidate("Saoirse Keane", "General",  2024, "Ireland",   "Green Party", 31200);
        m.addCandidate("Declan Hayes",  "General",  2024, "Ireland",   "Independent", 19800);

        m.addCandidate("Aoife Byrne", "Local", 2023, "Dublin", "Fine Gael", 8200);
        m.addCandidate("Niamh Walsh", "Local", 2023, "Dublin", "Sinn Fein", 7900);
        m.addCandidate("Cian Murphy", "Local", 2023, "Dublin", "Fianna Fail", 6400);

        m.addCandidate("Eoin ORourke", "Local", 2022, "Waterford", "Labour", 4100);
        m.addCandidate("Declan Hayes", "Local", 2022, "Waterford", "Independent", 3900);

        m.addCandidate("Saoirse Keane", "European", 2019, "Ireland", "Green Party", 115000);
        m.addCandidate("Cian Murphy",   "European", 2019, "Ireland", "Fianna Fail", 109000);
        m.addCandidate("Aoife Byrne",   "European", 2019, "Ireland", "Fine Gael",   98000);
        m.addCandidate("Niamh Walsh",   "European", 2019, "Ireland", "Sinn Fein",   92000);
    }

    /**
     * Inserts a politician if they do not exist, otherwise updates their details.
     *
     * This keeps SampleData idempotent for politician records: running it twice
     * does not create duplicates, and can refresh stored fields like party/county/imageUrl.
     */
    private static void upsertPolitician(ElectionSystemManager m,
                                         String name, String dob, String party,
                                         String county, String imageUrl) {

        if (m.getPolitician(name) == null) {
            m.addPolitician(name, dob, party, county, imageUrl);
        } else {
            // Updating in place preserves the key and avoids creating a second record
            m.updatePolitician(name, name, dob, party, county, imageUrl);
        }
    }
}


