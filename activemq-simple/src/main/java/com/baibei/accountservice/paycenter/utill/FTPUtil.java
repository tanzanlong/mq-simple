package com.baibei.accountservice.paycenter.utill;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.pagehelper.util.StringUtil;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class FTPUtil {

	static final Logger logger = LoggerFactory.getLogger(FTPUtil.class);

	private static String encoding = System.getProperty("file.encoding");

	public static File getFTPFile(String ftpHost, int ftpPort,
			String ftpUserName, String ftpPassword, String fileName,
			String localPath,String remotePath, String protocol, boolean isMatchFileName) throws Exception {

		boolean flag = downFile(ftpHost, ftpPort, ftpUserName, ftpPassword, remotePath,fileName, localPath, protocol,isMatchFileName);

		if (!flag) {
			return null;
		}

		File file = new File(localPath + File.separator + fileName);

		return file;
	}

	public static boolean writeFileToFTP(String ftpHost, int ftpPort,
			String ftpUserName, String ftpPassword, String fileName,
			String fileContent, String writeTempFilePath,String remotePath, String protocol)
			throws Exception {
		boolean flag = false;

		// 对远程目录的处理
		String remoteFileName = fileName;
		if (fileName.contains("/")) {
			remoteFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		}
		// 先把文件写在本地。在上传到FTP上最后在删除
		boolean writeResult = writeLocal(remoteFileName, fileContent,
				writeTempFilePath);

		if (writeResult) {
			InputStream in = null;
			File file = null;
			if ("SFTP".equals(protocol)) {
				logger.info("开始上传文件到SFTP.");
				Session sshSession = null;
				ChannelSftp sftp = null;
				try {
					JSch jsch = new JSch();
					jsch.getSession(ftpUserName, ftpHost, ftpPort);
					sshSession = jsch.getSession(ftpUserName, ftpHost, ftpPort);
					sshSession.setPassword(ftpPassword);
					Properties sshConfig = new Properties();
					sshConfig.put("StrictHostKeyChecking", "no");
					sshSession.setConfig(sshConfig);
					sshSession.connect();
					Channel channel = sshSession.openChannel("sftp");
					channel.connect();

					sftp = (ChannelSftp) channel;

					file = new File(writeTempFilePath + fileName);
					in = new FileInputStream(file);
					sftp.put(in, remotePath+fileName);
					flag = true;
					logger.info("上传文件" + remoteFileName + "到SFTP成功!");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (SftpException e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (null != file) {
						file.delete();
					}
					if (sftp != null) {
						if (sftp.isConnected()) {
							sftp.disconnect();
						}
					}
					if (sshSession != null) {
						if (sshSession.isConnected()) {
							sshSession.disconnect();
						}
					}
				}
			} else {
				logger.info("开始上传文件到FTP.");
				FTPClient ftpClient = null;
				try {
					ftpClient = FTPUtil.getFTPClient(ftpHost, ftpPassword,
							ftpUserName, ftpPort);
					// 设置PassiveMode传输
					ftpClient.enterLocalPassiveMode();
					// 设置以二进制流的方式传输
					ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

					if (writeResult) {
						file = new File(writeTempFilePath + "/"
								+ remoteFileName);
						in = new FileInputStream(file);
						ftpClient.storeFile(remoteFileName, in);
						in.close();
						flag = true;
						logger.info("上传文件" + remoteFileName + "到FTP成功!");
						file.delete();
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					if (null != file) {
						file.delete();
					}
					try {
						ftpClient.disconnect();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			logger.info("写文件失败!");
		}

		return flag;
	}
	
	public static boolean uploadFileToFTP(String ftpHost, int ftpPort,
			String ftpUserName, String ftpPassword, String fileName,
			String writeTempFilePath,String remotePath, String protocol)
			throws Exception {
		boolean flag = false;

		// 对远程目录的处理
		String remoteFileName = fileName;
		if (fileName.contains("/")) {
			remoteFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		}

		InputStream in = null;
		File file = null;
		if ("SFTP".equals(protocol)) {
			logger.info("开始上传文件到SFTP.");
			Session sshSession = null;
			ChannelSftp sftp = null;
			try {
				JSch jsch = new JSch();
				jsch.getSession(ftpUserName, ftpHost, ftpPort);
				sshSession = jsch.getSession(ftpUserName, ftpHost, ftpPort);
				sshSession.setPassword(ftpPassword);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();

				sftp = (ChannelSftp) channel;

				file = new File(writeTempFilePath + fileName);
				in = new FileInputStream(file);
				sftp.put(in, remotePath+fileName);
				flag = true;
				logger.info("上传文件" + remoteFileName + "到SFTP成功!");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SftpException e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != file) {
					file.delete();
				}
				if (sftp != null) {
					if (sftp.isConnected()) {
						sftp.disconnect();
					}
				}
				if (sshSession != null) {
					if (sshSession.isConnected()) {
						sshSession.disconnect();
					}
				}
			}
		} else {
			logger.info("开始上传文件到FTP.");
			FTPClient ftpClient = null;
			try {
				ftpClient = FTPUtil.getFTPClient(ftpHost, ftpPassword,
						ftpUserName, ftpPort);
				// 设置PassiveMode传输
				ftpClient.enterLocalPassiveMode();
				// 设置以二进制流的方式传输
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

				file = new File(writeTempFilePath + "/" + remoteFileName);
				in = new FileInputStream(file);
				ftpClient.storeFile(remoteFileName, in);
				in.close();
				flag = true;
				logger.info("上传文件" + remoteFileName + "到FTP成功!");
				file.delete();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (null != file) {
					file.delete();
				}
				try {
					ftpClient.disconnect();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return flag;
	}
	
	public static FTPClient getFTPClient(String ftpHost, String ftpPassword,
			String ftpUserName, int ftpPort) {
		FTPClient ftpClient = null;
		try {
			ftpClient = new FTPClient();
			// 连接FTP服务器
			ftpClient.connect(ftpHost, ftpPort);
			// 登陆FTP服务器
			ftpClient.login(ftpUserName, ftpPassword);
			if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
				logger.info("未连接到FTP，用户名或密码错误。");
				ftpClient.disconnect();
			} else {
				logger.info("FTP连接成功。");
			}
		} catch (SocketException e) {
			e.printStackTrace();
			logger.error("FTP的IP地址可能错误，请正确配置。");
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("FTP的端口错误,请正确配置。");
		}
		return ftpClient;
	}

	public static boolean writeLocal(String fileName, String fileContext,
			String writeTempFielPath) {
		try {
			logger.info("开始写入本地文件");
			File f = new File(writeTempFielPath + "/" + fileName);
			if (!f.exists()) {
				if (!f.createNewFile()) {
					logger.error("文件不存在，创建失败!");
				}
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(f, true));
			bw.write(fileContext.replaceAll("\n", "\r\n"));
			bw.flush();
			bw.close();
			return true;
		} catch (Exception e) {
			logger.error("写文件失败");
			e.printStackTrace();
			return false;
		}
	}

	public static boolean downFile(String ftpHost, int port,
			String ftpUserName, String ftpPassword, String remotePath,
			String fileName, String localPath, String protocol, boolean isMatchFileName) throws JSchException
			{
		boolean result = false;
		if ("SFTP".equals(protocol)) {
			logger.info("连接sftp...");
			FileOutputStream fieloutput = null;
			Session sshSession = null;
			ChannelSftp sftp = null;
			try {
				JSch jsch = new JSch();
				jsch.getSession(ftpUserName, ftpHost, port);
				sshSession = jsch.getSession(ftpUserName, ftpHost, port);
				sshSession.setPassword(ftpPassword);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();

				sftp = (ChannelSftp) channel;

				File file = new File(localPath + fileName);
				
				Vector allFiles = sftp.ls(remotePath);
				
				for(int i = 0;i<allFiles.size();i++){
					ChannelSftp.LsEntry ls = (LsEntry) allFiles.get(i);
					
					if (ls.getFilename().equals(fileName)) {
						fieloutput = new FileOutputStream(file);
						sftp.get(remotePath + ls.getFilename(), fieloutput);
						result = true;
						logger.info("文件下载成功!文件名是:" + ls.getFilename());
						break;
					}
					if(isMatchFileName){
						if (-1 != ls.getFilename().indexOf((fileName))) {
							fieloutput = new FileOutputStream(file);
							sftp.get(remotePath + ls.getFilename(), fieloutput);
							result = true;
							logger.info("文件下载成功!文件名是:" + ls.getFilename());
							break;
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (SftpException e) {
				e.printStackTrace();
			} finally {
				if (null != fieloutput) {
					try {
						fieloutput.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (sftp != null) {
					if (sftp.isConnected()) {
						sftp.disconnect();
					}
				}
				if (sshSession != null) {
					if (sshSession.isConnected()) {
						sshSession.disconnect();
					}
				}
			}
		} else {
			logger.info("开始从FTP下载文件.");
			FTPClient ftpClient = null;
			try {
				ftpClient = getFTPClient(ftpHost, ftpPassword, ftpUserName,
						port);
				int reply;
				ftpClient.setControlEncoding(encoding);

				/*
				 * 为了上传和下载中文文件，有些地方建议使用以下两句代替 new
				 * String(remotePath.getBytes(encoding),"iso-8859-1")转码。
				 * 经过测试，通不过。
				 */
				// FTPClientConfig conf = new
				// FTPClientConfig(FTPClientConfig.SYST_NT);
				// conf.setServerLanguageCode("zh");

				ftpClient.connect(ftpHost, port);
				// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
				ftpClient.login(ftpUserName, ftpPassword);// 登录
				// 设置文件传输类型为二进制
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 获取ftp登录应答代码
				reply = ftpClient.getReplyCode();
				// 验证是否登陆成功
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					System.err.println("FTP server refused connection.");
					return result;
				}
				// 转移到FTP服务器目录至指定的目录下
				ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(encoding), "iso-8859-1"));
				// 获取文件列表
				FTPFile[] fs = ftpClient.listFiles();
				if (fs != null && fs.length > 0) {
					for (FTPFile ff : fs) {
						if (ff.getName().equals(fileName)) {
							File localFile = new File(localPath + "/"
									+ ff.getName());
							OutputStream is = new FileOutputStream(localFile);
							ftpClient.retrieveFile(ff.getName(), is);
							is.close();
							result = true;
							logger.info("文件下载成功!文件名是:" + fileName);
							break;
						}
						if(isMatchFileName){
							if (-1 != ff.getName().indexOf((fileName))) {
								File localFile = new File(localPath + "/"
										+ ff.getName());
								OutputStream is = new FileOutputStream(localFile);
								ftpClient.retrieveFile(ff.getName(), is);
								is.close();
								result = true;
								logger.info("文件下载成功!文件名是:" + ff.getName());
								break;
							}
						}
					}
				}

				ftpClient.logout();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException ioe) {
					}
				}
			}
		}
		return result;
	}
	
	public static boolean renameFile(String ftpHost, int port,
			String ftpUserName, String ftpPassword, String remotePath,
			String fileName, String protocol, boolean isMatchFileName,String newName) throws JSchException
			{
		boolean result = false;
		if ("SFTP".equals(protocol)) {
			logger.info("连接sftp...");
			Session sshSession = null;
			ChannelSftp sftp = null;
			try {
				JSch jsch = new JSch();
				jsch.getSession(ftpUserName, ftpHost, port);
				sshSession = jsch.getSession(ftpUserName, ftpHost, port);
				sshSession.setPassword(ftpPassword);
				Properties sshConfig = new Properties();
				sshConfig.put("StrictHostKeyChecking", "no");
				sshSession.setConfig(sshConfig);
				sshSession.connect();
				Channel channel = sshSession.openChannel("sftp");
				channel.connect();

				sftp = (ChannelSftp) channel;

				Vector allFiles = sftp.ls(remotePath);
				
				for(int i = 0;i<allFiles.size();i++){
					ChannelSftp.LsEntry ls = (LsEntry) allFiles.get(i);
					
					if (ls.getFilename().equals(fileName)) {
						sftp.rename(remotePath + ls.getFilename(), remotePath + newName);
						result = true;
						logger.info("文件重命名成功!新文件名是:" + newName);
						break;
					}
					if(isMatchFileName){
						if (-1 != ls.getFilename().indexOf((fileName))) {
							sftp.rename(remotePath + ls.getFilename(), remotePath + newName);
							result = true;
							logger.info("文件重命名成功!新文件名是:" + newName);
							break;
						}
					}
				}
			} catch (SftpException e) {
				e.printStackTrace();
			} finally {
				if (sftp != null) {
					if (sftp.isConnected()) {
						sftp.disconnect();
					}
				}
				if (sshSession != null) {
					if (sshSession.isConnected()) {
						sshSession.disconnect();
					}
				}
			}
		} else {
			logger.info("连接ftp...");
			FTPClient ftpClient = null;
			try {
				ftpClient = getFTPClient(ftpHost, ftpPassword, ftpUserName,port);
				int reply;
				ftpClient.setControlEncoding(encoding);

				/*
				 * 为了上传和下载中文文件，有些地方建议使用以下两句代替 new
				 * String(remotePath.getBytes(encoding),"iso-8859-1")转码。
				 * 经过测试，通不过。
				 */
				// FTPClientConfig conf = new
				// FTPClientConfig(FTPClientConfig.SYST_NT);
				// conf.setServerLanguageCode("zh");

				ftpClient.connect(ftpHost, port);
				// 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
				ftpClient.login(ftpUserName, ftpPassword);// 登录
				// 设置文件传输类型为二进制
				ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
				// 获取ftp登录应答代码
				reply = ftpClient.getReplyCode();
				// 验证是否登陆成功
				if (!FTPReply.isPositiveCompletion(reply)) {
					ftpClient.disconnect();
					System.err.println("FTP server refused connection.");
					return result;
				}
				// 转移到FTP服务器目录至指定的目录下
				ftpClient.changeWorkingDirectory(new String(remotePath.getBytes(encoding), "iso-8859-1"));
				// 获取文件列表
				FTPFile[] fs = ftpClient.listFiles();
				if (fs != null && fs.length > 0) {
					for (FTPFile ff : fs) {
						if (ff.getName().equals(fileName)) {
							ftpClient.rename(remotePath+ff.getName(), remotePath + newName);
							result = true;
							logger.info("文件重命名成功!新文件名是:" + newName);
							break;
						}
						if(isMatchFileName){
							if (-1 != ff.getName().indexOf((fileName))) {
								ftpClient.rename(remotePath+ff.getName(), remotePath + newName);
								result = true;
								logger.info("文件重命名成功!新文件名是:" + newName);
								break;
							}
						}
					}
				}

				ftpClient.logout();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (ftpClient.isConnected()) {
					try {
						ftpClient.disconnect();
					} catch (IOException ioe) {
					}
				}
			}
		}
		return result;
	}
	
	public static String generateFileName(String fileNamePrefix,String reqFlag,String resultFlag, String businessType,Date dealDate)
	{
		StringBuilder sb = new StringBuilder(fileNamePrefix);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String fileNameSuffix = "_" + simpleDateFormat.format(dealDate) + ".txt";
		
		if(StringUtil.isNotEmpty(reqFlag)){
			sb.append(reqFlag);
		}else if(StringUtil.isNotEmpty(reqFlag)){
			sb.append(resultFlag);
		}
		
		sb.append(businessType);
		sb.append(fileNameSuffix);
		
		return sb.toString();
	}
	
	
}
