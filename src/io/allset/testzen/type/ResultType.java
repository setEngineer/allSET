package io.allset.testzen.type;

import io.allset.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 
 * @author Ram Lakshmanan
 */
public class ResultType implements Type {

	public static final String NAME = "Result";
	
	public static final ResultType INSTANCE = new ResultType();
	
	public static final List<Object> possibleValues = new ArrayList<>();
	
	
	public List<Object> possibleValues() {
		
		if (possibleValues.size() == 0) {
			
			possibleValues.add(Boolean.TRUE);
			possibleValues.add(Boolean.FALSE);
		}
		
		return possibleValues;
	}
	
	public Boolean parse(String str) {
		
		if (!StringUtil.isValid(str)) {
			return null;
		}
		
		if ("No".equalsIgnoreCase(str)
				|| "False".equalsIgnoreCase(str)) {
			
			return Boolean.FALSE;
		}
		
		if ("Yes".equalsIgnoreCase(str)
				|| "True".equalsIgnoreCase(str)) {
			
			return Boolean.TRUE;
		}
		
		throw new IllegalArgumentException(str + " is unknown");
	}
	
	@Override
	public String getName() {

		return NAME;
	}
	
	@Override
	public String toString() {

		return NAME;
	}	
	
}
