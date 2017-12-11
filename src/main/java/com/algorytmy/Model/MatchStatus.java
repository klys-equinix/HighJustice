package com.algorytmy.Model;

public enum MatchStatus {
    PENDING("Pending"),
    IN_PROGRESS("In progress"),
    ENDED("Ended");

    private final String name;

    MatchStatus(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return name.equals(otherName);
    }

    public String toString() {
        return this.name;
    }
}
