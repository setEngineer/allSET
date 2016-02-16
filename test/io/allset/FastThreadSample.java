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
package io.allset;

import java.util.ArrayList;
import java.util.List;

import io.allset.fastthread.FastThreadExecutor;
import io.allset.fastthread.Job;
import io.allset.fastthread.JobResult;

/**
 * Sample program to demonstrate FastThreadExecutor usage
 *
 * @author Ram Lakshmanan
 */
public class FastThreadSample {

	/**
	 * Define Fast Thread Executor
	 */
	private static FastThreadExecutor ftExecutor = new FastThreadExecutor("Sample", // Worker Thread Name
																			5, // Thread Count
																			10); // Queue Depth

	public static void main(String args[]) {

		// Step 1: Build Jobs that needs to be executed concurrently
		List<Job> jobs = new ArrayList<Job>();
		jobs.add(new SumOfNumbersJob(500000));
		jobs.add(new SumOfNumbersJob(1000000));

		// Step 2: Execute the Jobs
		List<JobResult> jobResults = ftExecutor.executeJobs(jobs);

		// Step 3: Read the Results
		for (JobResult jobResult : jobResults) {

			try {
				System.out.println("Result is: " + jobResult.getResult());
			} catch (Exception e) {
				System.out.println("Job failed to execute: " + jobResult.getException());
			}
		}
	}

	/**
	 * Gives the sum of numbers. Example if you want to find sum of
	 * all digits from 1 - 100, this job will return the result of
	 *
	 * 1 + 2 + 3 + 4 .....+ 100
	 *
	 */
	static class SumOfNumbersJob extends Job {

		private int number;

		public SumOfNumbersJob(int number) {

			this.number = number;
		}

		@Override
		public Object doWork() throws Exception {

			int total = 0;

			for (int counter = 1; counter <= number; ++counter) {
				total += counter;
			}

			return total;
		}

	}

}
