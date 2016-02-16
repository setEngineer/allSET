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

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;

/**
 *
 * @author Ram Lakshmanan
 */
public class SampleLog4jJob extends Log4jJob {

	protected String mdcValue;
	protected String ndcValue;

	private int workTime;

	public SampleLog4jJob(int workTime) {

		this.workTime = workTime;
	}

	@Override
	public Boolean doWork() throws Exception {

		mdcValue = (String)MDC.get("MDC-key-1");

		System.out.println("***************NDC Value: " + NDC.peek());
		ndcValue = NDC.peek();

		Thread.sleep(workTime);
		return Boolean.TRUE;
	}
}
