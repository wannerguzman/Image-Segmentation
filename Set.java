// This is used in DisjointSets<T> to store actual data in the same sets.
import java.util.AbstractCollection; 


import java.util.Iterator;

/**
 * You will realize this class Set<T> is in fact just a list.
 * Because DisjointSets<T> ensures that all values stored in Set<T> must be unique, 
 * but should it be array list or linked list??
 * I Implemented my set similar to a linked list create a node and having a pointer to next node.
 * With private instance variables head and tail and size.
 * 
 * @author Wanner
 *
 * @param <T>
 */
public class Set<T> extends AbstractCollection<T> {
	/**
	 * Private class Node with: data and pointer to next node.
	 * 
	 * @author wanner
	 *
	 * @param <T>
	 */
	private class Node {
		T data;
		Node next;

		/**
		 * Constructor for Node. set data to value given.
		 * @param value
		 */
		public Node(T value) {
			this.data = value;
		}
	}

	private Node head;
	private Node tail;
	private int size = 0;

	/**
	 * O(1)
	 * @param item
	 * @return True if item was added else it was probably a null so throw exception.
	 * If list has no been initialized set head and tail to new node else
	 * make tail next to newNode from item and tail to newNode.
	 */
	public boolean add(T item) {
		if (item == null) {
			throw new NullPointerException();
		}
		Node newNode = new Node(item);
		//If head has not been initialized, initialize head.
		if (head == null && tail == null) {
			head = newNode;
			tail = newNode;
		}
		//Else we are adding.
		else {
			tail.next = newNode;
			tail = newNode;
		}
		//Increment size when add is successful.
		size++;
		return true;
	}


	/**
	 * O(1)
	 * Throw NullPointerException if other is null.
	 * @param other
	 * @return True all were added else false set tail.next to other.head and update.
	 * This tail to other tail return true add the other size to this size.
	 */
	public boolean addAll(Set<T> other) {
		if (other == null) {
			throw new NullPointerException();
		}
		tail.next = other.head;
		tail = other.tail;
		size += other.size;
		return true;
	}


	/**
	 *  O(1)
	 * Clear the list calling its clear method set tail null and head null set size 0.
	 */
	public void clear() {
		tail = null;
		head = null;
		size = 0;
	}


	/**
	 * O(1)
	 * @return the size of the list.
	 */
	public int size() {
		return size;
	}

	/**
	 * Iterator with methods next and hasNext.
	 * Create a temporary variable to equal to head.
	 * if it is equal to null it does not have next, else it has next.
	 * Next gets the data and returns it.
	 */
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			private Node temp = head;

			public T next() {
				if (!hasNext())
					throw new RuntimeException();
				T dataTemp = temp.data;
				temp = temp.next;
				return dataTemp;
			}

			public boolean hasNext() {
				return temp != null;
			}
		};
	}
}