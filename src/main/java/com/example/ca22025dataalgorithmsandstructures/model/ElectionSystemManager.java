package com.example.ca22025dataalgorithmsandstructures.model;

import com.example.ca22025dataalgorithmsandstructures.structures.HashTable;
import com.example.ca22025dataalgorithmsandstructures.structures.MyArray;

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

    //-------------------------
    // Search Politicians
    //-------------------------

    public MyArray<Politician> searchPoliticiansByName(String part) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 200; i++) {
            Politician p = politicians.getFromIndex(i); // need helper below
            if (p != null && p.getName().toLowerCase().contains(part.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    public MyArray<Politician> searchPoliticiansByParty(String party) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 200; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null && p.getParty().equalsIgnoreCase(party)) {
                results.add(p);
            }
        }
        return results;
    }

    public MyArray<Politician> searchPoliticiansByCounty(String county) {
        MyArray<Politician> results = new MyArray<>();

        for (int i = 0; i < 200; i++) {
            Politician p = politicians.getFromIndex(i);
            if (p != null && p.getCounty().equalsIgnoreCase(county)) {
                results.add(p);
            }
        }
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

    // -------------------------------
    // Search Elections
    // -------------------------------
    public MyArray<Election> searchElectionsByYear(int year) {
        MyArray<Election> results = new MyArray<>();

        for (int i = 0; i < 200; i++) {
            Election e = elections.getFromIndex(i);
            if (e != null && e.getYear() == year) {
                results.add(e);
            }
        }
        return results;
    }

    public MyArray<Election> searchElectionsByType(String type) {
        MyArray<Election> results = new MyArray<>();

        for (int i = 0; i < 200; i++) {
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
    public boolean addCandidate(String politicianName, String type, int year, String location, String partyAtTime, int votes) {
        Politician p = politicians.get(politicianName);
        Election e = getElection(type, year, location);

        if (p == null || e == null) return false;

        // Create entry
        CandidateEntry ce = new CandidateEntry(p, e, partyAtTime, votes);

        // Link both ways
        p.addCandidacy(ce);
        e.addCandidate(ce);

        return true;
    }
}
