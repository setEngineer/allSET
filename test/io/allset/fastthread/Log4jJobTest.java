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

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.MDC;
import org.apache.log4j.NDC;
import org.testng.annotations.Test;

/**
 *
 * @author Ram Lakshmanan
 */
public class Log4jJobTest {

	private static FastThreadExecutor ftExecutor = new FastThreadExecutor("Worker");

	public static final String MDC_KEY_1 = "MDC-key-1";
	public static final String MDC_VALUE_1 = "MDC-value-1";
	public static final String NDC_ELEMENT = "NDC-element-1";

	@Test
	public void goGood() {

		MDC.put(MDC_KEY_1, MDC_VALUE_1);
		NDC.push(NDC_ELEMENT);

		List<SampleLog4jJob> sleepingJobs = new ArrayList<SampleLog4jJob>();
		SampleLog4jJob job = new SampleLog4jJob(FastThreadExecutorTest.WORK_TIME);
		sleepingJobs.add(job);

		List<JobResult> results = ftExecutor.executeJobs(sleepingJobs, FastThreadExecutorTest.TIMEOUT_PERIOD);

		assertEquals(job.mdcValue, MDC_VALUE_1);
		assertEquals(job.ndcValue, NDC_ELEMENT);
	}
}
