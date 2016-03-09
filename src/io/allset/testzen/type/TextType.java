package io.allset.testzen.type;

import java.util.List;

import io.allset.util.StringUtil;

/**
 * 
 * @author Ram Lakshmanan
 */
public class TextType implements Type {

	public static final String NAME = "Text";
	
	public static final TextType INSTANCE = new TextType();
	
	public String parse(String str) {
		
		if (!StringUtil.isValid(str)) {
			return null;
		}
		
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

		return null;
	}	
}
