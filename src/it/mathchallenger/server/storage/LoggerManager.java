package it.mathchallenger.server.storage;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class LoggerManager {
	private final static int LOG_SIZE  = 1048576;
	private final static int LOG_COUNT = 1000;

	public static Logger newLogger(String nome) {
		try {
			Handler handler = new FileHandler(nome + ".log", LOG_SIZE, LOG_COUNT, true);
			Logger log = Logger.getLogger(nome);
			log.addHandler(handler);
			return log;
		}
		catch (SecurityException | IOException e) {
			return Logger.getLogger(nome);
		}

	}
}
