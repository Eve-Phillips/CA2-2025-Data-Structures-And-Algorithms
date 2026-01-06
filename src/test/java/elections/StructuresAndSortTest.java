package elections;

import elections.model.CandidateEntry;
import elections.model.Election;
import elections.model.Politician;
import elections.structures.HashTable;
import elections.structures.MyArray;
import elections.structures.Sort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the custom data structures (MyArray, HashTable) and sorting methods.
 *
 * These tests verify:
 * - MyArray resizing and element shifting on removal
 * - HashTable basic operations and collision handling via linear probing
 * - Sorting correctness for politicians, candidates, and elections
 *
 * The aim is to confirm that core DS&A components behave predictably,
 * since the rest of the application depends on them.
 */
public class StructuresAndSortTest {

    /**
     * Key type with a deliberately poor hashCode implementation.
     *
     * All instances return the same hash code, forcing collisions in the hash table.
     * This makes it possible to test that linear probing is working correctly.
     */
    private static final class BadKey {
        private final String v;

        BadKey(String v) { this.v = v; }

        @Override
        public int hashCode() { return 1; }

        @Override
        public boolean equals(Object o) {
            return (o instanceof BadKey bk) && v.equals(bk.v);
        }
    }

    /**
     * Verifies MyArray add/get/remove:
     * - size increases on add
     * - elements are accessible by index
     * - remove shifts elements left to fill the gap
     */
    @Test
    void myArray_addGetRemove_behavesCorrectly() {
        MyArray<Integer> a = new MyArray<>();
        a.add(10);
        a.add(20);
        a.add(30);

        assertEquals(3, a.size());
        assertEquals(20, a.get(1));

        // Removing index 1 should shift the old index 2 element into index 1
        a.remove(1);
        assertEquals(2, a.size());
        assertEquals(30, a.get(1));
    }

    /**
     * Verifies MyArray grows beyond its initial capacity.
     *
     * The default capacity is 10, so adding 25 elements forces ensureCapacity()
     * to resize the backing array at least once.
     */
    @Test
    void myArray_growsBeyondInitialCapacity() {
        MyArray<Integer> a = new MyArray<>();

        for (int i = 0; i < 25; i++) a.add(i);

        assertEquals(25, a.size());
        assertEquals(0, a.get(0));
        assertEquals(24, a.get(24));
    }

    /**
     * Verifies HashTable put/get/remove on a small dataset.
     *
     * This checks that:
     * - put stores values
     * - get retrieves values
     * - remove makes an entry unreachable while leaving other entries intact
     */
    @Test
    void hashTable_putGetRemove_basic() {
        HashTable<String, Integer> ht = new HashTable<>(10);
        ht.put("a", 1);
        ht.put("b", 2);

        assertEquals(1, ht.get("a"));
        assertEquals(2, ht.get("b"));

        ht.remove("a");
        assertNull(ht.get("a"));
        assertEquals(2, ht.get("b"));
    }

    /**
     * Verifies that HashTable handles collisions using linear probing.
     *
     * BadKey forces all entries to start at the same hashed index.
     * Successful retrieval confirms that the probe sequence is consistent
     * for both insertion and lookup.
     */
    @Test
    void hashTable_handlesCollisions_linearProbing() {
        HashTable<BadKey, String> ht = new HashTable<>(5);

        ht.put(new BadKey("one"), "1");
        ht.put(new BadKey("two"), "2");
        ht.put(new BadKey("three"), "3");

        assertEquals("1", ht.get(new BadKey("one")));
        assertEquals("2", ht.get(new BadKey("two")));
        assertEquals("3", ht.get(new BadKey("three")));
    }

    /**
     * Verifies sorting politicians by name (insertion sort) is:
     * - ascending (A–Z)
     * - case-insensitive
     */
    @Test
    void sortPoliticiansByName_insertionSortAscendingIgnoreCase() {
        MyArray<Politician> ps = new MyArray<>();
        ps.add(new Politician("zeta", "x", "p", "c", "u"));
        ps.add(new Politician("Alpha", "x", "p", "c", "u"));
        ps.add(new Politician("beta", "x", "p", "c", "u"));

        Sort.sortPoliticiansByName(ps);

        assertEquals("Alpha", ps.get(0).getName());
        assertEquals("beta", ps.get(1).getName());
        assertEquals("zeta", ps.get(2).getName());
    }

    /**
     * Verifies sorting candidates by votes (selection sort) is:
     * - descending (highest votes first)
     */
    @Test
    void sortCandidatesByVotesDesc_selectionSortDescending() {
        Politician p1 = new Politician("A", "x", "p", "c", "u");
        Politician p2 = new Politician("B", "x", "p", "c", "u");
        Election e = new Election("General", "Ireland", 2024, 1);

        MyArray<CandidateEntry> cs = new MyArray<>();
        cs.add(new CandidateEntry(p1, e, "Independent", 100));
        cs.add(new CandidateEntry(p2, e, "Independent", 250));

        Sort.sortCandidatesByVotesDesc(cs);

        assertEquals("B", cs.get(0).getPolitician().getName());
        assertEquals(250, cs.get(0).getVotes());
        assertEquals("A", cs.get(1).getPolitician().getName());
        assertEquals(100, cs.get(1).getVotes());
    }

    /**
     * Verifies both election year sort methods:
     * - ascending (earliest to latest)
     * - descending (latest to earliest)
     */
    @Test
    void sortElectionsByYearAsc_andDesc_work() {
        MyArray<Election> es = new MyArray<>();
        es.add(new Election("General", "Ireland", 2024, 1));
        es.add(new Election("General", "Ireland", 2022, 1));
        es.add(new Election("General", "Ireland", 2023, 1));

        Sort.sortElectionsByYearAsc(es);
        assertEquals(2022, es.get(0).getYear());
        assertEquals(2023, es.get(1).getYear());
        assertEquals(2024, es.get(2).getYear());

        Sort.sortElectionsByYearDesc(es);
        assertEquals(2024, es.get(0).getYear());
        assertEquals(2023, es.get(1).getYear());
        assertEquals(2022, es.get(2).getYear());
    }
}