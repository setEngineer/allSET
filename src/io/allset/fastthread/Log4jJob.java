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

import java.util.Hashtable;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;

/**
 * Propagates {@link MDC} and {@link NDC} to child threads
 *
 * @author Ram Lakshmanan
 */
public abstract class Log4jJob extends Job {

	private Hashtable parentMDC;
	private Stack parentNDC;

	public Log4jJob() {

		super();
		parentMDC = MDC.getContext();
		parentNDC = NDC.cloneStack();
	}

	@Override
	public Object call() throws Exception {

		try {
			// Transition the MDC.
			if (parentMDC != null) {

				Set<String> keys = parentMDC.keySet();
				for (String key : keys) {
					MDC.put(key, parentMDC.get(key));
				}
			}

			// Transition NDC
			if (parentNDC != null) {
				NDC.inherit(parentNDC);
			}

			return super.call();
		} finally {

			if (parentMDC != null) {
				MDC.clear();
			}
			if (parentNDC != null) {
				NDC.clear();
			}
		}
	}

}
