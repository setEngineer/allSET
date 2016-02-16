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

/**
 * Object that houses result of executing {@link Job}.
 *
 * @author Ram Lakshmanan
 */
public class JobResult {

	private Object value;

	private Exception exception;


	public JobResult() {
	}

	public JobResult(Object pValue) {
		this.value = pValue;
	}

	public JobResult(Exception pException) {
		this.exception = pException;
	}

	/**
	 * Returns the resulting object of Callable execution. Exception is
	 * thrown back if execution of Callable resulted in a exception.
	 *
	 * @return
	 * @throws Exception
	 */
	public Object getResult() throws Exception {

		if (exception != null) {
			throw exception;
		}

		return value;
	}

    //++++++++++++++++++++++++++++++++++++++++++++++++
	// Start: Simple Getters/Setters
	//++++++++++++++++++++++++++++++++++++++++++++++++
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
    //++++++++++++++++++++++++++++++++++++++++++++++++
	// End: Simple Getters/Setters
	//++++++++++++++++++++++++++++++++++++++++++++++++


}
