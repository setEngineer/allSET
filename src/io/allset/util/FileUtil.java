package io.allset.util;

import java.io.File;
import java.nio.charset.MalformedInputException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Ram Lakshmanan
 */
public class FileUtil {

	private static final Logger s_logger = LogManager.getLogger(FileUtil.class);

	protected static String uploadDir;
	protected static String archivedDir;

	public static final String UPLOAD_DIR_PROPERTY = "uploadDir";
	public static final String UPLOAD_DIR_DEFAULT = "." + File.separatorChar + "tier1appUploads";

	public static final String REAL_TIME_DIR = "current";
	public static final String ARCHIVED_DIR = "archived";

	protected static String getUploadDir() {

		if (uploadDir != null) {
			return uploadDir;
		}

		// If it's configured as system property, pick it up from that location.
		String uploadDirectory = System.getProperty(UPLOAD_DIR_PROPERTY);
		if (StringUtil.isValid(uploadDirectory)) {
			uploadDir = uploadDirectory;
		} else {
			uploadDir = UPLOAD_DIR_DEFAULT;
		}

		uploadDir = uploadDir + File.separatorChar + REAL_TIME_DIR;

		return uploadDir;
	}

	protected static String getArchivedDir() {

		if (archivedDir != null) {
			return archivedDir;
		}

		// If it's configured as system property, pick it up from that location.
		String uploadDirectory = System.getProperty(UPLOAD_DIR_PROPERTY);
		if (StringUtil.isValid(uploadDirectory)) {
			archivedDir = uploadDirectory;
		} else {
			archivedDir = UPLOAD_DIR_DEFAULT;
		}

		archivedDir = archivedDir + File.separatorChar + ARCHIVED_DIR;

		return archivedDir;
	}

	/**
	 * If input is gc.log, this function would return file path as:
	 *
	 * /tmp/uploads/2015-02-27/gc.log-5-15-26
	 *
	 * where 2015-02-27 is  YYYY-MM-DD
	 * 5-15-26 is HH-mm-ss
	 *
	 * @param fileName
	 * @return
	 */
	public static String generateFilePath(String fileName) {

		if (!StringUtil.isValid(fileName)) {
			fileName = "_";
		}

		try {

			// Sometime in VM Ware environment, full path name is sent.
			int filePathIndex = fileName.lastIndexOf(File.separatorChar);
			if (filePathIndex != -1) {

				fileName = fileName.substring(filePathIndex + 1);
			}
		} catch (Exception e) {

			s_logger.error("Failed to figure out fileName: " + fileName + " " + ExceptionUtil.getDetails(e));
			fileName = "_";
		}

		String subDir = getUploadDir();// + File.separatorChar + subDirectory + File.separatorChar;
		File createdDir = createDirectory(subDir);

		// Suffix HH-mm-ss to each Filename.
		Calendar cal = Calendar.getInstance();
		String fileSuffix = "-" + cal.get(Calendar.HOUR_OF_DAY) + "-" + cal.get(Calendar.MINUTE) + "-" + cal.get(Calendar.SECOND);

		// Filepath is root Directory + date Folder + suffixed filename
		return createdDir.getAbsolutePath() + File.separatorChar + fileName + fileSuffix;
	}

	public static File createDirectory(String dir) {

		try {

	        // creates the save directory if it does not exists
	        File fileSaveDir = new File(dir);
	        File fileSaveAbsDir = fileSaveDir.getAbsoluteFile();
	        if (!fileSaveAbsDir.exists()) {
	        	fileSaveAbsDir.mkdirs();
	        }

			return fileSaveAbsDir.getAbsoluteFile();
		} catch (Exception e) {

			s_logger.error("Failed to create directory " + ExceptionUtil.getDetails(e));
		}

		return null;
	}


	public static List<String> readFileContents(String filePath) {

		if (!StringUtil.isValid(filePath)) {

			return null;
		}

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
		} catch (Exception e) {

			s_logger.error("Failed to read file: " + filePath + ", " + ExceptionUtil.getDetails(e));
		}

		return lines;
	}

	public static final String INVALID_INPUT_SEQUENCE = "Content in the file is not legal, unable to parse the Garbage Collection Log file.";

	public static List<String> readFileContentsErrorHandling(String filePath) {

		if (!StringUtil.isValid(filePath)) {

			return null;
		}

		List<String> lines = null;
		try {
			lines = Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8);
		} catch (MalformedInputException e) {

			s_logger.error("Failed to read file: " + filePath + ", " + ExceptionUtil.getDetails(e));
			throw new IllegalArgumentException(INVALID_INPUT_SEQUENCE);
		} catch (Exception e) {

			s_logger.error("Failed to read file: " + filePath + ", " + ExceptionUtil.getDetails(e));
			throw new IllegalArgumentException("Failed to read the file, because: " + e.getMessage());
		}

		if (lines == null || lines.size() == 0) {
			throw new IllegalArgumentException("There are no contents in the file");
		}

		return lines;
	}


	public static void writeReport(String filePath, String reportString) {

		try {

			Files.write(Paths.get(filePath), reportString.getBytes());
		} catch (Exception e) {
			s_logger.error("Failed to write to file " + ExceptionUtil.getDetails(e));
		}
	}

	public static String archiveFile(String filePath) {

		try {

			File file = new File(filePath);

			// Create Folder with current date - YYYY-MM-DD
			Calendar cal = Calendar.getInstance();
			String subDirectory = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-" + cal.get(Calendar.DAY_OF_MONTH);
			String archiveDir = getArchivedDir() + File.separatorChar + subDirectory + File.separatorChar;
			createDirectory(archiveDir);

			String archivedFilePath = archiveDir + file.getName();
			file.renameTo(new File(archivedFilePath));

			return archivedFilePath;
		} catch (Exception e) {

			s_logger.error("Failed to move the file to archived folder" + ExceptionUtil.getDetails(e));
		}

		return null;
	}
}
