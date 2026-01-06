package elections;

import elections.model.Election;
import elections.model.ElectionSystemManager;
import elections.model.Politician;
import elections.structures.MyArray;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ElectionSystemManager.
 *
 * These tests focus on:
 * - Basic CRUD behaviour for politicians and elections
 * - Candidate linking (CandidateEntry appears on both the Politician and Election)
 * - Duplicate prevention rules
 * - Search functions (case-insensitivity and partial matching)
 *
 * The goal is to confirm that the manager maintains consistent state
 * across its internal structures and relationships.
 */
public class ElectionSystemManagerTest {

    /**
     * Verifies that when a politician is added, it can be retrieved by name.
     * This tests the "happy path" for add/get.
     */
    @Test
    void addPolitician_thenGetPolitician() {
        ElectionSystemManager mgr = new ElectionSystemManager();

        assertTrue(mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x"));

        Politician p = mgr.getPolitician("A");
        assertNotNull(p);
        assertEquals("A", p.getName());
    }

    /**
     * Ensures that politician names act as unique keys.
     * Adding the same name twice should be rejected.
     */
    @Test
    void addPolitician_duplicateName_rejected() {
        ElectionSystemManager mgr = new ElectionSystemManager();

        assertTrue(mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x"));
        assertFalse(mgr.addPolitician("A", "2001-01-01", "Independent", "Dublin", "http://y"));
    }

    /**
     * Updating a politician's name should change the lookup key.
     * After update, the old key should not resolve, and the new key should.
     */
    @Test
    void updatePolitician_changesKey_whenNameChanges() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("Old", "2000-01-01", "Independent", "Waterford", "http://x");

        assertTrue(mgr.updatePolitician("Old", "New", "2000-01-01", "Independent", "Waterford", "http://x"));

        assertNull(mgr.getPolitician("Old"));
        assertNotNull(mgr.getPolitician("New"));
    }

    /**
     * Verifies that deletePolitician removes the record and makes it unretrievable.
     */
    @Test
    void deletePolitician_removesIt() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x");

        assertTrue(mgr.deletePolitician("A"));
        assertNull(mgr.getPolitician("A"));
    }

    /**
     * Verifies that when an election is added, it can be retrieved by its composite identity.
     */
    @Test
    void addElection_thenGetElection() {
        ElectionSystemManager mgr = new ElectionSystemManager();

        assertTrue(mgr.addElection("General", "Ireland", 2024, 3));

        Election e = mgr.getElection("General", 2024, "Ireland");
        assertNotNull(e);
        assertEquals(3, e.getNumberOfWinners());
    }

    /**
     * Updating an election where the identifying fields change (type/year/location)
     * should change the key used to retrieve it.
     */
    @Test
    void updateElection_changesKey() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addElection("General", "Ireland", 2024, 3);

        assertTrue(mgr.updateElection("General", 2024, "Ireland",
                "General", "Ireland", 2025, 4));

        assertNull(mgr.getElection("General", 2024, "Ireland"));
        assertNotNull(mgr.getElection("General", 2025, "Ireland"));
    }

    /**
     * Verifies that deleteElection removes the record and makes it unretrievable.
     */
    @Test
    void deleteElection_removesIt() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addElection("General", "Ireland", 2024, 3);

        assertTrue(mgr.deleteElection("General", 2024, "Ireland"));
        assertNull(mgr.getElection("General", 2024, "Ireland"));
    }

    /**
     * Adding a candidate should create a CandidateEntry that is reachable from:
     * - the politician (candidacy history)
     * - the election (candidate list)
     *
     * This confirms that the relationship is maintained on both sides.
     */
    @Test
    void addCandidate_thenAppearsOnBothPoliticianAndElection() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x");
        mgr.addElection("Local", "Waterford", 2023, 1);

        assertTrue(mgr.addCandidate("A", "Local", 2023, "Waterford", "Independent", 100));

        assertEquals(1, mgr.getPolitician("A").getCandidacies().size());
        assertEquals(1, mgr.getElection("Local", 2023, "Waterford").getCandidates().size());
    }

    /**
     * A politician should not be added twice as a candidate in the same election.
     * The manager should reject duplicates.
     */
    @Test
    void addCandidate_duplicateInSameElection_rejected() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x");
        mgr.addElection("Local", "Waterford", 2023, 1);

        assertTrue(mgr.addCandidate("A", "Local", 2023, "Waterford", "Independent", 100));
        assertFalse(mgr.addCandidate("A", "Local", 2023, "Waterford", "Independent", 200));
    }

    /**
     * Updating a candidate should change the election-specific fields
     * stored on the CandidateEntry (votes + partyAtTheTime).
     */
    @Test
    void updateCandidate_changesVotesAndPartyAtTime() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x");
        mgr.addElection("Local", "Waterford", 2023, 1);
        mgr.addCandidate("A", "Local", 2023, "Waterford", "Independent", 100);

        assertTrue(mgr.updateCandidate("A", "Local", 2023, "Waterford", "Fine Gael", 250));

        // Using sorted list here provides a stable way to access the (only) candidate entry
        var sorted = mgr.getCandidatesSortedByVotes("Local", 2023, "Waterford");
        assertEquals(1, sorted.size());
        assertEquals(250, sorted.get(0).getVotes());
        assertEquals("Fine Gael", sorted.get(0).getPartyAtTheTime());
    }

    /**
     * Deleting a candidate should remove the CandidateEntry from:
     * - the election candidate list
     * - the politician candidacy list
     *
     * This prevents stale references and keeps the system consistent.
     */
    @Test
    void deleteCandidate_removesFromBothSides() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Independent", "Waterford", "http://x");
        mgr.addElection("Local", "Waterford", 2023, 1);
        mgr.addCandidate("A", "Local", 2023, "Waterford", "Independent", 100);

        assertTrue(mgr.deleteCandidate("A", "Local", 2023, "Waterford"));
        assertEquals(0, mgr.getElection("Local", 2023, "Waterford").getCandidates().size());
        assertEquals(0, mgr.getPolitician("A").getCandidacies().size());
    }

    /**
     * Search by name should:
     * - match partial substrings
     * - ignore case
     *
     * This test checks that "mic" matches both "Michael" and "Micheál".
     */
    @Test
    void searchPoliticiansByName_findsPartialCaseInsensitive() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("Michael", "2000-01-01", "Independent", "Waterford", "x");
        mgr.addPolitician("Micheál", "2000-01-01", "Independent", "Waterford", "x");
        mgr.addPolitician("Sarah", "2000-01-01", "Independent", "Waterford", "x");

        MyArray<Politician> results = mgr.searchPoliticiansByName("mic");
        assertEquals(2, results.size());
    }

    /**
     * Search by party should be case-insensitive and return only exact party matches.
     */
    @Test
    void searchPoliticiansByParty_findsIgnoreCase() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addPolitician("A", "2000-01-01", "Fine Gael", "Waterford", "x");
        mgr.addPolitician("B", "2000-01-01", "Fianna Fail", "Waterford", "x");

        assertEquals(1, mgr.searchPoliticiansByParty("fine gael").size());
    }

    /**
     * Search elections by year should return all elections in that year.
     */
    @Test
    void searchElectionsByYear_findsAllInYear() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addElection("Local", "Waterford", 2023, 1);
        mgr.addElection("General", "Ireland", 2023, 3);
        mgr.addElection("General", "Ireland", 2024, 3);

        assertEquals(2, mgr.searchElectionsByYear(2023).size());
    }

    /**
     * Search elections by type should be case-insensitive.
     */
    @Test
    void searchElectionsByType_findsIgnoreCase() {
        ElectionSystemManager mgr = new ElectionSystemManager();
        mgr.addElection("Local", "Waterford", 2023, 1);
        mgr.addElection("General", "Ireland", 2023, 3);

        assertEquals(1, mgr.searchElectionsByType("local").size());
    }
}


