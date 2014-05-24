package it.mathchallenger.server.tda;

public class NodeQueue<E> implements Queue<E> {
	private Node<E> front, rear;
	private int	 size;

	public NodeQueue() {
		front = null;
		rear = null;
		size = 0;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public E front() throws EmptyQueueException {
		if (isEmpty())
			throw new EmptyQueueException("Coda vuota");
		return front.element();
	}

	@Override
	public void enqueue(E elem) {
		Node<E> newItem = new Node<E>(elem, null);
		if (isEmpty()) {
			front = newItem;
			rear = newItem;
		}
		else {
			rear.setNext(newItem);
			rear = newItem;
		}
		size++;
	}

	@Override
	public E dequeue() throws EmptyQueueException {
		if (isEmpty())
			throw new EmptyQueueException("Coda vuota");
		E e = front.element();
		size--;
		front = front.next();
		return e;
	}

	public String toString() {
		int size = size();
		int i = 0;
		String ret = "[(";
		while (i < size) {
			E elem = dequeue();
			enqueue(elem);
			i++;
			ret += elem;
			if (i <= size - 1)
				ret += ",";
		}
		ret += ")]";
		return ret;
	}
}
