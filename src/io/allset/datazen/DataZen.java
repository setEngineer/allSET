package io.allset.datazen;

import io.allset.datazen.entity.Field;
import io.allset.datazen.reader.SimpleExcelReader;
import io.allset.datazen.util.StringPrinter;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * @author Ram Lakshmanan
 */
public class DataZen {

	private static final Logger s_logger = LogManager.getLogger(DataZen.class);	
	
	public static void main(String args[]) throws Exception {
		
		validateInput(args);
		
		String filePath = args[0];
		
		// Read the data from Excel spread sheet & populate the objects with values from Excel.
		SimpleExcelReader reader = new SimpleExcelReader();
		List<List<Field>> businessRules = reader.read(filePath);
		System.out.println("Business Rules:\n " + StringPrinter.toString(businessRules));
		
		DataGenerator dataGenerator = new DataGenerator(filePath);
		dataGenerator.generateData(filePath, businessRules);
	}

	private static void validateInput(String[] args) {

		// TODO Auto-generated method stub
		if (args == null || args.length == 0) {
			
			String msg = "Invalid input. Business Rules File path not passed!";
			s_logger.error(msg);
			throw new IllegalArgumentException(msg);
		}
	}
	
}
