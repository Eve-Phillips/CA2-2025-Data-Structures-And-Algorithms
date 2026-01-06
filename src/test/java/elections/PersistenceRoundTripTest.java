package elections;

import com.example.ca22025dataalgorithmsandstructures.persistence.PersistenceManagerXStream;
import elections.model.CandidateEntry;
import elections.model.Election;
import elections.model.ElectionSystemManager;
import elections.model.Politician;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that persistence (save/load) works correctly using XStream.
 *
 * These are "round-trip" tests:
 * 1) Create objects in memory
 * 2) Save to a temporary file
 * 3) Load from that file
 * 4) Verify that the loaded state matches expectations
 *
 * Temporary files are used so tests do not rely on or modify real user data.
 */
public class PersistenceRoundTripTest {

    /**
     * If the save file does not exist, load() should return a fresh manager
     * rather than throwing an exception or returning null.
     */
    @Test
    void load_missingFile_returnsFreshManager() throws Exception {
        // Create a temp path, then delete it to simulate a missing file
        Path file = Files.createTempFile("elections_missing_", ".xml");
        Files.deleteIfExists(file);

        ElectionSystemManager loaded = PersistenceManagerXStream.load(file.toString());

        // A missing file should still produce a usable manager instance
        assertNotNull(loaded);
        assertEquals(0, loaded.getAllPoliticians().size());
        assertEquals(0, loaded.getAllElections().size());
    }

    /**
     * Verifies that saving and loading a manager preserves a politician's stored fields.
     */
    @Test
    void saveThenLoad_preservesPoliticianFields() throws Exception {
        Path file = Files.createTempFile("elections_persist_", ".xml");

        ElectionSystemManager mgr = new ElectionSystemManager();
        assertTrue(mgr.addPolitician(
                "Alice Example",
                "1990-01-01",
                "Independent",
                "Waterford",
                "http://example.com/alice.png"
        ));

        // Persist to disk, then reload into a new manager instance
        PersistenceManagerXStream.save(mgr, file.toString());
        ElectionSystemManager loaded = PersistenceManagerXStream.load(file.toString());

        // Confirm that the politician exists and key fields match exactly
        Politician p = loaded.getPolitician("Alice Example");
        assertNotNull(p);
        assertEquals("1990-01-01", p.getDateOfBirth());
        assertEquals("Independent", p.getParty());
        assertEquals("Waterford", p.getCounty());
        assertEquals("http://example.com/alice.png", p.getImageUrl());
    }

    /**
     * Verifies that saving and loading a manager preserves election fields.
     */
    @Test
    void saveThenLoad_preservesElectionFields() throws Exception {
        Path file = Files.createTempFile("elections_persist_", ".xml");

        ElectionSystemManager mgr = new ElectionSystemManager();
        assertTrue(mgr.addElection("General", "Ireland", 2024, 5));

        PersistenceManagerXStream.save(mgr, file.toString());
        ElectionSystemManager loaded = PersistenceManagerXStream.load(file.toString());

        Election e = loaded.getElection("General", 2024, "Ireland");
        assertNotNull(e);

        // These assertions confirm that the election identity and details round-trip correctly
        assertEquals("General", e.getType());
        assertEquals("Ireland", e.getLocation());
        assertEquals(2024, e.getYear());
        assertEquals(5, e.getNumberOfWinners());
    }

    /**
     * Verifies that candidate entries are persisted and that object relationships
     * are restored consistently after loading.
     *
     * This checks:
     * - The election contains candidate entries after load
     * - Each politician has a matching candidacy entry after load
     * - The CandidateEntry points to the same Election and Politician objects
     *   that were loaded into the manager (not duplicate instances)
     */
    @Test
    void saveThenLoad_preservesCandidatesAndBidirectionalLinks() throws Exception {
        Path file = Files.createTempFile("elections_persist_", ".xml");

        ElectionSystemManager mgr = new ElectionSystemManager();

        // Create two politicians and one election
        mgr.addPolitician("Bob Example", "1985-02-02", "Fine Gael", "Dublin", "http://example.com/bob.png");
        mgr.addPolitician("Cara Example", "1988-03-03", "Fianna Fail", "Dublin", "http://example.com/cara.png");
        mgr.addElection("Local", "Dublin", 2023, 1);

        // Link both politicians to the election via candidate entries
        assertTrue(mgr.addCandidate("Bob Example", "Local", 2023, "Dublin", "Fine Gael", 1200));
        assertTrue(mgr.addCandidate("Cara Example", "Local", 2023, "Dublin", "Fianna Fail", 900));

        // Persist then reload
        PersistenceManagerXStream.save(mgr, file.toString());
        ElectionSystemManager loaded = PersistenceManagerXStream.load(file.toString());

        // Election should exist and contain both candidates
        Election e = loaded.getElection("Local", 2023, "Dublin");
        assertNotNull(e);
        assertEquals(2, e.getCandidates().size());

        // Politicians should exist and each should have exactly one candidacy
        Politician bob = loaded.getPolitician("Bob Example");
        Politician cara = loaded.getPolitician("Cara Example");
        assertNotNull(bob);
        assertNotNull(cara);

        assertEquals(1, bob.getCandidacies().size());
        assertEquals(1, cara.getCandidacies().size());

        CandidateEntry bobEntry = bob.getCandidacies().get(0);
        CandidateEntry caraEntry = cara.getCandidacies().get(0);

        // Use assertSame to confirm identity (the references point to the same loaded objects)
        assertSame(e, bobEntry.getElection());
        assertSame(e, caraEntry.getElection());
        assertSame(bob, bobEntry.getPolitician());
        assertSame(cara, caraEntry.getPolitician());
    }
}
