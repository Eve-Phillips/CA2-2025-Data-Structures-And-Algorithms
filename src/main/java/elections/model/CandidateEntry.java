package elections.model;

/**
 * Represents a candidate's participation in a specific election.
 *
 * This class acts as a *linking object* between a Politician and an Election,
 * allowing election-specific data (such as votes received and party affiliation
 * at the time) to be stored without duplicating or mutating core Politician data.
 */
public class CandidateEntry {

    /**
     * Reference to the politician who stood in the election.
     */
    private Politician politician;

    /**
     * Reference to the election this entry belongs to.
     */
    private Election election;

    /**
     * The political party the candidate represented *during this election*.
     *
     * This is intentionally stored separately from Politician.getParty()
     * because politicians may change parties over time.
     */
    private String partyAtTheTime;

    /**
     * Number of votes received by this candidate in this election.
     *
     * Stored as a primitive int for efficiency and simplicity.
     */
    private int votes;

    /**
     * Constructs a CandidateEntry linking a politician to an election
     * with election-specific attributes.
     *
     * @param politician     the politician standing in the election
     * @param election       the election being contested
     * @param partyAtTheTime the party represented during this election
     * @param votes          initial vote count
     */
    public CandidateEntry(Politician politician, Election election, String partyAtTheTime, int votes) {
        this.politician = politician;
        this.election = election;
        this.partyAtTheTime = partyAtTheTime;
        this.votes = votes;
    }

    /**
     * @return the politician associated with this candidate entry
     */
    public Politician getPolitician() {
        return politician;
    }

    /**
     * @return the election this candidate entry belongs to
     */
    public Election getElection() {
        return election;
    }

    /**
     * @return the party the candidate represented at the time of the election
     */
    public String getPartyAtTheTime() {
        return partyAtTheTime;
    }

    /**
     * @return the number of votes received by the candidate
     */
    public int getVotes() {
        return votes;
    }

    /**
     * Updates the vote count for this candidate entry.
     *
     * @param newVotes the updated vote count
     */
    public void setVotes(int newVotes) {
        this.votes = newVotes;
    }

    /**
     * Updates the party affiliation for this specific election entry.
     *
     * @param newParty the updated party name
     */
    public void setPartyAtTheTime(String newParty) {
        this.partyAtTheTime = newParty;
    }
}
