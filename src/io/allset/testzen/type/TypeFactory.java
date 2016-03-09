package io.allset.testzen.type;

import io.allset.util.StringUtil;


public class TypeFactory {
	
	public static Type parseType(String typeString) {

		if (!StringUtil.isValid(typeString)) {
			
			throw new IllegalArgumentException("type is empty");
		}
		
		if (ResultType.NAME.equalsIgnoreCase(typeString.trim())) {
			
			return ResultType.INSTANCE;
		}

		if (TextType.NAME.equalsIgnoreCase(typeString.trim())) {
			
			return TextType.INSTANCE;
		}

		if (EnumerationType.NAME.equalsIgnoreCase(typeString.trim())) {
			
			// EnumerationType is stateful, thus new type is created.
			return new EnumerationType();
		}

		throw new IllegalArgumentException(typeString + " is an unknown type");		
	}
}
