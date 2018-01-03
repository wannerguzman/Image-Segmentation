import java.util.ArrayList;

// Disjoint sets class, using union by size and path compression.
/**
 * DisjointSets data structure, store a set of sets that are  not-merged or in other words disjoint.
 * Private instance variable array integer s and ArrayList of sets integer size.
 * 
 * @author wanner
 *
 * @param <T>
 */
public class DisjointSets<T> {

	// Data
	private int[] s;
	private ArrayList<Set<T>> sets;
	private int size;

	/**
	 * Constructor initialize the sets and s with size of data create set for each.
	 * Data and add the set to ArrayList at the same replace every spot in s with -1.
	 * 
	 * @param data
	 */
	public DisjointSets(ArrayList<T> data) {
		s = new int[data.size()];
		sets = new ArrayList<Set<T>>();
		for (int i = 0; i < data.size(); i++) {
			Set<T> tempset = new Set<T>();
			tempset.add(data.get(i));
			sets.add(tempset);
			s[i] = -1;
		}
		size = sets.size();
	}
	
	/**
	 * Must have O(1) time complexity.
	 * We check and makes sure that it is a root, if not throw an exception if root
	 * is greater or equal to the size of array, throw exception if the root is less
	 * than 0. Increase the size will increase negatively s-- from each union.
	 * If the size of the set in root1 is greater root2, then merge root2 to root1.
	 * Else if the size of set root2 is greater than root1, then merge root1 to root2.
	 * 
	 * @param root1
	 * @param root2
	 * @return the new root from the union of root1 and root2.
	 */
	public int union(int root1, int root2) {
		if (sets.get(root1) == null || sets.get(root2) == null)
			throw new RuntimeException("root is null");
		if (root1 < 0 || root2 < 0)
			throw new RuntimeException("one of the roots is below 0, not a valid index");
		if (root1 >= s.length || root2 >= s.length)
			throw new RuntimeException("one of roots is above size of sets, not a valid index");
		size--;
		if (s[root1] < s[root2] || s[root1] == s[root2]) {
			sets.get(root1).addAll(sets.get(root2));
			sets.get(root2).clear();
			sets.set(root2, null);
			s[root1] = s[root1] + s[root2];
			s[root2] = root1; // Root1 equal or taller; make root1 new root.
			return root1;
		} else {
			sets.get(root2).addAll(sets.get(root1));
			sets.get(root1).clear();
			sets.set(root1, null);
			s[root2] = s[root1] + s[root2];
			s[root1] = root2; // Root1 equal or taller; make root1 new root.
			return root2;
		}
	}
	
	/**
	 * Find x and return the root.
	 * Must implement path compression.
	 * @param x
	 * @return the root of x
	 */
	public int find(int x) {
		if (s[x] < 0) {
			return x;
		} else {
			s[x] = find(s[x]);
			return s[x];
		}
	}

	/**
	 * Get all the data in the same set.
	 * Must have O(1) time complexity.
	 * We check and makes sure that it is a root, if not throw an exception if root
	 * is greater or equal to the size of array throw exception if the root is less
	 * than 0 throw an exception else just get the root.
	 * 
	 * @param root
	 * @return the set at given root
	 */
	public Set<T> get(int root) {
		if (sets.get(root) == null)
			throw new RuntimeException("the root is null, not a valid root");
		if (root < 0)
			throw new RuntimeException("the root is below 0, not a valid index");
		if (root >= s.length)
			throw new RuntimeException("the root is above the length of sets, not a valid index");
		return sets.get(root);
	}

	/**
	 * Must be O(1) time complexity.
	 * @return the number of disjoint sets remaining
	 */
	public int getNumSets() {
		return size;
	}
}

