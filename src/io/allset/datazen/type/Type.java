package io.allset.datazen.type;

import java.util.List;

/**
 * 
 * @author Ram Lakshmanan
 */
public interface Type {

	public String getName();
	
	public Object parse(String string);
	
	public List<Object> possibleValues();
	
}
