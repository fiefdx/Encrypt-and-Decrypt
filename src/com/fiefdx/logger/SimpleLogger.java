package com.fiefdx.logger;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.Handler;

public class SimpleLogger {
	static private FileHandler fileText;
	static private SimpleFormatter formatterText;
	
	static public void setup(String pattern, 
			                 int limit, 
			                 int count, 
			                 boolean append, 
			                 boolean console, 
			                 Level level) throws IOException {
		// get the global logger to configure it
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
		// suppress the logging output to the console
		Logger rootLogger = Logger.getLogger("");
		Handler[] handlers = rootLogger.getHandlers();
		if (console == false) {
			if (handlers[0] instanceof ConsoleHandler) {
				rootLogger.removeHandler(handlers[0]);
			}
		} else {
			if (handlers[0] instanceof ConsoleHandler) {
				handlers[0].setLevel(level);
			}
		}
		
		logger.setLevel(level);
		fileText = new FileHandler(pattern, limit, count, append);
		
		// create a Text formatter
		formatterText = new SimpleFormatter();
		fileText.setFormatter(formatterText);
		logger.addHandler(fileText);
	}
}
