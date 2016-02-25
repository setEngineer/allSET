package io.allset.datazen;

import io.allset.datazen.entity.Field;
import io.allset.datazen.type.EnumerationType;
import io.allset.datazen.type.ResultType;
import io.allset.datazen.util.StringPrinter;

import java.util.List;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 
 * @author Ram Lakshmanan
 */
public class DataGeneratorTest {

	/*@Test
	public void generateData() throws Exception {
		
		// Build List of rows.
		// Each row will Income, NetWorth, ExpectedResult
		BusinessRulesBuilder builder = new BusinessRulesBuilder(new Field("income", new EnumerationType(), null, "100"), 
																new Field("networth", new EnumerationType(), null, "1000"), 
																new Field("expectedResult", ResultType.INSTANCE));
		builder.addRule("100", null, "YES");
		builder.addRule("101", null, "NO");
		builder.addRule(null, "1000", "YES");
		builder.addRule(null, "1001", "NO");
		
		List<List<Field>> businessRules = builder.getRules();
		
		DataGenerator generator = new DataGenerator();
		generator.generateData(businessRules);
		List<List<Field>> allData = generator.getAllData();
		List<List<Field>> cleanData = generator.getCleanData();
		
		System.out.println(StringPrinter.toString(allData));
		Assert.assertEquals(allData.size(), 8);
		Assert.assertEquals(cleanData.size(), 4);		
	}
	
	@Test
	public void generateData2() throws Exception {
		
		// Build List of rows.
		// Each row will Income, NetWorth, ExpectedResult
		BusinessRulesBuilder builder = new BusinessRulesBuilder(new Field("income", new EnumerationType(), null, "100"), 
																new Field("networth", new EnumerationType(), null, "1000"),
																new Field("occupation", new EnumerationType(), null, "Entreprenuer"),
																new Field("expectedResult", ResultType.INSTANCE));
		builder.addRule("100", null, null, "YES");
		builder.addRule("101", null, null, "NO");
		builder.addRule(null, null, "Student", "NO");
		builder.addRule(null, null, "Enterpreneur", "YES");
		builder.addRule(null, "1000", null, "YES");
		builder.addRule(null, "1001", null, "NO");
		
		List<List<Field>> businessRules = builder.getRules();
		
		DataGenerator generator = new DataGenerator();
		generator.generateData(businessRules);
		List<List<Field>> allData = generator.getAllData();
		List<List<Field>> cleanData = generator.getCleanData();
		
		System.out.println(StringPrinter.toString(allData));
		//Assert.assertEquals(allData.size(), 8);
		//Assert.assertEquals(cleanData.size(), 4);		
	}*/
	
}
