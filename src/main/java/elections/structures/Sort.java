package elections.structures;

import elections.model.CandidateEntry;
import elections.model.Politician;

public class Sort {

    // ------------------------------------------------------------
    // SELECTION SORT (DESC by votes)
    // Finds the candidate with the highest votes and moves it forward
    // ------------------------------------------------------------
    public static void sortCandidatesByVotesDesc(MyArray<CandidateEntry> a) {

        for (int sp = 0; sp < a.size() - 1; sp++) {   // selection point
            int highestIndex = sp;                    // assume highest so far

            for (int i = sp + 1; i < a.size(); i++) {
                if (a.get(i).getVotes() > a.get(highestIndex).getVotes()) {
                    highestIndex = i;
                }
            }

            // swap if needed
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
    public static void sortPoliticiansByName(MyArray<Politician> a) {

        for (int e = 1; e < a.size(); e++) {
            Politician elem = a.get(e);
            int i;

            for (i = e; i >= 1
                    && a.get(i - 1).getName().compareToIgnoreCase(elem.getName()) > 0; i--) {
                a.set(i, a.get(i - 1)); // shift right
            }

            a.set(i, elem); // insert element
        }
    }
}
