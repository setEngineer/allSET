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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * @author Ram Lakshmanan
 */
public class TimerFutureTask extends FutureTask<Object> {

	/**
	 * Time at which TimeAwareFutureTask is created.
	 */
	private long startTime;

	/**
	 * Hold on to the reference of Callable. So that it can be cancelled
	 * if client thread has timed out.
	 */
	private Job cancellableCallable;

	protected TimerFutureTask(Job pCallable)  {
		super(pCallable);
		this.cancellableCallable = pCallable;
		startTime = System.currentTimeMillis();
	}

    /**
     * This is overridden to pass the difference in the time from when the execute was invoked
     * and the getResults was.
     *
     * @param pMillisecs Actual Timeout in millis
     * @return Results object.
     * @throws TimeoutException
     * @throws InterruptedException
     * @throws ExecutionException
     */
    public synchronized Object timedGet(long pMillisecs)
            throws TimeoutException, InterruptedException, ExecutionException {

        // If Result is already acquired then return it.
        if (isDone()) {
            return get();
        }

        long lDiffTime = 0;
        // If the start time is not set then donot substract the time!!!
        if( this.startTime > 0 ) {

        	// Total time spend on this Job = Current Time - Start Time.
            lDiffTime = System.currentTimeMillis() - this.startTime;

            // If the diff time is greater than timeout time then throw an exception !!
            if( lDiffTime >= pMillisecs) {
                // If the Caller has timed out then don't execute the Job.
                timedOut(pMillisecs);
            }
        }

        // Delegate the Results retrieval to the Future results!!
        try {
            return super.get(pMillisecs-lDiffTime, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            // If the Caller has timed out then don't execute the Job.
            timedOut(pMillisecs);
        }
        return null;
    }

    /**
     * Notify the Callable that Client thread has timed out and it doesn't have
     * to execute further.
     *
     * Throw the timeout exception back to the Caller!!!
     *
     * @param pMillisecs Actual timeout in millis
     * @throws TimeoutException
     */
    public void timedOut(long pMillisecs) throws TimeoutException {

        this.cancellableCallable.cancel();
    	throw new TimeoutException("Time out after: "+  pMillisecs + "milli seconds");
    }

    public void setException(Throwable t) {
    	super.setException(t);
    }
}
