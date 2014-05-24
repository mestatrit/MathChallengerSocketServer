package it.mathchallenger.server.tda;

public class EmptyQueueException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyQueueException(String s) {
		super(s);
	}

	public EmptyQueueException() {
		super();
	}
}
