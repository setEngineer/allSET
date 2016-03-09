package io.allset.testzen.util;

import io.allset.testzen.entity.Field;

import java.util.List;


public class StringPrinter {
	
	public static final String DELIMITER = ", ";
	
	public static final String LINE_DELIMITER = "\n";

	public static String toStringValues(List<Field> fields) {
		
		StringBuilder builder = new StringBuilder();
		
		for (Field field : fields) {
			
			builder.append(field.getValue() != null ? field.getValue() : Field.EMPTY_VALUE)
					.append(DELIMITER);
		}
		
		return builder.toString();
	}

	public static String toStringNames(List<Field> fields) {
		
		StringBuilder builder = new StringBuilder();
		
		for (Field field : fields) {
			
			builder.append(field.getName())
					.append(DELIMITER);
		}
		
		return builder.toString();
	}
	
	public static String toString(List<List<Field>> allData) {
		
		if (allData == null || allData.size() ==0) {
			
			return null;
		}
		
		StringBuilder builder = new StringBuilder();

		// First Line should contain only Names.
		builder.append(toStringNames(allData.get(0)));
		
		for (int counter = 0; counter < allData.size(); ++counter) {
			
			builder.append(LINE_DELIMITER)
					.append(toStringValues(allData.get(counter)));
		}
		
		return builder.toString();
	}
	
}
