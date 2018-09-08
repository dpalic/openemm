/*********************************************************************************
 * The contents of this file are subject to the Common Public Attribution
 * License Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.openemm.org/cpal1.html. The License is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Original Developer is the Initial Developer.
 * The Initial Developer of the Original Code is AGNITAS AG. All portions of
 * the code written by AGNITAS AG are Copyright (c) 2014 AGNITAS AG. All Rights
 * Reserved.
 *
 * Contributor(s): AGNITAS AG.
 ********************************************************************************/
package org.agnitas.util;

import java.io.File;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;

/**
 * The Class SFtpHelper.
 *
 * @author Andreas Rehak
 */
public class SFtpHelper {
	
	/** The Constant logger. */
	private static final transient Logger logger = Logger.getLogger(SFtpHelper.class);
	
	/** The host. */
	private String host = null;
	
	/** The user. */
	private String user = null;
	
	/** The password. */
	private String password = null;
	
	/** The port. */
	private int port = 22; // Default port for SFTP is 22
	
	private boolean allowUnknownHostKeys = false;
	
	/** The jsch. */
	private JSch jsch = new JSch();
	
	/** The session. */
	private Session session = null;
	
	/** The channel. */
	private ChannelSftp channel = null;
	
	/**
	 * Instantiates a new sftp helper.
	 * Default port for SFTP is 22.
	 *
	 * @param host the host
	 * @param user the user
	 * @param password the password
	 */
	public SFtpHelper(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Instantiates a new sftp helper.
	 *
	 * @param host the host
	 * @param user the user
	 * @param password the password
	 * @param port the port
	 */
	public SFtpHelper(String host, String user, String password, int port) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	/**
	 * Instantiates a new sftp helper.
	 * Default port for SFTP is 22
	 *
	 * @param fileServerAndAuthConfigString like "[username[:password]@]server[:port]"
	 */
	public SFtpHelper(String fileServerAndAuthConfigString) {
		if (fileServerAndAuthConfigString.contains("@")) {
			String[] parts = fileServerAndAuthConfigString.split("@");
			String authPart = parts[0];
			String serverPart = parts[1];
			
			if (authPart.contains(":")) {
				user = authPart.substring(0, authPart.indexOf(":"));
				password = authPart.substring(authPart.indexOf(":") + 1);
			} else {
				user = authPart;
			}
			
			if (serverPart.contains(":")) {
				host = serverPart.substring(0, serverPart.indexOf(":"));
				port = Integer.parseInt(serverPart.substring(serverPart.indexOf(":") + 1));
			} else {
				host = serverPart;
			}
		} else {
			host = fileServerAndAuthConfigString;
		}
	}
	
	/**
	 * Override the known_hosts file
	 * 
	 * @param allowUnknownHostKeys
	 */
	public void setAllowUnknownHostKeys(boolean allowUnknownHostKeys) {
		this.allowUnknownHostKeys = allowUnknownHostKeys;
	}

	/**
	 * Connect.
	 *
	 * @throws Exception the exception
	 */
	public void connect() throws Exception {
		if (password == null) {
			String home = System.getProperty("user.home");
			if (new File(home + "/.ssh/id_dsa").exists()) {
				jsch.addIdentity(home + "/.ssh/id_dsa");
			} else if (new File(home + "/.ssh/id_rsa").exists()) {
				jsch.addIdentity(home + "/.ssh/id_rsa");
			}
		}

		session = jsch.getSession(user, host, port);
		
		Properties config = new Properties(); 
		config.put("StrictHostKeyChecking", allowUnknownHostKeys ? "no" : "yes");
		session.setConfig(config);
		
		session.setUserInfo(new JSchUserInfo(password));
		
		session.connect();

		Channel chan = session.openChannel("sftp");
		chan.connect();
		channel = (ChannelSftp) chan;
	}

	/**
	 * Cd.
	 *
	 * @param path the path
	 * @throws SftpException the sftp exception
	 */
	public void cd(String path) throws SftpException {
		channel.cd(path);
	}

	/**
	 * Mkdir.
	 *
	 * @param path the path
	 * @throws SftpException the sftp exception
	 */
	public void mkdir(String path) throws SftpException {
		channel.mkdir(path);
	}

	/**
	 * Put.
	 *
	 * @param src the src
	 * @param dst the dst
	 * @param mode the mode
	 * @throws SftpException the sftp exception
	 */
	public void put(String src, String dst, int mode) throws SftpException {
		channel.put(src, dst, mode);
	}

	/**
	 * Put.
	 *
	 * @param in the in
	 * @param dst the dst
	 * @param mode the mode
	 * @throws SftpException the sftp exception
	 */
	public void put(InputStream in, String dst, int mode) throws SftpException {
		channel.put(in, dst, mode);
	}

	/**
	 * Gets the.
	 *
	 * @param name the name
	 * @return the input stream
	 * @throws SftpException the sftp exception
	 */
	public InputStream get(String name) throws SftpException {
		return channel.get(name);
	}

	/**
	 * Gets the mofidy date.
	 *
	 * @param name the name
	 * @return the mofidy date
	 * @throws SftpException the sftp exception
	 * @throws ParseException the parse exception
	 */
	public Date getModifyDate(String name) throws SftpException, ParseException {
		SftpATTRS attrs = channel.lstat(name);
		SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
		return format.parse(attrs.getMtimeString());
	}

	/**
	 * Ls.
	 *
	 * @param path the path
	 * @return the vector
	 * @throws SftpException the sftp exception
	 */
	@SuppressWarnings("unchecked")
	public Vector<LsEntry> ls(String path) throws SftpException {
		channel.cd(path);
		return channel.ls(".");
	}

	/**
	 * Close.
	 */
	public void close() {
		if (channel != null) {
			try {
				channel.quit();
			} catch (Exception e) {
				logger.error("Cannot close SFtpHelper channel: " + e.getMessage(), e);
			}
		}
		if (session != null) {
			try {
				session.disconnect();
			} catch (Exception e) {
				logger.error("Cannot close SFtpHelper session: " + e.getMessage(), e);
			}
		}
	}
	
	/**
	 * Check if directory exists on sftp server
	 * 
	 * @param directoryPath
	 * @return
	 */
	public boolean directoryExists(String directoryPath) {
		try {
			if (StringUtils.isNotBlank(directoryPath)) {
				ls(directoryPath);
				return true;
			} else {
				throw new Exception("Invalid directory");
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Check if file exists on sftp server
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception on invalid directory
	 */
	public boolean fileExists(String filePath) throws Exception {
		String directoryPath = "";
		String filename = filePath;
		
		int lastSlash = filename.lastIndexOf("/");
		if (lastSlash > 0) {
			directoryPath = filename.substring(0, lastSlash);
			filename = filename.substring(lastSlash + 1);
		}
		
		List<LsEntry> directoryEntries;
		try {
			if (StringUtils.isNotBlank(directoryPath)) {
				directoryEntries = ls(directoryPath);
			} else {
				directoryEntries = ls("/");
			}
		} catch (Exception e) {
			throw new Exception("Cannot read directory", e);
		}
		
		boolean fileFound = false;
		if (directoryEntries != null) {
			for (LsEntry lsEntry : directoryEntries) {
				if (lsEntry.getFilename().equals(filename)) {
					fileFound = true;
					break;
				}
			}
		}
		return fileFound;
	}
}
