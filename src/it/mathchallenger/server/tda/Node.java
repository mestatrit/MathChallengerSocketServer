package it.mathchallenger.server.tda;

public class Node<E> {
	private E	   element;
	private Node<E> next;

	public Node() {
		this(null, null);
	}

	public Node(E e, Node<E> n) {
		this.element = e;
		next = n;
	}

	public E element() {
		return element;
	}

	public Node<E> next() {
		return next;
	}

	public void setNext(Node<E> n) {
		next = n;
	}

	public String toString() {
		return element + "";
	}
}