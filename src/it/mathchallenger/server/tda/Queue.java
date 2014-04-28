package it.mathchallenger.server.tda;

public interface Queue<E> {
	public int size();
	public boolean isEmpty();
	public E front() throws EmptyQueueException;
	public void enqueue(E elem);
	public E dequeue() throws EmptyQueueException;
}
