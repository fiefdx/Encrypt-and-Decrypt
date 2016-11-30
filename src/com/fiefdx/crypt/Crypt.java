package com.fiefdx.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.math.BigInteger;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fiefdx.logger.SimpleLogger;
import com.fiefdx.tea.Tea;

public class Crypt {
	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	static int CRYPT_BLOCK = 1024 * 8;
	static String FILE_HEADER = "crypt";
	static String CRYPT_FILE_TYPE = ".crypt";
	
	private File file;
	private String file_name;
	private String file_path;
	private long file_size;
	private int fname_pos = 0;
	private int fname_len = 0;
	private int file_pos = 0;
	private long file_len = 0;
	private int percent = 0;
	private String crypt_file_name;
	private String crypt_file_path = "";
	private File target, out;
	private boolean hard = false;
	
	public Crypt(File file, File out) {
		this.file = file;
		this.out = out;
		file_name = file.getName();
		file_path = file.getAbsolutePath();
		file_size = file.length();
	}
	
	public Crypt(File file, File out, boolean hard) {
		this.file = file;
		this.out = out;
		this.hard = hard;
		file_name = file.getName();
		file_path = file.getAbsolutePath();
		file_size = file.length();
		
	}
	
	public static int getEncryptLength(int length) {
		int fill_n, result;
		fill_n = (8 - (length + 2)) % 8;
		if (fill_n < 0) {
			fill_n = 8 + fill_n + 2;
		} else {
			fill_n += 2;
		}
		result = 1 + length + fill_n + 7;
		return result;
	}
	
	public static byte[] readBytes(RandomAccessFile in, int length) throws IOException {
		byte[] tmp = new byte[length];
		byte[] result;
		byte[] c = new byte[1]; 
		int i = 0;
		while ((i < length) && ((in.read(c)) != -1)) {
            tmp[i] = c[0];
            i++;
        }
		if (i < length) {
			result = Arrays.copyOfRange(tmp, 0, i);
		} else {
			result = tmp;
		}
		return result;
	}
	
	public class Result {
		public boolean flag = false;
		public String path = "";
		
		public Result(boolean flag, String path) {
			this.flag = flag;
			this.path = path;
		}
	}
	
	public Result encryptFile(String k, CallBack callback) throws NoSuchAlgorithmException, IOException {
		boolean result = false;
		String fname_hash;
		fname_hash = Tea.sha1sum(file_name);
		String timestamp = String.format("%d.0", new Date().getTime()/1000);
		String tmp = fname_hash + String.format("%d", file_size) + timestamp;
		crypt_file_name = Tea.sha1sum(tmp) + CRYPT_FILE_TYPE;
		crypt_file_path = Paths.get(out.getAbsolutePath(), crypt_file_name).toString();
		LOG.fine(String.format("crypt_file_name: %s", crypt_file_name));
		LOG.fine(String.format("crypt_file_path: %s", crypt_file_path));
		target = new File(crypt_file_path);
		String crypt_key = "";
		if (k != "") {
			crypt_key = Tea.md5twice(k);
		}
		if (target.exists()) {
			LOG.warning(String.format("Encrypt file path[%s] exists!", crypt_file_path));
		} else if (crypt_key != "") {
			if (callback != null) callback.callBack(0, String.format("Encrypt: %d%%", 0));
			byte[] header_fname;
			if (!hard) {
				header_fname = Tea.strEncrypt(file_name.getBytes(), Tea.hexToByteArray(crypt_key));
			} else {
				header_fname = Tea.strEncrypt(file_name.getBytes(), Tea.hexToByteArray(crypt_key), 64);
			}
			fname_pos = 25;
			fname_len = header_fname.length;
			file_pos = fname_pos + fname_len;
			RandomAccessFile file_in;
			RandomAccessFile target_out;
			try {
				target_out = new RandomAccessFile(target, "rw");
				target_out.write(FILE_HEADER.getBytes());
				target_out.writeInt(fname_pos);
				target_out.writeInt(fname_len);
				target_out.writeInt(file_pos);
				target_out.writeLong(file_len);
				target_out.write(header_fname);
				long crypt_size = 0;
				file_in = new RandomAccessFile(file, "r");
				while (true) {
					byte[] buf = readBytes(file_in, CRYPT_BLOCK);
					if (buf.length == 0) {
						file_in.close();
						break;
					}
					byte[] crypt_buf;
					if (!hard) {
						crypt_buf = Tea.strEncrypt(buf, Tea.hexToByteArray(crypt_key));
					} else {
						crypt_buf = Tea.strEncrypt(buf, Tea.hexToByteArray(crypt_key), 64);
					}
					file_len += crypt_buf.length;
					target_out.write(crypt_buf);
					crypt_size += CRYPT_BLOCK;
					if (callback != null) {
						int p = (int)(crypt_size * 100 / file_size);
						if (p >= 100) p = 100;
						if (p > percent) {
							percent = p;
							LOG.fine(String.format("Encrypt: %d", percent));
							callback.callBack(percent, String.format("Encrypt: %d%%", percent));
						}
					}
				}
				LOG.fine(String.format("file_len array: %s", Arrays.toString(Tea.longToByteArray(file_len))));
				target_out.seek(17);
				target_out.writeLong(file_len);
				LOG.fine(String.format("file pos: %d, file len: %d", file_pos, file_len));
				target_out.close();
				if (callback != null) callback.callBack(100, String.format("Encrypt: %d%%", 100));
				result = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new Result(result, crypt_file_path);
	}
	
	public Result decryptFile(String k, CallBack callback) throws IOException, NoSuchAlgorithmException {
		boolean result = false;
		String crypt_key = "";
		if (k != "") {
			crypt_key = Tea.md5twice(k);
			LOG.fine(String.format("crypt_key: %s", crypt_key));
		}
		RandomAccessFile file_in;
		if (crypt_key != "") {
			file_in = new RandomAccessFile(file, "r");
			byte[] file_header_byte = new byte[5];
			file_in.readFully(file_header_byte);
			String file_header = new String(file_header_byte);
			LOG.fine(String.format("file_header: %s, %s, %b", file_header, FILE_HEADER, file_header == FILE_HEADER));
			if (file_header.equals(FILE_HEADER)) {
				fname_pos = file_in.readInt();
				fname_len = file_in.readInt();
				file_pos = file_in.readInt();
				file_len = file_in.readLong();
				file_size = file_len;
				LOG.fine(String.format("fname_pos: %d, fname_len: %d, file_pos: %d, file_len: %d", 
						               fname_pos, fname_len, file_pos, file_len));
				file_in.seek(fname_pos);
				byte[] file_name_byte = new byte[fname_len];
				file_in.readFully(file_name_byte);
				LOG.fine(String.format("file_name_byte: %s", Tea.bytesToHex(file_name_byte)));
				byte[] file_name_decrypt_byte;
				if (!hard) {
					file_name_decrypt_byte = Tea.strDecrypt(file_name_byte, Tea.hexToByteArray(crypt_key));
				} else {
					file_name_decrypt_byte = Tea.strDecrypt(file_name_byte, Tea.hexToByteArray(crypt_key), 64);
				}
				crypt_file_name = new String(file_name_decrypt_byte);
				crypt_file_path = Paths.get(out.getAbsolutePath(), crypt_file_name).toString();
				LOG.fine(String.format("crypt_file_name: %s", crypt_file_name));
				LOG.fine(String.format("crypt_file_path: %s", crypt_file_path));
				target = new File(crypt_file_path);
				if (target.exists()) {
					LOG.warning(String.format("Decrypt file path[%s] exists!", crypt_file_path));
				} else {
					if (callback != null) callback.callBack(0, String.format("Decrypt: %d%%", 0));
					RandomAccessFile target_out;
					try {
						target_out = new RandomAccessFile(target, "rw");
						int crypt_length = getEncryptLength(CRYPT_BLOCK);
						long crypt_size = 0;
						while (true) {
							byte[] buf;
							if (file_len < crypt_length) {
								buf = readBytes(file_in, (int)file_len);
							} else {
								buf = readBytes(file_in, crypt_length);
							}
							if (buf.length == 0) {
								file_in.close();
								break;
							}
							file_len -= buf.length;
							byte[] crypt_buf;
							if (!hard) {
								crypt_buf = Tea.strDecrypt(buf, Tea.hexToByteArray(crypt_key));
							} else {
								crypt_buf = Tea.strDecrypt(buf, Tea.hexToByteArray(crypt_key), 64);
							}
							target_out.write(crypt_buf);
							crypt_size += crypt_length;
							if (callback != null) {
								int p = (int)(crypt_size * 100 / file_size);
								if (p >= 100) p = 100;
								if (p > percent) {
									percent = p;
									callback.callBack(percent, String.format("Decrypt: %d%%", percent));
								}
							}
							if (file_len <= 0) break;
						}
						target_out.close();
						if (callback != null) callback.callBack(100, String.format("Decrypt: %d%%", 100));
						result = true;
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				file_in.close();
			} else {
				LOG.warning(String.format("The file[%s] is not a crypt file!", file_path));
			}
		}
		return new Result(result, crypt_file_path);
	}
	
	public String encryptFileName(byte[] k) {
		return "";
	}
	
	public String decryptFileName(String k) throws NoSuchAlgorithmException, IOException {
		String result = "";
		String crypt_key = "";
		if (k != "") {
			crypt_key = Tea.md5twice(k);
			LOG.fine(String.format("crypt_key: %s", crypt_key));
		}
		RandomAccessFile file_in;
		if (crypt_key != "") {
			file_in = new RandomAccessFile(file, "r");
			byte[] file_header_byte = new byte[5];
			file_in.readFully(file_header_byte);
			String file_header = new String(file_header_byte);
			LOG.fine(String.format("file_header: %s, %s, %b", file_header, FILE_HEADER, file_header == FILE_HEADER));
			if (file_header.equals(FILE_HEADER)) {
				fname_pos = file_in.readInt();
				fname_len = file_in.readInt();
				file_pos = file_in.readInt();
				file_len = file_in.readLong();
				LOG.fine(String.format("fname_pos: %d, fname_len: %d, file_pos: %d, file_len: %d", 
						               fname_pos, fname_len, file_pos, file_len));
				file_in.seek(fname_pos);
				byte[] file_name_byte = new byte[fname_len];
				file_in.readFully(file_name_byte);
				LOG.fine(String.format("file_name_byte: %s", Tea.bytesToHex(file_name_byte)));
				byte[] file_name_decrypt_byte;
				if (!hard) {
					file_name_decrypt_byte = Tea.strDecrypt(file_name_byte, Tea.hexToByteArray(crypt_key));
				} else {
					file_name_decrypt_byte = Tea.strDecrypt(file_name_byte, Tea.hexToByteArray(crypt_key), 64);
				}
				result = new String(file_name_decrypt_byte);
				file_in.close();
			} else {
				LOG.warning(String.format("The file[%s] is not a crypt file!", file_path));
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s %4$s    %5$s%n");
		try {
			SimpleLogger.setup("test.log", 1024 * 1024 * 5, 5, true, true, Level.ALL);
			
//			File file = new File("/home/breeze/gobook.pdf");
//			Crypt crypt = new Crypt(file);
//			crypt.encryptFile("111111", null);
//			Crypt crypt2 = new Crypt(new File("/home/breeze/f534c5dbf5fea7980ce75f53ecc952213dc77712.crypt"));
//			String file_name = crypt2.decryptFileName("111111");
//			LOG.fine(String.format("file_name: %s", file_name));
			File file2 = new File("/home/breeze/f595ee17aa016b91926db7acb704f05559f1ae00.crypt");
			Crypt crypt3 = new Crypt(file2, new File("/home/breeze"));
			crypt3.decryptFile("111111", null);
			byte[] tmp = {0, 1, 2};
			byte[] result = Arrays.copyOfRange(tmp, 0, 0);
			LOG.fine(String.format("result: %s, %b", Arrays.toString(result), result.length == 0));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Problems with creating the log files");
		}
	}
}
