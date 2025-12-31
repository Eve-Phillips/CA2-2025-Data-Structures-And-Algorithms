package elections.model;

import elections.structures.MyArray;

public class Politician {

    private String name;
    private String dateOfBirth;     // store as String for simplicity (e.g. "1980-05-12")
    private String party;
    private String county;
    private String imageUrl;

    // elections this politician has stood in
    private MyArray<CandidateEntry> candidacies;

    public Politician(String name, String dateOfBirth, String party, String county, String imageUrl) {
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.party = party;
        this.county = county;
        this.imageUrl = imageUrl;
        this.candidacies = new MyArray<>();
    }

    public String getName() {
        return name;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getParty() {
        return party;
    }

    public String getCounty() {
        return county;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public MyArray<CandidateEntry> getCandidacies() {
        return candidacies;
    }

    public void addCandidacy(CandidateEntry entry) {
        candidacies.add(entry);
    }

    public void updateDetails(String name, String dob, String party, String county, String imageUrl) {
        this.name = name;
        this.dateOfBirth = dob;
        this.party = party;
        this.county = county;
        this.imageUrl = imageUrl;
    }
}

