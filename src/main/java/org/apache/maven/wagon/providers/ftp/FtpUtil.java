package org.apache.maven.wagon.providers.ftp;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * This utility class implements a method that removes a non-empty directory
 * on a FTP server.
 * @author www.codejava.net
 */
public class FtpUtil {
  private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

  /**
   * Removes a non-empty directory by delete all its sub files and
   * sub directories recursively. And finally remove the directory.
   */
  public static void removeDirectory(FTPClient ftpClient, String parentDir,
                                     String currentDir) throws IOException {
    String dirToList = parentDir;
    if (!currentDir.equals("")) {
      dirToList += "/" + currentDir;
    }

    FTPFile[] subFiles = ftpClient.listFiles(dirToList);

    if (subFiles != null && subFiles.length > 0) {
      for (FTPFile aFile : subFiles) {
        String currentFileName = aFile.getName();
        if (currentFileName.equals(".") || currentFileName.equals("..")) {
          // skip parent directory and the directory itself
          continue;
        }
        String filePath = null;
        if (currentDir.equals("")) {
          filePath = parentDir + "/" + currentFileName;
        } else {
          filePath = parentDir + "/" + currentDir + "/"
              + currentFileName;
        }

        if (aFile.isDirectory()) {
          // remove the sub directory
          removeDirectory(ftpClient, dirToList, currentFileName);
        } else {
          // delete the file
          boolean deleted = ftpClient.deleteFile(filePath);
          if (deleted) {
            logger.info("DELETED the file: " + filePath);
          } else {
            logger.info("CANNOT delete the file: "
                + filePath);
          }
        }
      }

      // finally, remove the directory itself
      boolean removed = ftpClient.removeDirectory(dirToList);
      if (removed) {
        logger.info("REMOVED the directory: " + dirToList);
      } else {
        logger.info("CANNOT remove the directory: " + dirToList);
      }
    }
  }
}
