package io.allset.util;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ram Lakshmanan
 */
public class ContinuousFileWriter {

	private boolean isFirstTimeWrite = Boolean.TRUE;
	
	private File file;
	
	public ContinuousFileWriter(String filePath) throws Exception {
		
		this.file = new File(filePath);		
	}
	
	public void write(String text) throws Exception {

		if (!StringUtil.isValid(text)) {
			return;
		}
		
		List<String> textList = new ArrayList<>();
		textList.add(text);
		
		write(textList);
	}

	public void write(List<String> text) throws Exception {
		
		if (isFirstTimeWrite) {
			
			file.createNewFile();
			FileWriter writer = new FileWriter(file);
			writeToFile(writer, text);
			isFirstTimeWrite = Boolean.FALSE;
			return;
		}
		
		// Opening the File Writer in such a mode that contents will be appended
		FileWriter writer = new FileWriter(file, Boolean.TRUE);
		writeToFile(writer, text);
	}
		
	protected void writeToFile(FileWriter writer, List<String> text) throws Exception{
		
		if (text == null || text.size() ==0) {
			return;
		}
		
		for (String line : text) {
			writer.write(line);
		}
		
		writer.flush();
		writer.close();
	}
}
