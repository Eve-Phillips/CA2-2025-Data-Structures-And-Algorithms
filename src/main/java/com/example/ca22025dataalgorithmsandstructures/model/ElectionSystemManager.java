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
            return false; // already exists, dont add
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
}
