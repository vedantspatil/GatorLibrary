//Min-heap implementation to perform insert, delete min, and delete a specific key from the heap

import java.sql.Timestamp;

public class PriorityQueue {
	public UserNode[] Heap;
	public int index;
	private int size;

	// constructor to initialize the Min Heap to max size 2000
	public PriorityQueue(int size) {
		this.size = size;
		this.index = 0;
		Heap = new UserNode[size];
	}
	// returns index of the parent node in the heap array
	private int parent(int i) {
		return (i - 1) / 2;
	}

	// returns index of the left child node in the heap array
	private int leftChild(int i) {
		return (i * 2) + 1;
	}

	// returns index of the right child node in the heap array
	private int rightChild(int i) {
		return (i * 2) + 2;
	}

	// returns true if the given node at index i is a leaf node
	private boolean isLeaf(int i) {
		if (leftChild(i) >= index) {// change2
			return true;
		}
		return false;
	}
    public boolean isEmpty(){
        // System.out.println("Index "+ index);
        return index<=0?true:false;
    }

    public int size(){
        return index;
    }

    public UserNode get(int i){
        return Heap[i];
    }
	// inserts Book into the MinHeap
	public UserNode add(UserNode element) {
		if (index >= size) {
			return null;
		}
		Heap[index] = element;
		int current = index;
		// compares ride costs and min cost rides are put at top of heap
        while (Heap[current].priority < Heap[parent(current)].priority
				|| (current != parent(current) && Heap[current].priority == Heap[parent(current)].priority)) {
			// compares trip durations for same cost rides and puts lesser duration rides on
			// top of heap
			if (Heap[current].priority == Heap[parent(current)].priority
					&& Heap[current].timestamp.compareTo(Heap[parent(current)].timestamp) < 0 ) {
				swap(current, parent(current));
				current = parent(current);
			} else if (Heap[current].priority < Heap[parent(current)].priority) {
				swap(current, parent(current));
				current = parent(current);
			}else {
				break;
			}
		}
		index++;
		return Heap[current];
	}

	// removes and returns the min element from the heap
	public UserNode poll() {
		if (index == 0)
			return null;

		// root is the minimum since it is a min heap
		UserNode popped = Heap[0]; // 2

		if (index != 1) {
			Heap[0] = Heap[--index];// 1
			minHeapify(0);
		} else {
			index--;
			Heap[0] = null;
		}
		return popped == null ? null : popped;
	}

	// deletes a ride at a given index from the min heap
	public UserNode deleteKey(int i) {
		UserNode delNode = Heap[i];
		decreaseKey(i, new UserNode("", Integer.MIN_VALUE,new Timestamp(System.currentTimeMillis())));
		poll();
		return delNode;
	}

	// bubbles up the deleted ride to the top of the min heap for remove min
	// operation
	private void decreaseKey(int i, UserNode new_val) {
		Heap[i] = new_val;
		while (i != 0 && ((Heap[parent(i)].priority > Heap[i].priority)
				|| (i != parent(i) && (Heap[parent(i)].priority == Heap[i].priority)
						&& (Heap[parent(i)].timestamp.compareTo(Heap[i].timestamp))>1))) {
			{
				swap(i, parent(i));
				i = parent(i);
			}
		}
	}

	// performs min-heap heapify operation at index i
	private void minHeapify(int i) {

		// for a non-leaf node with any child having a lesser/equal ride cost
		if (!isLeaf(i)) {

			if (Heap[i] != null && (Heap[leftChild(i)] != null && Heap[i].priority >= Heap[leftChild(i)].priority)
					|| (Heap[rightChild(i)] != null && Heap[i].priority >= Heap[rightChild(i)].priority)) {

				// swapping the minimum of left and right child value with the parent
				if (Heap[leftChild(i)].priority < Heap[rightChild(i)].priority) {
					// comparing trip durations of rides having same cost
					if ((Heap[leftChild(i)].priority == Heap[i].priority)
							&& (Heap[leftChild(i)].timestamp.compareTo(Heap[i].timestamp))<1) {
						swap(i, leftChild(i));
						minHeapify(leftChild(i));
					} else if (Heap[leftChild(i)].priority < Heap[i].priority) {
						swap(i, leftChild(i));
						minHeapify(leftChild(i));
					}
				} else {
					// comparing trip durations of rides having same cost
					if (Heap[rightChild(i)].priority == Heap[leftChild(i)].priority) {
						if (Heap[rightChild(i)].timestamp.compareTo(Heap[leftChild(i)].timestamp)<1) {
							swap(i, rightChild(i));
							minHeapify(rightChild(i));
						} else {
							swap(i, leftChild(i));
							minHeapify(leftChild(i));
						}
					} else if ((Heap[rightChild(i)].priority == Heap[i].priority)
							&& (Heap[rightChild(i)].timestamp.compareTo(Heap[i].timestamp)<1)) {
						swap(i, rightChild(i));
						minHeapify(rightChild(i));
					} else if (Heap[rightChild(i)].priority < Heap[i].priority) {
						swap(i, rightChild(i));
						minHeapify(rightChild(i));
					}
				}
			}
		}
	}

	// swapping 2 nodes in the min heap
	private void swap(int x, int y) {
		UserNode tmp;
		tmp = Heap[x];
		Heap[x] = Heap[y];
		Heap[y] = tmp;
	}

}
