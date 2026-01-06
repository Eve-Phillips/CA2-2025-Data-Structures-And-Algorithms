package elections.structures;

import elections.model.CandidateEntry;
import elections.model.Election;
import elections.model.Politician;

/**
 * Sorting utilities for the custom MyArray data structure.
 *
 * These methods implement simple, well-known sorting algorithms
 * (selection sort and insertion sort) to keep the logic explicit
 * and easy to follow in a data structures context.
 */
public class Sort {

    // ------------------------------------------------------------
    // SELECTION SORT (DESC by votes)
    // ------------------------------------------------------------

    /**
     * Sorts CandidateEntry objects in descending order by vote count.
     *
     * Selection sort works by selecting the highest remaining element
     * and swapping it into the next sorted position.
     *
     * This method sorts the array in-place (it modifies the input MyArray).
     */
    public static void sortCandidatesByVotesDesc(MyArray<CandidateEntry> a) {

        // sp = "sorted position": everything before sp is already sorted
        for (int sp = 0; sp < a.size() - 1; sp++) {
            int highestIndex = sp;

            // Find the candidate with the highest votes in the unsorted region
            for (int i = sp + 1; i < a.size(); i++) {
                if (a.get(i).getVotes() > a.get(highestIndex).getVotes()) {
                    highestIndex = i;
                }
            }

            // Swap highest into the current sorted position if needed
            if (highestIndex != sp) {
                CandidateEntry swap = a.get(sp);
                a.set(sp, a.get(highestIndex));
                a.set(highestIndex, swap);
            }
        }
    }

    // ------------------------------------------------------------
    // INSERTION SORT (ASC by politician name A–Z)
    // ------------------------------------------------------------

    /**
     * Sorts Politician objects alphabetically by name (A–Z),
     * using case-insensitive comparison.
     *
     * Insertion sort builds a sorted prefix of the array by taking the next
     * element and shifting larger elements one position to the right.
     *
     * This method sorts the array in-place.
     */
    public static void sortPoliticiansByName(MyArray<Politician> a) {

        // Start from the second element and insert it into the sorted prefix [0..e-1]
        for (int e = 1; e < a.size(); e++) {
            Politician elem = a.get(e);
            int i;

            // Shift elements right until the correct insertion position is found
            for (i = e;
                 i >= 1 && a.get(i - 1).getName().compareToIgnoreCase(elem.getName()) > 0;
                 i--) {
                a.set(i, a.get(i - 1));
            }

            // Place the extracted element into its correct sorted position
            a.set(i, elem);
        }
    }

    // ------------------------------------------------------------
    // INSERTION SORT (ASC by election year)
    // ------------------------------------------------------------

    /**
     * Sorts elections by year in ascending order (earliest to latest).
     *
     * This method sorts the array in-place using insertion sort.
     */
    public static void sortElectionsByYearAsc(MyArray<Election> a) {
        for (int e = 1; e < a.size(); e++) {
            Election elem = a.get(e);
            int i;

            // Move later years one step right to make space for the current element
            for (i = e; i >= 1 && a.get(i - 1).getYear() > elem.getYear(); i--) {
                a.set(i, a.get(i - 1));
            }
            a.set(i, elem);
        }
    }

    // ------------------------------------------------------------
    // INSERTION SORT (DESC by election year)
    // ------------------------------------------------------------

    /**
     * Sorts elections by year in descending order (latest to earliest).
     *
     * This method sorts the array in-place using insertion sort.
     */
    public static void sortElectionsByYearDesc(MyArray<Election> a) {
        for (int e = 1; e < a.size(); e++) {
            Election elem = a.get(e);
            int i;

            // Move earlier years one step right to make space for the current element
            for (i = e; i >= 1 && a.get(i - 1).getYear() < elem.getYear(); i--) {
                a.set(i, a.get(i - 1));
            }
            a.set(i, elem);
        }
    }
}

