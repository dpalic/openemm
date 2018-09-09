/*********************************************************************************
 * The contents of this file are subject to the OpenEMM Public License Version 1.1
 * ("License"); You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.agnitas.org/openemm.
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied.  See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Original Code is OpenEMM.
 * The Initial Developer of the Original Code is AGNITAS AG. Portions created by
 * AGNITAS AG are Copyright (C) 2006 AGNITAS AG. All Rights Reserved.
 *
 * All copies of the Covered Code must include on each user interface screen,
 * visible to all users at all times
 *    (a) the OpenEMM logo in the upper left corner and
 *    (b) the OpenEMM copyright notice at the very bottom center
 * See full license, exhibit B for requirements.
 ********************************************************************************/

package org.agnitas.util;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.log4j.Logger;

import com.jcraft.jsch.SftpException;

/**
 * The Class FtpHelper.
 * It uses FTP passive mode by default
 */
public class FtpHelper {
	
	/** The Constant logger. */
	@SuppressWarnings("unused")
	private static final transient Logger logger = Logger.getLogger(FtpHelper.class);
	
	/** The host. */
	private String host = null;
	
	/** The user. */
	private String user = null;
	
	/** The password. */
	private String password = null;
	
	/** The port. */
	private int port = 21; // Default port for FTP is 21
	
	/**
	 * usePassiveMode
	 */
	private boolean usePassiveMode = true;
	
	/**
	 * Currently connected client
	 */
	private FTPClient ftpClient = null;
	
	/**
	 * Instantiates a new ftp helper.
	 * Default port for FTP is 21.
	 *
	 * @param host the host
	 * @param user the user
	 * @param password the password
	 */
	public FtpHelper(String host, String user, String password) {
		this.host = host;
		this.user = user;
		this.password = password;
	}
	
	/**
	 * Instantiates a new ftp helper.
	 *
	 * @param host the host
	 * @param user the user
	 * @param password the password
	 * @param port the port
	 */
	public FtpHelper(String host, String user, String password, int port) {
		this.host = host;
		this.user = user;
		this.password = password;
		this.port = port;
	}

	/**
	 * Instantiates a new ftp helper.
	 * Default port for FTP is 21
	 *
	 * @param fileServerAndAuthConfigString like "ftp://[username[:password]@]server[:port]"
	 */
	public FtpHelper(String fileServerAndAuthConfigString) throws Exception {
		if (fileServerAndAuthConfigString.toLowerCase().startsWith("sftp://")) {
			throw new Exception("Invalid protocol for FtpHelper");
		} else if (fileServerAndAuthConfigString.toLowerCase().startsWith("ftp://")) {
			fileServerAndAuthConfigString = fileServerAndAuthConfigString.substring(6);
		}
		
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
	 * Connect.
	 *
	 * @throws Exception the exception
	 */
	public void connect() throws Exception {
		try {
			ftpClient = new FTPClient();
			ftpClient.connect(host, port);
			if (usePassiveMode) {
				ftpClient.enterLocalPassiveMode();
			}
			if (!ftpClient.login(user, password)) {
				ftpClient.logout();
				throw new Exception("Authentication failed");
            }
		} catch (Exception e) {
			close();
			throw new Exception("Connection failed");
		}
	}

	/**
	 * Cd.
	 *
	 * @param path the path
	 * @throws SftpException the sftp exception
	 */
	public void cd(String path) throws Exception {
		checkForConnection();
		ftpClient.changeWorkingDirectory(path);
	}

	/**
	 * Ls.
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<String> ls(String path) throws Exception {
		checkForConnection();
		return Arrays.asList(ftpClient.listNames(path));
	}
	
	/**
	 * Check if file exists on ftp server
	 * 
	 * @param filePath
	 * @return
	 * @throws Exception on invalid directory
	 */
	public boolean exists(String filePath) throws Exception {
		checkForConnection();
		return ftpClient.listNames(filePath) != null;
	}

	/**
	 * Put a file on the server.
	 *
	 * @param in the in
	 * @param dst the dst
	 */
	public void put(InputStream inputStream, String destination) throws Exception {
		checkForConnection();
		ftpClient.storeFile(destination, inputStream);
	}

	/**
	 * Gets the file.
	 *
	 * @param name the name
	 * @return the input stream
	 */
	public InputStream get(String name) throws Exception {
		checkForConnection();
		InputStream inputStream = ftpClient.retrieveFileStream(name);
	    int returnCode = ftpClient.getReplyCode();
	    if (inputStream != null && returnCode != 550) {
	    	return inputStream;
	    } else {
	    	throw new Exception("File not found");
	    }
	}
	
	private void checkForConnection() throws Exception {
		if (ftpClient == null) {
			throw new Exception("FtpHelper is not connected");
		} else if (!ftpClient.isConnected()) {
			ftpClient = null;
			throw new Exception("FtpHelper is not connected anymore");
		}
	}

	/**
	 * Close connection
	 */
	public void close() {
		if (ftpClient != null) {
			if (ftpClient.isConnected()) {
				try {
					ftpClient.disconnect();
				} catch (Exception f) {
					// do nothing
				}
			}
			ftpClient = null;
		}
	}
}
