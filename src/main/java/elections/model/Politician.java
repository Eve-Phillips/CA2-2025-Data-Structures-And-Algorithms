package elections.model;

import elections.structures.MyArray;

/**
 * Represents a politician within the election system.
 *
 * This class stores personal and affiliation details for a politician,
 * as well as a record of the elections they have participated in.
 */
public class Politician {

    // Full name of the politician (used as a unique identifier elsewhere)
    private String name;

    // Date of birth stored as a String for simplicity (e.g. "1980-05-12")
    private String dateOfBirth;

    // Current political party affiliation
    private String party;

    // County or primary area of association
    private String county;

    // URL or path to an image representing the politician
    private String imageUrl;

    /**
     * List of elections this politician has stood in.
     *
     * Each entry links this politician to a specific election
     * and stores election-specific details such as votes received.
     */
    private MyArray<CandidateEntry> candidacies;

    /**
     * Constructs a new Politician with the provided details.
     *
     * The candidacies list is initialised empty and populated
     * as the politician is registered in elections.
     */
    public Politician(String name, String dateOfBirth,
                      String party, String county,
                      String imageUrl) {

        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.party = party;
        this.county = county;
        this.imageUrl = imageUrl;

        // Ensure the politician always has a valid candidacy list
        this.candidacies = new MyArray<>();
    }

    /**
     * @return the politician's full name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the politician's date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * @return the politician's current party affiliation
     */
    public String getParty() {
        return party;
    }

    /**
     * @return the county associated with the politician
     */
    public String getCounty() {
        return county;
    }

    /**
     * @return the image URL or path for the politician
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Returns the list of elections this politician has participated in.
     *
     * The returned collection contains CandidateEntry objects,
     * each representing one election candidacy.
     */
    public MyArray<CandidateEntry> getCandidacies() {
        return candidacies;
    }

    /**
     * Adds a new election candidacy for this politician.
     *
     * This method does not perform duplicate checks; it assumes
     * validation is handled by the calling code.
     */
    public void addCandidacy(CandidateEntry entry) {
        candidacies.add(entry);
    }

    /**
     * Updates the politician's stored details.
     *
     * This allows changes such as party switches or updated
     * personal information without creating a new object.
     */
    public void updateDetails(String name, String dob,
                              String party, String county,
                              String imageUrl) {

        this.name = name;
        this.dateOfBirth = dob;
        this.party = party;
        this.county = county;
        this.imageUrl = imageUrl;
    }
}

