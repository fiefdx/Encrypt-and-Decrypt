package com.fiefdx.logger.test;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

import com.fiefdx.logger.SimpleLogger;

public class Test {
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static String repeat(String s, int times) {
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}
	
	public void doSomeThingToLog() {
		// LOG.setLevel(Level.SEVERE);
		LOG.severe("Info log");
		LOG.warning("Info log");
		LOG.info("Info log");
		LOG.finest("Really not important");
		
		// LOG.setLevel(Level.INFO);
	    LOG.severe("Info log");
	    LOG.warning("Info log");
	    LOG.info("Info log");
	    LOG.finest("Really not important");
	}
	
	public void randomTest(int num) {
		Random random = new Random();
		for(int i = 0; i < num; i++) {
			LOG.info("Random char(" + i + "): " + (char)random.nextInt(0xff));
		}
		LOG.info("String add a char: " + 'A' + repeat(String.valueOf('A'), 10));
		LOG.info("Integer valueOf: " + Long.valueOf("ffffffff", 16).intValue() + " " + ((int)-1 == 0xffffffff) + " \0");
		LOG.info("2 % 8: " + (-2 % 8));
		byte[] b = "TestTest".getBytes();
		LOG.info("String to Byte[]: " + b.length);
		for (byte c:b) {
			LOG.info("Char: " + (int)c);
		}
	}

	public static void main(String[] args) {
		Test tester = new Test();
		System.setProperty("java.util.logging.SimpleFormatter.format", 
		                   "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s %4$s    %5$s%n");
		try {
			SimpleLogger.setup("test.log", 1024 * 1024 * 5, 5, true, true, Level.INFO);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}
		tester.doSomeThingToLog();
		tester.randomTest(100);
	}
}
