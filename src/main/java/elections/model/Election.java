package elections.model;

import elections.structures.MyArray;

/**
 * Represents a single election event (e.g. General, Local, European).
 *
 * This class acts as a container for election metadata as well as
 * the collection of candidates who contested the election.
 */
public class Election {

    /**
     * Type of election.
     * Examples include "General", "Local", "European", or "Presidential".
     *
     * Stored as a String for flexibility rather than a fixed enum,
     * allowing new election types to be added without refactoring.
     */
    private String type;

    /**
     * Location or constituency of the election.
     * This may represent a city, county, country, or electoral district.
     */
    private String location;

    /**
     * Year the election took place.
     *
     */
    private int year;

    /**
     * Number of winners elected in this election.
     */
    private int numberOfWinners;

    /**
     * Collection of candidates who stood in this election.
     *
     * MyArray is a custom dynamic array implemented for the
     * Data Structures & Algorithms module.
     */
    private MyArray<CandidateEntry> candidates;

    /**
     * Constructs an Election with its core identifying details.
     *
     * The candidates collection is initialised empty and populated
     * incrementally as candidates are registered.
     *
     * @param type             the type of election
     * @param location         the location or constituency
     * @param year             the year the election took place
     * @param numberOfWinners  number of winners to be elected
     */
    public Election(String type, String location, int year, int numberOfWinners) {
        this.type = type;
        this.location = location;
        this.year = year;
        this.numberOfWinners = numberOfWinners;

        // Initialising here ensures the Election object is always in a valid state
        this.candidates = new MyArray<>();
    }

    /**
     * @return the type of election
     */
    public String getType() {
        return type;
    }

    /**
     * @return the election location or constituency
     */
    public String getLocation() {
        return location;
    }

    /**
     * @return the year the election took place
     */
    public int getYear() {
        return year;
    }

    /**
     * @return the number of winners for this election
     */
    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    /**
     * Returns the collection of candidates in this election.
     *
     * @return a MyArray of CandidateEntry objects
     */
    public MyArray<CandidateEntry> getCandidates() {
        return candidates;
    }

    /**
     * Adds a candidate entry to the election.
     *
     * @param entry the candidate entry to add
     */
    public void addCandidate(CandidateEntry entry) {
        candidates.add(entry);
    }

    /**
     * Updates the core details of the election.
     *
     * @param type            updated election type
     * @param location        updated location or constituency
     * @param year            updated election year
     * @param numberOfWinners updated number of winners
     */
    public void updateDetails(String type, String location, int year, int numberOfWinners) {
        this.type = type;
        this.location = location;
        this.year = year;
        this.numberOfWinners = numberOfWinners;
    }
}
