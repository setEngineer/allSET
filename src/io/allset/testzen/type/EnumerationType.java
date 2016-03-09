package io.allset.testzen.type;

import io.allset.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Ram Lakshmanan
 */
public class EnumerationType implements Type {

	public static final String NAME = "Enumeration";

	// As EnumerationType has possibleValues as it's state, it can't be
	// a singleton.
	//public static final EnumerationType INSTANCE = new EnumerationType();
	
	private List<Object> possibleValues = new ArrayList<>();
	
	public void addValue(Object obj) {

		if (!possibleValues.contains(obj)) {
			possibleValues.add(obj);	
		}		
	}

	public String parse(String str) {
		
		if (!StringUtil.isValid(str)) {
			return null;
		}
		
		addValue(str);
		
		return str;
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public String toString() {

		return NAME;
	}	

	@Override
	public List<Object> possibleValues() {
	
		return possibleValues;
	}

	public void setPossibleValues(List<Object> possibleValues) {
	
		this.possibleValues = possibleValues;
	}
}