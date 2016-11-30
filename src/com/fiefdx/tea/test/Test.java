package com.fiefdx.tea.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;

import com.fiefdx.logger.SimpleLogger;
import com.fiefdx.tea.Tea;

public class Test {
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static String repeat(String s, int times) {
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
		                   "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s %4$s    %5$s%n");
		try {
			SimpleLogger.setup("test.log", 1024 * 1024 * 5, 5, true, true, Level.ALL);
			
			int[] v = {3, 8};
			int[] k = {1, 2, 3, 4};
			Tea.teaEncrypt(v, k, 32);
			LOG.fine(String.format("encrypt v: %x, %x", v[0], v[1]));
			Tea.teaDecrypt(v, k, 32);
			LOG.fine(String.format("decrypt v: %x, %x", v[0], v[1]));
			
			long l = 0x0f0f0f0f0a0a0a0aL;
			long ll = l >> 32;
	    	LOG.fine("l >> 32: " + ll);
	        int i= (int)(ll & 0xffffffff);
	        LOG.fine("int i: " + i);
			
			int[] v1 = {5, 3, 3, 8, 5, 3, 3, 8, 5, 3, 3, 8};
			Tea.teaStrEncrypt(v1, k, 12, 32);
			LOG.fine(String.format("encrypt v: %x, %x, %x, %x, %x, %x, %x, %x, %x, %x, %x, %x", 
					               v1[0], v1[1], v1[2], v1[3],
					               v1[4], v1[5], v1[6], v1[7], 
					               v1[8], v1[9], v1[10], v1[11]));
			int pos = Tea.teaStrDecrypt(v1, k, 12, 32);
			LOG.fine(String.format("decrypt v: %x, %x, %x, %x, %x, %x, %x, %x, %x, %x, %x, %x", 
					               v1[0], v1[1], v1[2], v1[3],
					               v1[4], v1[5], v1[6], v1[7], 
					               v1[8], v1[9], v1[10], v1[11]));
			LOG.fine("pos: " + pos);
			LOG.fine(String.format("%x, %016x, %016x, %016x, ", 
					               (0xf60c63be00000000L | (long)0x9eb21270), 
					               ~(long)(0x00000000ffffffffL ^ 0x9eb21270), 
					               (long)(0x00000000ffffffffL ^ 0x9eb21270),
					               0x00000000ffffffffL & v1[0]));
			byte[] new_array = Tea.appendData(new byte[]{1, 2, 3, 4}, new byte[]{5, 6, 7, 8});
			LOG.fine(String.format("new_array: %s, length: %d", new_array.toString(), new_array.length));
			
			byte[] e_v = Tea.strEncrypt("This is a test!杨海涛".getBytes(),             //This is English string test!
										Tea.md5twice("111111").getBytes(),
					                    64);
			LOG.info("Encrypted_string: " + e_v.toString());
			byte[] d_v = Tea.strDecrypt(e_v, 
					                    Tea.md5twice("111111").getBytes(),
					                    64);
			LOG.info(String.format("Decrypted_string: %s, length: %d, %s", d_v.toString(), d_v.length, new String(d_v)));
			LOG.info(String.format("Original_string: %s, length: %d", "ThisThis".getBytes().toString(), "ThisThis".getBytes().length));
			
			byte[] original = new byte[]{1,2,3,4,5,6,7,8};
			int[] int_array = Tea.byteArrayToIntArray(original);
			byte[] byte_array = Tea.intArrayToByteArray(int_array);
			LOG.info(String.format("int_array: %s, byte_array: %s, original_byte_array: %s, %s", 
					               Arrays.toString(int_array), 
					               Arrays.toString(byte_array),
					               Arrays.toString(original),
					               Arrays.toString(new byte[]{1,2,3,4,5,6,7,8})));
			
			LOG.info(String.format("timestamp: %f", new Date().getTime()/1000.0));
			LOG.info(String.format("111111: sha1: %s, md5: %s, md5-2: %s", 
					               Tea.sha1sum("111111"), 
					               Tea.md5sum("111111"), 
					               Tea.md5twice("111111")));
			
			String o_str = "This is a test!";
			String k_str = "fe98c85c-d0e2-49c6-9a8b-1cc7900da9b3";
			String crypt_key = Tea.md5twice(k_str);
			String o_base64 = new String(Base64.encodeBase64(o_str.getBytes()));
			LOG.info(String.format("o_base64: %s, %s", o_base64, Arrays.toString(o_base64.getBytes())));
			byte[] e_byte = Tea.strEncrypt(o_base64.getBytes(), Tea.hexToByteArray(crypt_key));
			LOG.info(String.format("int_v: %s", Arrays.toString(Tea.byteArrayToIntArray(e_byte))));
			LOG.info(String.format("int_s: %s", Arrays.toString(e_byte)));
			LOG.info(String.format("byte_k: %s", Arrays.toString(Tea.hexToByteArray(crypt_key))));
			LOG.info(String.format("int_k: %s", Arrays.toString(Tea.byteArrayToIntArray(Tea.hexToByteArray(crypt_key)))));
			
			String e_base64 = new String(Base64.encodeBase64(e_byte));
			LOG.info(String.format("e_base64: '%s'", e_base64));
			
			String s_base64 = "69PIn55kC1aLCVrfYfwW2v5EKZoGeg+R";
			byte[] s_byte = Base64.decodeBase64(s_base64);
			LOG.info(String.format("s_string: %s, %d", Arrays.toString(s_byte), s_byte.length));
			String s_string = new String(s_byte);
			LOG.info(String.format("s_string: %s, %d, %s", Arrays.toString(s_string.getBytes()), 
					                                       s_string.getBytes().length, 
					                                       new String(s_string)));
			byte[] d_byte = Tea.strDecrypt(s_byte, Tea.hexToByteArray(crypt_key));
			LOG.info(String.format("d_byte: %s", new String(d_byte)));
			LOG.info(String.format("d_byte: %s", Arrays.toString(d_byte)));
			LOG.info(String.format("d_string: %s", new String(Base64.decodeBase64(d_byte))));
			
			int[] v2 = {1, 2, 3, 4};
			Tea.teaStrEncrypt(v2, Tea.byteArrayToIntArray(Tea.hexToByteArray(crypt_key)), 4, 32);
			LOG.fine(String.format("encrypt v: %x, %x, %x, %x", 
					               v2[0], v2[1], v2[2], v2[3]));
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}
	}
}

