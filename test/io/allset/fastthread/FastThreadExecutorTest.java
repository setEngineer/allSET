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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.testng.annotations.Test;


public class FastThreadExecutorTest {

	public static final long TIMEOUT_PERIOD = 100;

	public static final int JOB_COUNT = 5;
	public static final int LARGE_JOB_COUNT = 20;

	public static final int WORK_TIME = 50;
	public static final int LONG_WORK_TIME = 200;

	public static final int MIN_THREAD = 5;
	public static final int MAX_THREAD = 10;

	private static FastThreadExecutor ftExecutor = new FastThreadExecutor("Worker");


	@Test
	public void goGood() {

		// Step 1: Build Jobs
		List<SleepingJob> sleepingJobs = new ArrayList<SleepingJob>();

		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			sleepingJobs.add(new SleepingJob(WORK_TIME));
		}

		// Step 2: Fire the Jobs
		long startTime = System.currentTimeMillis();
		List<JobResult> results = ftExecutor.executeJobs(sleepingJobs, TIMEOUT_PERIOD);

		// All jobs should be executed with in the working time
		long totalExecutionTime = System.currentTimeMillis() - startTime;
		assertTrue(totalExecutionTime < (WORK_TIME + 30), "Total Execution Time is: " + totalExecutionTime + ", it should have been: " + (WORK_TIME + 30));

		// Step 3: Retrieve Results
		for (JobResult result : results) {

			try {
				assertNotNull(result.getResult());
			} catch (Exception e) {
				fail("Test shouldn't fail", e);
			}
		}
	}

	@Test
	public void goGood_multipleResultsFromJob() {

		// Step 1: Build Jobs
		List<MultiRoleJob> multiRoleJobs = new ArrayList<MultiRoleJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			multiRoleJobs.add(new MultiRoleJob(WORK_TIME));
		}

		// Step 2: Fire the Jobs
		long startTime = System.currentTimeMillis();
		List<JobResult> results = ftExecutor.executeJobs(multiRoleJobs, TIMEOUT_PERIOD);

		// All jobs should be executed with in the working time
		long totalExecutionTime = System.currentTimeMillis() - startTime;
		assertTrue(totalExecutionTime < (WORK_TIME + 20), "Total Execution Time is: " + totalExecutionTime + ", it should have been: " + (WORK_TIME + 20));

		// Step 3: Retrieve Results
		int counter = 0;
		for (JobResult callableResult : results) {

			if (callableResult.getException() == null) {

				// Handle the Response
				assertNotNull(multiRoleJobs.get(counter).getJobResponse());
				assertNotNull(multiRoleJobs.get(counter).getAge());
			} else {
				// Logic to handle the Exception
				fail("Transaction shouldn't have failed" + callableResult.getException());
			}

			++counter;
		}
	}

	/**
	 * 1. Drop the jobs to FT Executor
	 * 2. Do other stuffs in the parent thread
	 * 3. Come back & pick up the results.
	 *
	 */
	@Test
	public void dropAndGetResultLater() throws Exception {

		List<SleepingJob> sleepingJobs = new ArrayList<SleepingJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			sleepingJobs.add(new SleepingJob(WORK_TIME));
		}

		// Step 1: Drop the Jobs
		List<TimerFutureTask> futureResults = ftExecutor.dropJobsToExecutor(sleepingJobs);

		// Step 2: Do other work in the main thread.
		Thread.sleep(WORK_TIME);

		// Step 3: Come back & pick up the results.
		long startTime = System.currentTimeMillis();
		List<JobResult> results = ftExecutor.getResults(futureResults, TIMEOUT_PERIOD);
		// All jobs should be executed with in the working time
		long totalExecutionTime = System.currentTimeMillis() - startTime;
		assertTrue(totalExecutionTime < (WORK_TIME + 20), "Total Execution Time is: " + totalExecutionTime + ", it should have been: " + (WORK_TIME + 20));

		for (JobResult result : results) {

			try {
				assertNotNull(result.getResult());
			} catch (Exception e) {
				fail("Test shouldn't fail", e);
			}
		}
	}

	/**
	 * 1. Drop the jobs to FT Executor
	 * 2. Do other stuffs in the parent thread
	 * 3. Come back & pick up the results.
	 *
	 */
	@Test
	public void dropAndGetResultLaterNoTimeout() throws Exception {

		List<SleepingJob> sleepingJobs = new ArrayList<SleepingJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			sleepingJobs.add(new SleepingJob(WORK_TIME));
		}

		// Step 1: Drop the Jobs
		List<TimerFutureTask> futureResults = ftExecutor.dropJobsToExecutor(sleepingJobs);

		// Step 2: Do other work in the main thread.
		Thread.sleep(WORK_TIME);

		// Step 3: Come back & pick up the results.
		List<JobResult> results = ftExecutor.getResults(futureResults);
		for (JobResult result : results) {

			try {
				assertNotNull(result.getResult());
			} catch (Exception e) {
				fail("Test shouldn't fail", e);
			}
		}
	}


	/**
	 * Steps:
	 * 1. Set timeout period to be 100ms. Fire Transactions.
	 * 2. Configure transactions to run for 200ms. (In first phase of execution 100ms is spent. In second phase another 100ms is spent).
	 *
	 * Expected Result:
	 * 1. All Transaction should have timed out.
	 * 2. All Transaction should have results of phase #1 execution.
	 * 3. All Transaction shouln't have results of Phase #2 execution.
	 */
	@Test
	public void abortInMiddle() throws Exception {

		// Step 1: Build Jobs
		List<MultiRoleJob> multiRoleJobs = new ArrayList<MultiRoleJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			multiRoleJobs.add(new MultiRoleJob(LONG_WORK_TIME));
		}

		// Step 2: Fire the Jobs
		long startTime = System.currentTimeMillis();
		List<JobResult> results = ftExecutor.executeJobs(multiRoleJobs, TIMEOUT_PERIOD);

		// All jobs should be executed with in the working time
		long totalExecutionTime = System.currentTimeMillis() - startTime;
		assertTrue(totalExecutionTime < (LONG_WORK_TIME -10), "Total Execution Time is: " + totalExecutionTime + ", it should have been: " + (LONG_WORK_TIME - 10));

		// Step 3: Retrieve Results
		int counter = 0;
		for (JobResult callableResult : results) {

			if (callableResult.getException() == null) {

				// Handle the Response
				assertNotNull(multiRoleJobs.get(counter).getJobResponse());
				assertNotNull(multiRoleJobs.get(counter).getAge());
			} else {

				// Result of Phase 1 of execution 'Age' shouldn't be empty.
				assertNotNull(multiRoleJobs.get(counter).getAge());
				// Result of Phase 2 of execution 'Amadeus' shouldn't be empty.
				assertNull(multiRoleJobs.get(counter).getJobResponse());
			}

			++counter;
		}
	}


	@Test
	public void longRunningJobs_timeout() throws Exception {

		// Step 1: Build Jobs
		List<SleepingJob> sleepingJobs = new ArrayList<SleepingJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			sleepingJobs.add(new SleepingJob(LONG_WORK_TIME));
		}

		// Step 2: Execute the Jobs.
		long startTime = System.currentTimeMillis();
		List<JobResult> results = ftExecutor.executeJobs(sleepingJobs, TIMEOUT_PERIOD);

		// Test shouldn't run beyond timeout period
		long executionTime = (System.currentTimeMillis() - startTime);
		assertTrue(executionTime < (LONG_WORK_TIME));

		int exceptionCount = 0;
		for (JobResult result : results) {

			try {
				assertNotNull(result.getResult());
			} catch (Exception e) {

				assertTrue(e instanceof TimeoutException, "Expected TimeoutException, but received for job: " + e);
				++exceptionCount;
			}
		}

		// All Jobs should have timed out
		assertTrue(exceptionCount == JOB_COUNT);

		// Clean-up
		Thread.sleep(LONG_WORK_TIME);
	}


	@Test
	public void longRunningJobs_withoutTimeout() throws Exception {

		// Step 1: Build Jobs
		List<SleepingJob> sleepingJobs = new ArrayList<SleepingJob>();
		for (int counter = 0; counter < JOB_COUNT; ++counter) {

			sleepingJobs.add(new SleepingJob(LONG_WORK_TIME));
		}

		// Step 2: Execute the Jobs with out Timeout
		List<JobResult> results = ftExecutor.executeJobs(sleepingJobs);

		// Step 3: None of the jobs should have timed out
		for (JobResult result : results) {

			try {
				assertNotNull(result.getResult());
			} catch (Exception e) {
				fail("Shouldn't result in exception :( " + getStackTrace(e.getCause()));
			}
		}

		// Clean-up
		Thread.sleep(LONG_WORK_TIME);
	}

	// -------------------------------------------------------
	// Start: shutdown() API tests
	// -------------------------------------------------------

	@Test
	public void shutdown_goGood() {

		FastThreadExecutor executor = new FastThreadExecutor("shutdown");
		executor.shutdown();

		assertTrue(executor.getExecutorService().isShutdown());
	}

    public static String getStackTrace(Throwable pEx) {
        StringBuffer traceBuffer = new StringBuffer();

        CharArrayWriter charArray = null;
        PrintWriter pWriter = null;
        charArray = new CharArrayWriter();
        pWriter = new PrintWriter(charArray);
        pEx.printStackTrace(pWriter);
        traceBuffer.append(charArray.toString());

        return traceBuffer.toString();
    }
}
