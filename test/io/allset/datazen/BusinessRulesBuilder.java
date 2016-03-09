package io.allset.datazen;

import io.allset.testzen.entity.Field;
import io.allset.testzen.reader.SimpleExcelReader;

import java.util.ArrayList;
import java.util.List;


public class BusinessRulesBuilder {

	List<List<Field>> businessRules = new ArrayList<>();
	
	private List<Field> metadata = new ArrayList<>();
	
	public BusinessRulesBuilder(Field ... fields) {

		for (Field field : fields) {
			metadata.add(field);
		}		
	}

	public void addRule(String ... values ) throws Exception {

		SimpleExcelReader reader = new SimpleExcelReader();
		
		List<Field> data = reader.buildRow(metadata, values, 0);
		
		businessRules.add(data);
	}

	public List<List<Field>> getRules() {

		return businessRules;
	}

}
