package com.fiefdx.tea;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.fiefdx.logger.SimpleLogger;

public class Tea {
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static byte[] hexToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static String sha1sum(String msg) throws NoSuchAlgorithmException {
		MessageDigest sha = MessageDigest.getInstance("SHA1");
		byte[] hash_byte = sha.digest(msg.getBytes());
		BigInteger big_int = new BigInteger(1, hash_byte);
		String hash_text = big_int.toString(16);
		while(hash_text.length() < 40) {
			hash_text = "0" + hash_text;
		}
		return hash_text;
	}
	
	public static String md5sum(String msg) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] hash_byte = md.digest(msg.getBytes());
		BigInteger big_int = new BigInteger(1, hash_byte);
		String hash_text = big_int.toString(16);
		while(hash_text.length() < 32) {
			hash_text = "0" + hash_text;
		}
		return hash_text;
	}
	
	public static String md5twice(String msg) throws NoSuchAlgorithmException {
		String hash_text = md5sum(msg);
		hash_text = md5sum(hash_text);
		return hash_text;
	}
	
	public static String repeat(String s, int times) {
	    if (times <= 0) return "";
	    else return s + repeat(s, times-1);
	}
	
	public static byte[] appendData(byte firstObject,byte[] secondObject){
	    byte[] byteArray= {firstObject};
	    return appendData(byteArray, secondObject);
	}

	public static byte[] appendData(byte[] firstObject,byte secondByte){
	    byte[] byteArray= {secondByte};
	    return appendData(firstObject, byteArray);
	}

	public static byte[] appendData(byte[] firstObject,byte[] secondObject){
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    try {
	        if (firstObject!=null && firstObject.length!=0)
	            outputStream.write(firstObject);
	        if (secondObject!=null && secondObject.length!=0)   
	            outputStream.write(secondObject);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return outputStream.toByteArray();
	}
	
	public static byte[] intToByteArray(int intValue) {
		ByteBuffer b = ByteBuffer.allocate(4);
		b.putInt(intValue);
		byte[] result = b.array();
		return result;
	}
	
	public static int byteArrayToInt(byte[] byteArray) {
		return ByteBuffer.wrap(byteArray).getInt();
	}
	
	public static byte[] longToByteArray(long longValue) {
		ByteBuffer b = ByteBuffer.allocate(8);
		b.putLong(longValue);
		byte[] result = b.array();
		return result;
	}
	
	public static long byteArrayToLong(byte[] byteArray) {
		return ByteBuffer.wrap(byteArray).getLong();
	}
	
	public static byte[] intArrayToByteArray(int[] intArray) {
		ByteBuffer byte_buf = ByteBuffer.allocate(intArray.length * 4);
		IntBuffer int_buf = byte_buf.asIntBuffer();
		int_buf.put(intArray);
		byte[] byte_array = byte_buf.array();
		return byte_array;
	}
	
	public static int[] byteArrayToIntArray(byte[] byteArray) {
		IntBuffer int_buf = ByteBuffer.wrap(byteArray)
                .order(ByteOrder.BIG_ENDIAN)
                .asIntBuffer();
		int[] int_array = new int[int_buf.remaining()];
		int_buf.get(int_array);
		return int_array;
	}
	
	public static void teaEncrypt(int[] v, int[] k, int tea_sum) {
		int v0 = v[0], v1 = v[1], sum = 0;
		int delta = 0x9e3779b9;
		int k0 = k[0], k1 = k[1], k2 = k[2], k3 = k[3];
		for (int i = 0; i < tea_sum; i++) {
	        sum += delta;
	        v0 += ((v1<<4) + k0) ^ (v1 + sum) ^ ((v1>>>5) + k1);
	        v1 += ((v0<<4) + k2) ^ (v0 + sum) ^ ((v0>>>5) + k3);
	    } 
		v[0] = v0; v[1] = v1;
	}
	
	public static void teaDecrypt(int[] v, int[] k, int tea_sum) {
		int v0 = v[0], v1 = v[1], sum = 0xc6ef3720;
		int delta = 0x9e3779b9;
		int k0 = k[0], k1 = k[1], k2 = k[2], k3 = k[3];
		if (tea_sum == 64) {
	        sum=0x8DDE6E40;
	    }
	    for (int i = 0; i < tea_sum; i++) {
	        v1 -= ((v0<<4) + k2) ^ (v0 + sum) ^ ((v0>>>5) + k3);
	        v0 -= ((v1<<4) + k0) ^ (v1 + sum) ^ ((v1>>>5) + k1);
	        sum -= delta;
	    }
	    v[0] = v0; v[1] = v1;
	}
	
	public static void teaStrEncrypt(int[] v, int[] k, int length, int tea_sum) {
		int i_0, i_1;
	    int[] v_tmp = {0, 0};
	    long cipertext = 0x0000000000000000L;
	    long pre_plaintext = 0x0000000000000000L;
	    long plaintext = 0x0000000000000000L;
	    long encrypt_text = 0x0000000000000000L;
	    long and_flag = 0x00000000ffffffffL;
	    length = length / 2;

	    pre_plaintext |= and_flag & v[0];
	    pre_plaintext <<= 32;
	    pre_plaintext |= v[1];
	    v_tmp[0] = v[0];
	    v_tmp[1] = v[1];
	    teaEncrypt(v_tmp, k, tea_sum);
	    v[0] = v_tmp[0];
	    v[1] = v_tmp[1];
	    cipertext |= and_flag & v[0];
	    cipertext <<= 32;
	    cipertext |= and_flag & v[1];
	    for (int i = 1; i<length; i++){
	        i_0 = i*2;
	        i_1 = i_0 + 1;
	        plaintext |= and_flag & v[i_0];
	        plaintext <<= 32;
	        plaintext |= and_flag & v[i_1];
	        plaintext ^= cipertext;
	        v_tmp[0] = (int)((plaintext >>> 32) & 0xffffffff);
	        v_tmp[1] = (int)(plaintext & 0xffffffff);
	        teaEncrypt(v_tmp, k, tea_sum);
	        v[i_0] = v_tmp[0];
	        v[i_1] = v_tmp[1];
	        encrypt_text |= and_flag & v[i_0];
	        encrypt_text <<= 32;
	        encrypt_text |= and_flag & v[i_1];
	        encrypt_text ^= pre_plaintext;
	        v[i_0] = (int)((encrypt_text >>> 32) & 0xffffffff);
	        v[i_1] = (int)(encrypt_text & 0xffffffff);
	        cipertext = encrypt_text;
	        pre_plaintext = plaintext;
	        plaintext = 0x0000000000000000L;
	        encrypt_text = 0x0000000000000000L;
	    }
	}
	
	public static int teaStrDecrypt(int[] v, int[] k, int length, int tea_sum) {
		int i_0, i_1;
	    int pos = 0;
	    int[] v_tmp = {0, 0};
	    long cipertext = 0x0000000000000000L;
	    long cipertext_tmp = 0x0000000000000000L;
	    long pre_plaintext = 0x0000000000000000L;
	    long plaintext = 0x0000000000000000L;
	    long encrypt_text = 0x0000000000000000L;
	    long and_flag = 0x00000000ffffffffL;
	    length = length / 2;

	    cipertext |= and_flag & v[0];
	    cipertext <<= 32;
	    cipertext |= and_flag & v[1];
	    v_tmp[0] = v[0];
	    v_tmp[1] = v[1];
	    teaDecrypt(v_tmp, k, tea_sum);
	    v[0] = v_tmp[0];
	    v[1] = v_tmp[1];
	    pos = v[0];
	    pre_plaintext |= and_flag & v[0];
	    pre_plaintext <<= 32;
	    pre_plaintext |= and_flag & v[1];
	    for (int i = 1; i<length; i++){
	        i_0 = i*2;
	        i_1 = i_0 + 1;
	        cipertext_tmp |= and_flag & v[i_0];
	        cipertext_tmp <<= 32;
	        cipertext_tmp |= and_flag & v[i_1];
	        encrypt_text |= and_flag & v[i_0];
	        encrypt_text <<= 32;
	        encrypt_text |= and_flag & v[i_1];
	        encrypt_text ^= pre_plaintext;
	        v[i_0] = (int)((encrypt_text >>> 32) & 0xffffffff);
	        v[i_1] = (int)(encrypt_text & 0xffffffff);
	        v_tmp[0] = v[i_0];
	        v_tmp[1] = v[i_1];
	        teaDecrypt(v_tmp, k, tea_sum);
	        v[i_0] = v_tmp[0];
	        v[i_1] = v_tmp[1];
	        plaintext |= and_flag & v[i_0];
	        plaintext <<= 32;
	        plaintext |= and_flag & v[i_1];
	        plaintext ^= cipertext;
	        v[i_0] = (int)((plaintext >>> 32) & 0xffffffff);
	        v[i_1] = (int)(plaintext & 0xffffffff);
	        pre_plaintext = plaintext ^ cipertext;
	        cipertext = cipertext_tmp;
	        cipertext_tmp = 0x0000000000000000L;
	        plaintext = 0x0000000000000000L;
	        encrypt_text = 0x0000000000000000L;
	        
	    }
	    return pos;
	}
	
	public static byte[] strEncrypt(byte[] v, byte[] k) {
		Random random = new Random();
		byte[] result = {};
		String end_char = "\0";
		int fill_n_or = 0xf8;
		int v_length = 0;
		int fill_n = 0;
		int[] k_c = {0, 0, 0, 0};

		k_c = byteArrayToIntArray(k);
		try {
			
			v_length = v.length;
			fill_n = (8 - (v_length + 2)) % 8;
			if (fill_n < 0) {
				fill_n = 8 + fill_n + 2;
			} else {
				fill_n += 2;
			}
			
			byte[] fill_s = new byte[fill_n];
			
			for(int i = 0; i < fill_n; i++) {
				// fill_s[i] = (byte)random.nextInt(0xff);
				// fill_s[i] = (byte)(random.nextInt(0x7f));
				fill_s[i] = (byte)0x70;
			}
			byte[] v_after_fill = appendData((byte)((fill_n - 2)|fill_n_or), fill_s);
			v_after_fill = appendData(v_after_fill, v);
			v_after_fill = appendData(v_after_fill, repeat(end_char, 7).getBytes());

			int[] v_int_array = byteArrayToIntArray(v_after_fill);
			
			teaStrEncrypt(v_int_array, k_c, v_int_array.length, 32);
			
			result = intArrayToByteArray(v_int_array);
		} catch (Exception e) {
			LOG.warning("strEncrypt failed!");
		}
		return result;
	}
	
	public static byte[] strEncrypt(byte[] v, byte[] k, int iterations) {
		Random random = new Random();
		byte[] result = {};
		String end_char = "\0";
		int fill_n_or = 0xf8;
		int v_length = 0;
		int fill_n = 0;
		int[] k_c = {0, 0, 0, 0};

		k_c = byteArrayToIntArray(k);
		try {
			
			v_length = v.length;
			fill_n = (8 - (v_length + 2)) % 8;
			if (fill_n < 0) {
				fill_n = 8 + fill_n + 2;
			} else {
				fill_n += 2;
			}
			
			byte[] fill_s = new byte[fill_n];
			
			for(int i = 0; i < fill_n; i++) {
				// fill_s[i] = (byte)random.nextInt(0xff);
				fill_s[i] = (byte)(random.nextInt(0x7f));
				// LOG.fine(String.format("random: %08x", fill_s[i]));
			}
			byte[] v_after_fill = appendData((byte)((fill_n - 2)|fill_n_or), fill_s);
			v_after_fill = appendData(v_after_fill, v);
			v_after_fill = appendData(v_after_fill, repeat(end_char, 7).getBytes());

			int[] v_int_array = byteArrayToIntArray(v_after_fill);
			
			teaStrEncrypt(v_int_array, k_c, v_int_array.length, iterations);
			
			result = intArrayToByteArray(v_int_array);
		} catch (Exception e) {
			LOG.warning("strEncrypt failed!");
		}
		return result;
	}
	
	public static byte[] strDecrypt(byte[] v, byte[] k) {
		byte[] result = new byte[]{};
		int pos = 0;
		int[] k_c = {0, 0, 0, 0};
		
		k_c = byteArrayToIntArray(k);
		try {
			int[] v_int_array = byteArrayToIntArray(v);
			
			pos = teaStrDecrypt(v_int_array, k_c, v_int_array.length, 32);
			
			byte[] r_byte = intArrayToByteArray(v_int_array);
			byte[] pos_byte = intToByteArray(pos);
			pos = (int)(pos_byte[0] & 0x07) + 2;
			if (!(new String(Arrays.copyOfRange(r_byte, r_byte.length - 7, r_byte.length)).equals("\0\0\0\0\0\0\0"))) {
				result = new byte[]{};
			} else {
				result = Arrays.copyOfRange(r_byte, pos + 1, r_byte.length - 7);
			}
		} catch (Exception e) {
			LOG.warning("strDecrypt failed!");
		}
		return result;
	}
	
	public static byte[] strDecrypt(byte[] v, byte[] k, int iterations) {
		byte[] result = new byte[]{};
		int pos = 0;
		int[] k_c = {0, 0, 0, 0};
		
		k_c = byteArrayToIntArray(k);
		try {
			int[] v_int_array = byteArrayToIntArray(v);
			
			pos = teaStrDecrypt(v_int_array, k_c, v_int_array.length, iterations);
			
			byte[] r_byte = intArrayToByteArray(v_int_array);
			pos = (int)(r_byte[0] & 0x07) + 2;
			if (!(new String(Arrays.copyOfRange(r_byte, r_byte.length - 7, r_byte.length)).equals("\0\0\0\0\0\0\0"))) {
				result = new byte[]{};
			} else {
				result = Arrays.copyOfRange(r_byte, pos + 1, r_byte.length - 7);
			}
		} catch (Exception e) {
			LOG.warning("strDecrypt failed!");
		}
		return result;
	}
	
	public static void main(String []args) {
		
	}
}
