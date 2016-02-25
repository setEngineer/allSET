package io.allset.datazen.reader;

import io.allset.datazen.entity.Field;
import io.allset.datazen.type.ResultType;
import io.allset.datazen.type.Type;
import io.allset.datazen.type.TypeFactory;
import io.allset.util.FileUtil;
import io.allset.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Ram Lakshmanan
 */
public class SimpleExcelReader {
	
	private static final Logger s_logger = LogManager.getLogger(SimpleExcelReader.class);		
	
	protected List<Field> parseMetaData(List<String> lines) {
		
		// First line will have field names
		String firstLine = lines.get(0);
		
		// Second line will have types
		String secondLine = lines.get(1);

		// Third line will have positive values
		String thirdLine = lines.get(2);
		
		String[] fieldNames = firstLine.split(",");
		String[] fieldTypes = secondLine.split(",");
		String[] fieldpositiveValues = thirdLine.split(",");
		
		if (fieldNames.length != fieldTypes.length) {
			throw new IllegalArgumentException("Number of Field Names and number of types don't match. aka - there aren't equal number of columns in first row and second row");
		}

		List<Field> metadata = new ArrayList<>();
		for (int counter=0; counter<fieldTypes.length; ++counter) {
			
			if (!StringUtil.isValid(fieldNames[counter]) && !StringUtil.isValid(fieldTypes[counter])) {
				
				metadata.add(Field.EMPTY_FIELD);
				continue;
			}
			
			Type type = null;
			try {

				type = TypeFactory.parseType(fieldTypes[counter]);
			} catch (Exception e) {
				
				String msg = "\"" + fieldTypes[counter] + "\" is unknown type for the field: \"" +  fieldNames[counter] + "\"";
				s_logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			Object positiveValue = null;			
			try {
				
				// For result types positive value isn't required
				if (!type.toString().equals(ResultType.NAME)) {
					positiveValue = type.parse(fieldpositiveValues[counter]);
				}								
			} catch (Exception e) {
				
				String msg = "\"" + fieldpositiveValues[counter]  + "\" is not a valid data for the type: \"" + type + "\". Failed to set positive value for \"" + fieldNames[counter] + "\"";				
				s_logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			metadata.add(new Field(fieldNames[counter], type, null, positiveValue));			
		}
		
		return metadata;
	}

	public List<List<Field>> read(String filePath) throws Exception{
		
		// Read contents from Excel
		List<String> lines = FileUtil.readFileContents(filePath);	
		
		List<Field> metadata = parseMetaData(lines);
		
		List<List<Field>> allData = new ArrayList<>();
		
		// Ignore the 3 lines. As it contains meta data.
		for (int lineCounter=3; lineCounter < lines.size(); ++lineCounter) {
			
			String line = lines.get(lineCounter);
			String[] fieldValues = line.split(",");
			
			List<Field> row = buildRow(metadata, fieldValues, lineCounter);
			
			allData.add(row);
		}
		
		return allData;
	}
	
	public List<Field> buildRow(List<Field> metadata, String[] fieldValues, int lineNumber) throws Exception {

		List<Field> row = new ArrayList<>();
		for (int counter=0; counter < fieldValues.length; ++counter) {
			
			Field field = buildField(metadata.get(counter), fieldValues[counter], lineNumber);
			row.add(field);				
		}
		
		return row;
	}
	
	protected Field buildField(Field metadata, String fieldValue, int lineNumber) throws Exception {
		
		Field field = null;
		try {
			
			if (metadata == Field.EMPTY_FIELD) {
				return Field.EMPTY_FIELD;
			}
			
			// Clone the Meta data
			field = (Field)metadata.cloneWithOutValue();
			
			// If value is empty in the spread sheet, set ""
			/*if (!StringUtil.isValid(fieldValue)
					|| !StringUtil.isValid(fieldValue.trim())) {
				
				field.setValue(Field.EMPTY_VALUE);
				return field;
			}*/
			
			field.setValue(field.getType().parse(fieldValue));
		} catch (Exception e) {
			
			String msg = "Failed to parse the field: " + fieldValue + " in line: " + lineNumber + ". It's not of " + field.getType().getName() + " type";
			s_logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
		
		return field;
	}
}
