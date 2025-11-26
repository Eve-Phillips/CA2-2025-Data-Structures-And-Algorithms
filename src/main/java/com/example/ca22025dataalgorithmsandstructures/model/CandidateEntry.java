package com.example.ca22025dataalgorithmsandstructures.model;

public class CandidateEntry {

    private Politician politician;      // reference back to the politician
    private Election election;          // reference to the election
    private String partyAtTheTime;      // may differ from politician.getParty()
    private int votes;

    public CandidateEntry(Politician politician, Election election, String partyAtTheTime, int votes) {
        this.politician = politician;
        this.election = election;
        this.partyAtTheTime = partyAtTheTime;
        this.votes = votes;
    }

    public Politician getPolitician() {
        return politician;
    }

    public Election getElection() {
        return election;
    }

    public String getPartyAtTheTime() {
        return partyAtTheTime;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int newVotes) {
        this.votes = newVotes;
    }

    public void setPartyAtTheTime(String newParty) {
        this.partyAtTheTime = newParty;
    }
}

