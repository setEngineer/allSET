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

import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class Job implements Callable {

	protected static final Logger s_logger = LogManager.getLogger(Job.class);

	protected long jobCreationTime;

	protected long totalExecutionTime = - 1;

	/**
	 * A Flag to indicate whether this.realJob  needs to be executed
	 * OR aborted.
	 */
	protected boolean abort;

	private FastThreadContext fastThreadContext;

	public Job() {

		jobCreationTime = System.currentTimeMillis();
		fastThreadContext = FastThreadContext.getMondeeContext();
	}


	public boolean isAbort() {
		return abort;
	}

	/**
	 * Set the flag indicating that the Client thread
	 * has timed out & no one is waiting for the result
	 * of the job.
	 */
	public void  cancel() {
		this.abort = Boolean.TRUE;
		onTimeout();
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public Object call() throws Exception {

		FastThreadContext.setMondeeContext(fastThreadContext);

		try {

			Object lResult = null;
			// If client has not timed out then execute the Job.
			if( !this.abort ) {

				lResult = doWork();
			} else {
				// do nothing!!! Just Log!!
				// ToDo: Change it to Logger.
				s_logger.error("Job " + toString() + " is not being executed because of the caller has timeout.");
			}

			return lResult;
		} finally {

			FastThreadContext.clear();
			totalExecutionTime = (System.currentTimeMillis() - jobCreationTime);
			logWorkTime(totalExecutionTime);
		}
	}

	public void logWorkTime(long executionTime) {

		s_logger.debug("Job completed in " + executionTime);
	}

	public void onTimeout() {
		s_logger.info(this + " job timed out");
	}

	/**
	 *
	 * @return	Time taken by the Job to execute. '-1' is
	 * 			returned if job isn't executed or it's in the middle of
	 * 			execution
	 */
	public long getTotalExecutionTime() {

		return totalExecutionTime;
	}

	public abstract Object doWork() throws Exception;

}
