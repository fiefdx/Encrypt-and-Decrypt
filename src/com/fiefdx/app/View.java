package com.fiefdx.app;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.fiefdx.crypt.CallBack;
import com.fiefdx.crypt.Crypt;
import com.fiefdx.logger.SimpleLogger;

public class View extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final static Logger LOG = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private Timer timer;
	private JProgressBar pbar;
	private JTextField in, name, out;
	private JButton select, parse, change, encrypt, decrypt;
	private JCheckBox hard;
	private JPanel select_panel, name_panel, out_panel, operate_panel, bottom_panel, panel;
	private int processing = 0;
	private String processing_s = "";
	private File selected_file, out_path;
	private ImageIcon icon;
	
	View() {
		initUI();
	}
	
	private void initUI() {
		JComponent.setDefaultLocale(Locale.ENGLISH);
		icon = new ImageIcon("img/Lock-icon.png");
		
		timer = new Timer(50, new UpdateBarListener());
		
		panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        
        select_panel = new JPanel();
        select_panel.setLayout(new BoxLayout(select_panel, BoxLayout.X_AXIS));
        
        name_panel = new JPanel();
        name_panel.setLayout(new BoxLayout(name_panel, BoxLayout.X_AXIS));
        
        out_panel = new JPanel();
        out_panel.setLayout(new BoxLayout(out_panel, BoxLayout.X_AXIS));
        
        operate_panel = new JPanel();
        operate_panel.setLayout(new BoxLayout(operate_panel, BoxLayout.X_AXIS));
        
        bottom_panel = new JPanel();
        bottom_panel.setLayout(new BoxLayout(bottom_panel, BoxLayout.X_AXIS));
        
        in = new JTextField("Select A File");
        select = new JButton("Select");
        select.setPreferredSize(new Dimension(90, 24));
        select.addActionListener(new SelectClickAction());
        name = new JTextField("Parse The Original File Name");
        parse = new JButton("Parse");
        parse.setPreferredSize(new Dimension(90, 24));
        parse.addActionListener(new ParseClickAction());
        out = new JTextField("Encrypt OR Decrypt To ...");
        change = new JButton("Change");
        change.setPreferredSize(new Dimension(90, 24));
        change.addActionListener(new ChangeClickAction());
        encrypt = new JButton("Encrypt");
        encrypt.setPreferredSize(new Dimension(90, 24));
        encrypt.addActionListener(new EncryptClickAction());
        decrypt = new JButton("Decrypt");
        decrypt.setPreferredSize(new Dimension(90, 24));
        decrypt.addActionListener(new DecryptClickAction());
        hard = new JCheckBox("Hard Mode", false);
        pbar = new JProgressBar(0, 100);
        pbar.setStringPainted(true);
        
        select_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        select_panel.add(in);
        select_panel.add(Box.createRigidArea(new Dimension(1, 0)));
        select_panel.add(select);
        select_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        
        name_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        name_panel.add(name);
        name_panel.add(Box.createRigidArea(new Dimension(1, 0)));
        name_panel.add(parse);
        name_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        
        out_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        out_panel.add(out);
        out_panel.add(Box.createRigidArea(new Dimension(1, 0)));
        out_panel.add(change);
        out_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        
        operate_panel.add(Box.createRigidArea(new Dimension(88, 0)));
        operate_panel.add(hard);
        operate_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        operate_panel.add(encrypt);
        operate_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        operate_panel.add(decrypt);
        operate_panel.add(Box.createRigidArea(new Dimension(92, 0)));
        
        bottom_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        bottom_panel.add(pbar);
        bottom_panel.add(Box.createRigidArea(new Dimension(2, 0)));
        
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(select_panel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(name_panel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(out_panel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(operate_panel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        panel.add(bottom_panel);
        panel.add(Box.createRigidArea(new Dimension(0, 2)));
        add(panel);

        //Display the window.
        pack();
        setTitle("Encrypt & Decrypt - (v0.0.2)");
        setIconImage(icon.getImage());
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
	}
	
	private class UpdateProcessing implements CallBack {
		@Override
		public void callBack(int n, String s) {
			processing = n;
			processing_s = s;
			try {
				Thread.sleep(10);  // milliseconds
            } catch (InterruptedException ex) {}
		}
		
	}
	
	private class UpdateBarListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int val = pbar.getValue();
			if (val >= 100) {
				timer.stop();
				in.setEnabled(true);
				name.setEnabled(true);
				out.setEnabled(true);
				change.setEnabled(true);
				hard.setEnabled(true);
				select.setEnabled(true);
				parse.setEnabled(true);
				encrypt.setEnabled(true);
				decrypt.setEnabled(true);
				return;
			}
			if (processing == -1) {
				JOptionPane.showMessageDialog(panel,
						                      "There is a file with the same name, so ignore the decryption!",
						                      "Warning",
						                      JOptionPane.WARNING_MESSAGE);
				processing = 100;
				processing_s = String.format("Decrypt: %d%%", processing);
			}
			pbar.setValue(processing);
			pbar.setString(processing_s);
		}
	}
	
	private class SelectClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			//chooser.setDefaultLocale(Locale.ENGLISH);
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			//chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			FileFilter filter = new FileNameExtensionFilter("Encrypted file (.crypt)", "crypt", "CRYPT");
			chooser.addChoosableFileFilter(filter);
			Action details = chooser.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			
			int ret = chooser.showDialog(panel, "Select");
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				selected_file = chooser.getSelectedFile();
				out_path = selected_file.getParentFile();
				in.setText(chooser.getName(selected_file));
				out.setText(out_path.getAbsolutePath());
			}
		}
	}
	
	private class ChangeClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser chooser = new JFileChooser();
			//chooser.setDefaultLocale(Locale.ENGLISH);
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			Action details = chooser.getActionMap().get("viewTypeDetails");
			details.actionPerformed(null);
			
			int ret = chooser.showDialog(panel, "Change");
			
			if (ret == JFileChooser.APPROVE_OPTION) {
				out_path = chooser.getSelectedFile();
				out.setText(out_path.getAbsolutePath());
			}
		}
	}
	
	private class ParseClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			if (selected_file != null) {
				final String s = (String)JOptionPane.showInputDialog(panel,
                        "Password:",
                        "Password Dialog",
                        JOptionPane.PLAIN_MESSAGE,
                        null,
                        null,
                        "");
				if ((s != null) && (s.length() > 0)) {
					boolean h = hard.isSelected();
					final Crypt crypt = new Crypt(selected_file, out_path, h);
					try {
						String original_name = crypt.decryptFileName(s);
						name.setText(original_name);
					} catch (NoSuchAlgorithmException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
	}
	
	private class EncryptClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			final String s = (String)JOptionPane.showInputDialog(panel,
					                                       "Password:",
					                                       "Password Dialog",
					                                       JOptionPane.PLAIN_MESSAGE,
					                                       null,
					                                       null,
					                                       "");
			if ((out_path != null) && (selected_file != null) && (s != null) && (s.length() > 0)) {
				pbar.setValue(0);
				pbar.setString("0%");
				in.setEnabled(false);
				name.setEnabled(false);
				out.setEnabled(false);
				change.setEnabled(false);
				hard.setEnabled(false);
				select.setEnabled(false);
				parse.setEnabled(false);
				encrypt.setEnabled(false);
				decrypt.setEnabled(false);
				if (!timer.isRunning()) {
					timer.start();
				}
				boolean h = hard.isSelected();
				final Crypt crypt = new Crypt(selected_file, out_path, h);
				Thread t = new Thread() {
					public void run() {
						try {
							Crypt.Result result = crypt.encryptFile(s, new UpdateProcessing());
							if (result.flag) {
								out.setText(result.path);
							}
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		}
	}
	
	private class DecryptClickAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			final String s = (String)JOptionPane.showInputDialog(panel,
                    "Password:",
                    "Password Dialog",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "");
			if ((out_path != null) && (selected_file != null) && (s != null) && (s.length() > 0)) {
				pbar.setValue(0);
				pbar.setString("0%");
				in.setEnabled(false);
				name.setEnabled(false);
				out.setEnabled(false);
				change.setEnabled(false);
				hard.setEnabled(false);
				select.setEnabled(false);
				parse.setEnabled(false);
				encrypt.setEnabled(false);
				decrypt.setEnabled(false);
				if (!timer.isRunning()) {
					timer.start();
				}
				boolean h = hard.isSelected();
				final Crypt crypt = new Crypt(selected_file, out_path, h);
				Thread t = new Thread() {
					public void run() {
						try {
							Crypt.Result result = crypt.decryptFile(s, new UpdateProcessing());
							if (result.flag) {
								out.setText(result.path);
							} else {
								processing = -1;
							}
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				t.start();
			}
		}
	}
	
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", 
                "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS.%1$tL %2$s %4$s    %5$s%n");
		
		try {
			SimpleLogger.setup("crypt.log", 1024 * 1024 * 2, 1, true, false, Level.INFO);
			
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					View v = new View();
					v.setVisible(true);
				}
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

