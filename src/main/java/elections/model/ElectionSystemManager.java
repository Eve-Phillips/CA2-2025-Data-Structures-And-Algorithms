package elections.model;

import elections.structures.HashTable;
import elections.structures.MyArray;
import elections.structures.Sort;

public class ElectionSystemManager {
    private HashTable<String, Politician> politicians;
    private HashTable<String, Election> elections;

    public ElectionSystemManager() {
        //  the size can be anything im just using 100 for now.
        this.politicians = new HashTable<>(100);
        this.elections = new HashTable<>(100);
    }

    //-------------------------
    // Politician Management
    //-------------------------

    public boolean addPolitician(String name, String dateOfBirth, String party, String county, String imageUrl) {
        if(politicians.get(name) != null) {
            return false; // already exists, dont add.
        }

        Politician p = new Politician(name, dateOfBirth, party, county, imageUrl);
        politicians.put(name, p);
        return true;
    }

    public Politician getPolitician(String name) {
        return politicians.get(name);
    }

    public boolean deletePolitician(String name) {
        if (politicians.get(name) == null) return false;
        politicians.remove(name);
        return true;
    }

    public boolean updatePolitician(String name, String newName, String dateOfBirth, String party, String county, String imageUrl) {
        Politician p = politicians.get(name);
        if(p == null) return false;

        // if the politicians name is changed, remove and reinsert it
        if(!name.equals(newName)) {
            politicians.remove(name);
            p.updateDetails(newName, dateOfBirth, party, county, imageUrl);
            politicians.put(newName, p);
        } else {
            p.updateDetails(newName, dateOfBirth, party, county, imageUrl);
        }
        return true;
    }

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

    public MyArray<Politician> searchPoliticiansByName(String part) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 100; i++) {
            Politician p = politicians.getFromIndex(i); // need helper below
            if (p != null && p.getName().toLowerCase().contains(part.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

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

    public MyArray<Politician> searchPoliticiansByNameSorted(String part) {
        MyArray<Politician> results = searchPoliticiansByName(part);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    public MyArray<Politician> searchPoliticiansByPartySorted(String party) {
        MyArray<Politician> results = searchPoliticiansByParty(party);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    public MyArray<Politician> searchPoliticiansByCountySorted(String county) {
        MyArray<Politician> results = searchPoliticiansByCounty(county);
        Sort.sortPoliticiansByName(results);
        return results;
    }

    //-------------------------
    // Election Management
    //-------------------------

    private String buildElectionKey(String type, int year, String location) {
        return type + "-" + year + "-" + location;
    }

    public boolean addElection(String type, String location, int year, int numberOfWinners) {
        String key = buildElectionKey(type, year, location);

        if (elections.get(key) != null) {
            return false; // exists already
        }

        Election e = new Election(type, location, year, numberOfWinners);
        elections.put(key, e);
        return true;
    }

    public Election getElection(String type, int year, String location) {
        return elections.get(buildElectionKey(type, year, location));
    }

    public boolean deleteElection(String type, int year, String location) {
        String key = buildElectionKey(type, year, location);

        if (elections.get(key) == null) return false;
        elections.remove(key);
        return true;
    }

    public boolean updateElection(String type, int year, String location,
                                  String newType, String newLocation,
                                  int newYear, int winners) {

        String oldKey = buildElectionKey(type, year, location);
        Election e = elections.get(oldKey);
        if (e == null) return false;

        // Remove old key if changed
        String newKey = buildElectionKey(newType, newYear, newLocation);
        elections.remove(oldKey);

        // Update details
        e.updateDetails(newType, newLocation, newYear, winners);

        // Reinsert using new key
        elections.put(newKey, e);
        return true;
    }

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
    public boolean addCandidate(String politicianName, String type, int year, String location,
                                String partyAtTime, int votes) {

        Politician p = politicians.get(politicianName);
        Election e = getElection(type, year, location);

        if (p == null || e == null) return false;

        // block duplicates
        if (findCandidateIndex(e, politicianName) >= 0) return false;

        CandidateEntry ce = new CandidateEntry(p, e, partyAtTime, votes);
        p.addCandidacy(ce);
        e.addCandidate(ce);
        return true;
    }


    private int findCandidateIndex(Election e, String politicianName) {
        MyArray<CandidateEntry> cands = e.getCandidates();
        for (int i = 0; i < cands.size(); i++) {
            CandidateEntry ce = cands.get(i);
            if (ce.getPolitician().getName().equalsIgnoreCase(politicianName)) {
                return i;
            }
        }
        return -1;
    }

    public boolean updateCandidate(String politicianName, String type, int year, String location,
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

    public boolean deleteCandidate(String politicianName, String type, int year, String location) {
        Election e = getElection(type, year, location);
        Politician p = getPolitician(politicianName);
        if (e == null || p == null) return false;

        // remove from election list
        int eIdx = findCandidateIndex(e, politicianName);
        if (eIdx < 0) return false;
        CandidateEntry entry = e.getCandidates().get(eIdx);
        e.getCandidates().remove(eIdx);

        // remove from politician candidacies list (same election)
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

    public MyArray<CandidateEntry> getCandidatesSortedByVotes(String type, int year, String location) {
        Election e = getElection(type, year, location);
        if (e == null) return new MyArray<>();

        MyArray<CandidateEntry> copy = new MyArray<>();
        MyArray<CandidateEntry> original = e.getCandidates();
        for (int i = 0; i < original.size(); i++) copy.add(original.get(i));

        Sort.sortCandidatesByVotesDesc(copy);
        return copy;
    }

}
