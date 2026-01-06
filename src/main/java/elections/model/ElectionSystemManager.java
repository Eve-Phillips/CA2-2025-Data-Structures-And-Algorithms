package elections.model;

import elections.structures.HashTable;
import elections.structures.MyArray;
import elections.structures.Sort;

// Persistence imports
import com.example.ca22025dataalgorithmsandstructures.persistence.PersistenceManagerXStream;
import java.io.IOException;

/**
 * Central coordinator class for managing politicians, elections,
 * and candidate relationships within the system.
 *
 * This class acts as the main access point for higher-level logic,
 * delegating storage and algorithmic behaviour to underlying
 * data structures where appropriate.
 */
public class ElectionSystemManager {

    // Default filename used when persisting the system state
    public static final String DEFAULT_SAVE_FILE = "elections.xml";

    // Hash table of politicians, keyed by politician name
    private HashTable<String, Politician> politicians;

    // Hash table of elections, keyed by a composite election identifier
    private HashTable<String, Election> elections;

    /**
     * Constructs a new, empty ElectionSystemManager.
     *
     * Both hash tables are initialised with a fixed size.
     * This size can be tuned later depending on expected data volume.
     */
    public ElectionSystemManager() {
        // Using a fixed initial size keeps hashing predictable
        this.politicians = new HashTable<>(100);
        this.elections = new HashTable<>(100);
    }

    //-------------------------
    // Politician Management
    //-------------------------

    /**
     * Adds a new politician to the system.
     *
     * Politician names are treated as unique keys.
     *
     * @return false if a politician with the same name already exists
     */
    public boolean addPolitician(String name, String dateOfBirth, String party,
                                 String county, String imageUrl) {

        // Prevent duplicate keys in the hash table
        if (politicians.get(name) != null) {
            return false;
        }

        Politician p = new Politician(name, dateOfBirth, party, county, imageUrl);
        politicians.put(name, p);
        return true;
    }

    /**
     * Retrieves a politician by name.
     */
    public Politician getPolitician(String name) {
        return politicians.get(name);
    }

    /**
     * Deletes a politician from the system.
     *
     * @return false if the politician does not exist
     */
    public boolean deletePolitician(String name) {
        if (politicians.get(name) == null) return false;
        politicians.remove(name);
        return true;
    }

    /**
     * Updates a politician's details.
     *
     * If the politician's name changes, the hash table entry must be
     * removed and reinserted to maintain correct key mapping.
     */
    public boolean updatePolitician(String name, String newName,
                                    String dateOfBirth, String party,
                                    String county, String imageUrl) {

        Politician p = politicians.get(name);
        if (p == null) return false;

        // Changing the key requires removing and reinserting
        if (!name.equals(newName)) {
            politicians.remove(name);
            p.updateDetails(newName, dateOfBirth, party, county, imageUrl);
            politicians.put(newName, p);
        } else {
            p.updateDetails(newName, dateOfBirth, party, county, imageUrl);
        }
        return true;
    }

    /**
     * Returns all politicians currently stored in the system.
     *
     * Since HashTable does not expose direct iteration,
     * we scan the internal storage by index.
     */
    public MyArray<Politician> getAllPoliticians() {
        MyArray<Politician> out = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null) out.add(p);
        }
        return out;
    }

    //-------------------------
    // Search Politicians
    //-------------------------

    /**
     * Searches for politicians whose names contain a given substring.
     *
     * This is a linear scan over the hash table contents.
     */
    public MyArray<Politician> searchPoliticiansByName(String part) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null &&
                    p.getName().toLowerCase().contains(part.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Searches for politicians by party affiliation.
     */
    public MyArray<Politician> searchPoliticiansByParty(String party) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null && p.getParty().equalsIgnoreCase(party)) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * Searches for politicians by county.
     */
    public MyArray<Politician> searchPoliticiansByCounty(String county) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null && p.getCounty().equalsIgnoreCase(county)) {
                results.add(p);
            }
        }
        return results;
    }

    //-------------------------
    // Search Politicians (Sorted)
    //-------------------------

    /**
     * Searches for politicians by name and sorts the results alphabetically.
     */
    public MyArray<Politician> searchPoliticiansByNameSorted(String part) {
        MyArray<Politician> results = searchPoliticiansByName(part);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    /**
     * Searches for politicians by party and sorts the results alphabetically.
     */
    public MyArray<Politician> searchPoliticiansByPartySorted(String party) {
        MyArray<Politician> results = searchPoliticiansByParty(party);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    /**
     * Searches for politicians by county and sorts the results alphabetically.
     */
    public MyArray<Politician> searchPoliticiansByCountySorted(String county) {
        MyArray<Politician> results = searchPoliticiansByCounty(county);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    //-------------------------
    // Election Management
    //-------------------------

    /**
     * Builds a unique key for identifying elections in the hash table.
     *
     * Combining multiple attributes avoids key collisions between
     * elections with similar names.
     */
    private String buildElectionKey(String type, int year, String location) {
        return type + "-" + year + "-" + location;
    }

    /**
     * Adds a new election to the system.
     */
    public boolean addElection(String type, String location,
                               int year, int numberOfWinners) {

        String key = buildElectionKey(type, year, location);

        if (elections.get(key) != null) {
            return false;
        }

        Election e = new Election(type, location, year, numberOfWinners);
        elections.put(key, e);
        return true;
    }

    /**
     * Retrieves an election by its identifying attributes.
     */
    public Election getElection(String type, int year, String location) {
        return elections.get(buildElectionKey(type, year, location));
    }

    /**
     * Deletes an election from the system.
     */
    public boolean deleteElection(String type, int year, String location) {
        String key = buildElectionKey(type, year, location);

        if (elections.get(key) == null) return false;
        elections.remove(key);
        return true;
    }

    /**
     * Updates an election's details.
     *
     * If identifying attributes change, the election must be
     * reinserted using a new key.
     */
    public boolean updateElection(String type, int year, String location,
                                  String newType, String newLocation,
                                  int newYear, int winners) {

        String oldKey = buildElectionKey(type, year, location);
        Election e = elections.get(oldKey);
        if (e == null) return false;

        elections.remove(oldKey);
        e.updateDetails(newType, newLocation, newYear, winners);
        elections.put(buildElectionKey(newType, newYear, newLocation), e);
        return true;
    }

    /**
     * Returns all elections stored in the system.
     */
    public MyArray<Election> getAllElections() {
        MyArray<Election> out = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Election e = elections.getFromIndex(i);
            if (e != null) out.add(e);
        }
        return out;
    }

    // -------------------------------
    // Search Elections
    // -------------------------------

    /**
     * Searches for elections by year.
     */
    public MyArray<Election> searchElectionsByYear(int year) {
        MyArray<Election> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Election e = elections.getFromIndex(i);
            if (e != null && e.getYear() == year) {
                results.add(e);
            }
        }
        return results;
    }

    /**
     * Searches for elections by type.
     */
    public MyArray<Election> searchElectionsByType(String type) {
        MyArray<Election> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Election e = elections.getFromIndex(i);
            if (e != null && e.getType().equalsIgnoreCase(type)) {
                results.add(e);
            }
        }
        return results;
    }

    // -------------------------------
    // Candidate Management
    // -------------------------------

    /**
     * Registers a politician as a candidate in a specific election.
     *
     * This creates a CandidateEntry and links it to both the
     * Politician and Election objects.
     */
    public boolean addCandidate(String politicianName,
                                String type, int year, String location,
                                String partyAtTime, int votes) {

        Politician p = politicians.get(politicianName);
        Election e = getElection(type, year, location);

        if (p == null || e == null) return false;

        // Prevent the same politician being added twice to one election
        if (findCandidateIndex(e, politicianName) >= 0) return false;

        CandidateEntry ce = new CandidateEntry(p, e, partyAtTime, votes);
        p.addCandidacy(ce);
        e.addCandidate(ce);
        return true;
    }

    /**
     * Finds the index of a candidate within an election by politician name.
     *
     * @return index if found, otherwise -1
     */
    private int findCandidateIndex(Election e, String politicianName) {
        MyArray<CandidateEntry> cands = e.getCandidates();

        for (int i = 0; i < cands.size(); i++) {
            CandidateEntry ce = cands.get(i);
            if (ce.getPolitician().getName()
                    .equalsIgnoreCase(politicianName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Updates a candidate's election-specific details.
     */
    public boolean updateCandidate(String politicianName,
                                   String type, int year, String location,
                                   String newPartyAtTime, int newVotes) {

        Election e = getElection(type, year, location);
        if (e == null) return false;

        int idx = findCandidateIndex(e, politicianName);
        if (idx < 0) return false;

        CandidateEntry ce = e.getCandidates().get(idx);
        ce.setPartyAtTheTime(newPartyAtTime);
        ce.setVotes(newVotes);
        return true;
    }

    /**
     * Removes a candidate from an election and from the politician's
     * candidacy history.
     */
    public boolean deleteCandidate(String politicianName,
                                   String type, int year, String location) {

        Election e = getElection(type, year, location);
        Politician p = getPolitician(politicianName);
        if (e == null || p == null) return false;

        int eIdx = findCandidateIndex(e, politicianName);
        if (eIdx < 0) return false;

        CandidateEntry entry = e.getCandidates().get(eIdx);
        e.getCandidates().remove(eIdx);

        // Remove matching candidacy from the politician's list
        MyArray<CandidateEntry> pcs = p.getCandidacies();
        for (int i = 0; i < pcs.size(); i++) {
            if (pcs.get(i).getElection() == entry.getElection()) {
                pcs.remove(i);
                break;
            }
        }
        return true;
    }

    // -------------------------------
    // Sorted Candidates
    // -------------------------------

    /**
     * Returns candidates for an election sorted by vote count (descending).
     *
     * A copy of the candidate list is sorted so the original order
     * stored in the Election object remains unchanged.
     */
    public MyArray<CandidateEntry> getCandidatesSortedByVotes(String type,
                                                              int year,
                                                              String location) {

        Election e = getElection(type, year, location);
        if (e == null) return new MyArray<>();

        MyArray<CandidateEntry> copy = new MyArray<>();
        MyArray<CandidateEntry> original = e.getCandidates();

        for (int i = 0; i < original.size(); i++) {
            copy.add(original.get(i));
        }

        Sort.sortCandidatesByVotesDesc(copy);
        return copy;
    }

    //-------------------------
    // Persistence
    //-------------------------

    /**
     * Saves the current system state to an XML file.
     */
    public void saveToFile(String filename) throws IOException {
        PersistenceManagerXStream.save(this, filename);
    }

    /**
     * Loads a system state from an XML file.
     */
    public static ElectionSystemManager loadFromFile(String filename)
            throws IOException {
        return PersistenceManagerXStream.load(filename);
    }
}
