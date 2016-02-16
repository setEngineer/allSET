package io.allset.util;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Ram Lakshmanan
 */
public class StringUtil {

	public static boolean isValid(String str) {

		return (str == null || str.length() == 0) ? Boolean.FALSE : Boolean.TRUE;
	}

	public static boolean isEquals(String str1, String str2) {

		if (str1 == null && str2 == null) {

			return Boolean.TRUE;
		}

		if (str1 == null) {

			return Boolean.FALSE;
		}

		if (str2 == null) {

			return Boolean.FALSE;
		}

		return str1.equals(str2);
	}


    public static List<String> transformToList(String inputString) {

    	if (!isValid(inputString)) {

    		return null;
    	}

    	String[] stringArray = inputString.split("\n");
    	return Arrays.asList(stringArray);
    }

	public static int percentage(Long input, Long total) {

		if (input == null || total == null || input == 0 || total == 0) {
			return 0;
		}

		return Math.round((float)(input * 100)/total);
	}


}
