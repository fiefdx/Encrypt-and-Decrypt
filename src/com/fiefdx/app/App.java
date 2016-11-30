package com.fiefdx.app;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import argparser.ArgParser;
import argparser.StringHolder;
import argparser.BooleanHolder; 
import argparser.IntHolder; 

import com.fiefdx.logger.SimpleLogger;
import com.fiefdx.tea.Tea;

public class App {
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
                           "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s %4$s    %5$s%n");
		try {
			SimpleLogger.setup("test.log", 1024 * 1024 * 5, 5, true, true, Level.ALL);
			StringHolder src_file_path = new StringHolder();
			StringHolder tar_file_path = new StringHolder();
			BooleanHolder cmd_flag = new BooleanHolder();
			BooleanHolder encrypt_flag = new BooleanHolder();
			BooleanHolder decrypt_flag = new BooleanHolder();
			BooleanHolder help_flag = new BooleanHolder();
			IntHolder encrypt_level = new IntHolder();
			
			// -c -l 1 -e -d -h -s ./a/b/src.file -o ./a/b/tar.file
			ArgParser parser = new ArgParser("java -jar App.jar");
			parser.addOption("-c %v #command line mode", cmd_flag);
			parser.addOption("-l %i {0, 1}#encrypt level: 0 or 1, default 0", encrypt_level);
			parser.addOption("-e %v #encrypt file", encrypt_flag);
		    parser.addOption("-d %v #decrypt file", decrypt_flag);
		    parser.addOption("-s %s #path to source file", src_file_path);
		    parser.addOption("-o %s #path to target file", tar_file_path);
		    parser.addOption("-h %v #see help information", help_flag);
		    
		    parser.matchAllArgs(args);
		    
		    LOG.fine(String.format("src_file_path: %s", src_file_path.value));
		    LOG.fine(String.format("tar_file_path: %s", tar_file_path.value));
		    LOG.fine(String.format("encrypt_flag: %b", encrypt_flag.value));
		    LOG.fine(String.format("decrypt_flag: %b", decrypt_flag.value));
		    LOG.fine(String.format("cmd_flag: %b", cmd_flag.value));
		    LOG.fine(String.format("encrypt_level: %d", encrypt_level.value));
		    LOG.fine(String.format("help_flag: %b", help_flag.value));
		    
		    
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}
	}
}