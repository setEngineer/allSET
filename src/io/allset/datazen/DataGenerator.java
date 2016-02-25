package io.allset.datazen;

import io.allset.datazen.entity.Field;
import io.allset.datazen.type.ResultType;
import io.allset.datazen.util.StringPrinter;
import io.allset.util.ContinuousFileWriter;
import io.allset.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ram Lakshmanan
 */
public class DataGenerator {
	
	private long allDataCount;
	private long cleanDataCount;
	
	private String allDataFilePath;
	private String cleanDataFilePath;

	private ContinuousFileWriter allDataWriter;
	private ContinuousFileWriter cleanDataWriter;
	
	public DataGenerator(String filePath) throws Exception {
	
		allDataFilePath = suffixFileName(filePath, "-allData");
		cleanDataFilePath = suffixFileName(filePath, "-positiveData");
		
		allDataWriter = new ContinuousFileWriter(allDataFilePath);
		cleanDataWriter = new ContinuousFileWriter(cleanDataFilePath);				
	}
	
	public void generateData(String filePath, List<List<Field>> businessRules) throws Exception {
		
		if (businessRules == null) {
			return;
		}
		
		List<List<Field>> businessRulesNoResult = new ArrayList<>();
		List<List<Field>> businessRulesYesResult = new ArrayList<>();
		
		// Get Data with negative Results & Positive Results
		for (List<Field> rule : businessRules) {

			if (doesRuleHasYesResult(rule)) {
				
				businessRulesYesResult.add(rule);
			} else {
				
				businessRulesNoResult.add(rule);
			}
		}

		System.out.print("Generating data ");
		generateDataNoResult(businessRulesNoResult);
		generateDataYesResult(businessRulesYesResult);
		
		System.out.println("\nGenerated " + allDataCount + " test data in " + allDataFilePath);
		System.out.println("Generated " + cleanDataCount + " positive test data" + cleanDataFilePath);
		System.out.println("Enjoy!");
	}
	
	public void generateDataYesResult(List<List<Field>> businessRules) throws Exception {

		Map<String, List<Field>> masterMap = new HashMap<>();
		
		// Loop through each rule
		for (List<Field> originalRule : businessRules) {
		
			List<Field> clonedRule = cloneRow(originalRule);
			
			for (Field field : clonedRule) {
				
				//Field field = originalRule.get(fieldCounter);
				
				if (field == Field.EMPTY_FIELD) {
					continue;
				}

				if (field.getValue() == null) {
					field.setValue(field.getPositiveValue());
				}				
			}
			
			masterMap.put(StringPrinter.toStringValues(originalRule), originalRule);	
			masterMap.put(StringPrinter.toStringValues(clonedRule), clonedRule);	
		} // end: for (List<Field> originalRule : businessRules) {
		
		
		// Transform Map to Array List
		List<List<Field>> masterList = new ArrayList<>();		
		Iterator<String> keysIterator =  masterMap.keySet().iterator();
		while(keysIterator.hasNext()) {
			
			masterList.add(masterMap.get(keysIterator.next()));
		}
		
		List<List<Field>> cleanData = eliminateNull(masterList);
		allDataWriter.write(StringPrinter.toString(masterList));		
		cleanDataWriter.write(StringPrinter.toString(cleanData));
		
		allDataCount += masterList.size();
		cleanDataCount += cleanData.size();
		System.out.print("..");
	}
	
	public void generateDataNoResult(List<List<Field>> businessRules) throws Exception {

		// Loop through each rule
		for (List<Field> originalRule : businessRules) {
					
			List<List<Field>> newDatas = new ArrayList<>();
			newDatas.add(originalRule);
			
			// Loop through every field in this Rule
			for (int fieldCounter = 0; fieldCounter < originalRule.size(); ++fieldCounter) {
			
				Field field = originalRule.get(fieldCounter);
				
				if (field == Field.EMPTY_FIELD
						|| field.getType().toString().equals(ResultType.NAME)) {
					continue;
				}

				if (field.getValue() != null) {
					continue;
				}
				
				// If the field doesn't have any value, then for every data in the list, clone it
				// and insert all possible values of this field.
				List<List<Field>> interimDatas = new ArrayList<>();
				interimDatas.addAll(newDatas);
				//interimDatas = eliminateDuplicates(interimDatas);
				
				int number = field.getType().possibleValues() != null ? field.getType().possibleValues().size() : 0;
				for(int valueCounter = 0; valueCounter < number; ++valueCounter) {
			
					for (List<Field> tempData : newDatas) {

						List<Field> clone = cloneRow(tempData);
						Object value = (field.getType().possibleValues().get(valueCounter));
						try {
							clone.get(fieldCounter).setValue(value);
						} catch (Exception e) {
							e.printStackTrace();
						}
						//System.out.println(clone);
						interimDatas.add(clone);				
					}
				}	
				
				// Erasing old datas with new datas which has newly populated values
				newDatas = interimDatas;
				interimDatas = null;	// to facilitate GC.				
			}
			
			List<List<Field>> cleanData = eliminateNull(newDatas);
			allDataWriter.write(StringPrinter.toString(newDatas));
			cleanDataWriter.write(StringPrinter.toString(cleanData));
			
			allDataCount += newDatas.size();
			cleanDataCount += cleanData.size();	
			System.out.print("..");
		} // end: for (List<Field> originalRule : businessRules) {		
	}
	
	// ---------------------------------------------------------
	//		Start: Utility APIs
	// ---------------------------------------------------------

	/**
	 * 
	 * 
	 * @param filePath
	 * @param fileSuffix
	 * @return				if filePath is: "c:\workspace\foo.csv" and fileSuffix is "-allData"
	 * 						then result would be "c:\workspace\foo-allData.csv"<br/>
	 * 						
	 * 						if filePath is: "c:\workspace\foo" and fileSuffix is "-allData"
	 * 						then result would be "c:\workspace\foo-allData"<br/>
	 */
	private String suffixFileName(String filePath, String fileSuffix) {

		if (!StringUtil.isValid(filePath)) {
			return null;
		}

		String[] filePathElements = filePath.split("\\.");
		
		if (filePathElements == null || filePathElements.length == 0) {
			return filePath + fileSuffix;
		}

		String lastElement = filePathElements[filePathElements.length-1];
		lastElement = fileSuffix + "." + lastElement;
		filePathElements[filePathElements.length-1] = lastElement;
		
		StringBuilder builder = new StringBuilder();
		for (String filePathElement : filePathElements) {
			
			builder.append(filePathElement);
		}
		
		return builder.toString();		
	}
	
	private List<List<Field>> eliminateDuplicates(List<List<Field>> datas) {
		
		Map<String, List<Field>> map = new HashMap<>();
		
		// This will eliminate the duplicates
		for (List<Field> finalData : datas) {
			
			map.put(StringPrinter.toStringValues(finalData), finalData);	
		}						
				
		// Transform Map to Array List
		List<List<Field>> masterList = new ArrayList<>();		
		Iterator<String> keysIterator =  map.keySet().iterator();
		while(keysIterator.hasNext()) {
			
			masterList.add(map.get(keysIterator.next()));
		}		
		
		return masterList;
	}
	
	private List<List<Field>> eliminateNull(List<List<Field>> datas) throws Exception{
		
		List<List<Field>> masterList = new ArrayList<>();	
		
		// This will eliminate the duplicates
		for (List<Field> finalData : datas) {
			
			if (!containsNullValue(finalData)) {
				masterList.add(finalData);
			}
		}						
				
		return masterList;
	}
	
	public static List<Field> cloneRow(List<Field> data) throws Exception {
		
		List<Field> newData = new ArrayList<>();
		
		for (Field field : data) {
			
			newData.add((Field)field.clone());
		}
		
		return newData;
	}

	public static boolean containsNullValue(List<Field> data) throws Exception {
		
		List<Field> newData = new ArrayList<>();
		
		for (Field field : data) {
			
			if (field == Field.EMPTY_FIELD) {
				return Boolean.TRUE;
			}
			
			if(field.getValue() == null) {
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public boolean doesRuleHasYesResult(List<Field> rule) {
		
		for (Field field : rule) {
			
			if (field == Field.EMPTY_FIELD) {
				continue;
			}
			
			if (field.getType() != null
					&& field.getType().equals(ResultType.INSTANCE)) {
				
				if (field.getValue().equals(Boolean.TRUE) ) {

					return Boolean.TRUE;
				}
			}
		}
		
		return Boolean.FALSE;
	}
	
}
