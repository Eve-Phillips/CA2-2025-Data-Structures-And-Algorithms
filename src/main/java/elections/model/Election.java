package elections.model;

import elections.structures.MyArray;

public class Election {

    private String type;                // "General", "Local", "European", "Presidential"
    private String location;            // e.g. "Waterford", "Ireland", or constituency
    private int year;
    private int numberOfWinners;

    private MyArray<CandidateEntry> candidates;

    public Election(String type, String location, int year, int numberOfWinners) {
        this.type = type;
        this.location = location;
        this.year = year;
        this.numberOfWinners = numberOfWinners;
        this.candidates = new MyArray<>();
    }

    public String getType() {
        return type;
    }

    public String getLocation() {
        return location;
    }

    public int getYear() {
        return year;
    }

    public int getNumberOfWinners() {
        return numberOfWinners;
    }

    public MyArray<CandidateEntry> getCandidates() {
        return candidates;
    }

    public void addCandidate(CandidateEntry entry) {
        candidates.add(entry);
    }

    public void updateDetails(String type, String location, int year, int numberOfWinners) {
        this.type = type;
        this.location = location;
        this.year = year;
        this.numberOfWinners = numberOfWinners;
    }
}