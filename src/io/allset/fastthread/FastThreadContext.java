/*
 * Copyright [2015] [Tier1app LLC]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package io.allset.fastthread;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Thread Local Implementation
 *
 * @author Ram Lakshmanan
 */
public class FastThreadContext {

	private static final ThreadLocal<FastThreadContext> threadLocalFastThreadContext = new ThreadLocal<FastThreadContext>();

	public static FastThreadContext getMondeeContext() {

		FastThreadContext MondeeContext = threadLocalFastThreadContext.get();

		if (MondeeContext == null) {
			MondeeContext = new FastThreadContext();
			threadLocalFastThreadContext.set(MondeeContext);
		}
		return MondeeContext;
	}

	public static void clear() {
		threadLocalFastThreadContext.set(null);
	}

    public static void init() {
        clear();
        getMondeeContext();
    }
 
	public static void setMondeeContext(FastThreadContext pMondeeContext) {
		threadLocalFastThreadContext.set(pMondeeContext);
	}


    private final Map<String, Object> map = Collections.synchronizedMap(new LinkedHashMap<String, Object>());

	public static boolean isMondeeContextPresent() {
		return threadLocalFastThreadContext.get() != null;
	}

	public void put(String key, Object value) {
		map.put(key, value);
	}

	public Object get(String key) {
		return map.get(key);
	}
}
