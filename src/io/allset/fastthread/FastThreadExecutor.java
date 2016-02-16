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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.allset.util.ExceptionUtil;
import io.allset.util.StringUtil;

/**
 * <br/><br/><b>Concurrent Processing - simple Interface</b><p/>
 *
 * Fast thread framework provides simplified interface to do concurrent
 * processing. Here is an example how one can do concurrent processing using fast thread framework.
 *
 * <br/><br/><b>Time out Capability</b><p/>
 *
 * You can set a timeout value for the jobs to be executed. After the time out value parent thread who
 * dropped the jobs in to the pool will not wait for the jobs to complete their execution. In this way parent
 * thread is protected from indefinite wait. Here is an example scenario: From your application you want
 * to make multiple webservice API (or database) calls. If one of the webservice API isn't responding or very
 * poorly responding then your parent thread will end up waiting indefinetly. With Fast Thread Executor, you can fire
 * multiple webservice API calls concurrently by setting a time out value. If one of the webservice API isn't
 * responding, your application thread will return back after time out value with results from other
 * responding webservice APIs.
 *
 * <br/><br/><b>Optimized Resource utilization</b><p/>
 *
 * After time out value kicks-in parent thread wouldn't wait for jobs execution. When there is a big backlog of
 * jobs, most of the times even though parent thread has timed out still jobs would get executed. Basically
 * these orphaned jobs would get executed for no purpose (as caller has timed out). In such circumstance
 * compute resources (CPU, memory, network) is wasted in executing these orphaned jobs. Fast Thread Framework has
 * intelligence not to execute these orphaned jobs. Thus saving compute resources.
 *
 * <br/><br/><b>Instant recovery from backlog</b><p/>
 *
 * You would have experienced this scenario: The datasource/WebService API that you make might suffer from
 * poor response time. However after some time datasource/webservice API would have recovered, but still your
 * application will not recover from it, until application is restarted. This problem primarily happens
 * because during the time datasource/webservice API calls were poorly responding, huge work load of jobs
 * gets built up in your application. Even though datasource/Webservice API call recovered, typical concurrency
 * processing framework would be only processing the back-logged jobs. Since in fast thread framework there is
 * enforcement of time out value, after which parent thread wouldn't wait for execution of the jobs. Fast thread
 * framework will not execute any of the jobs for which parent thread isn't waiting. Thus backlog of jobs gets immediately
 * cleared after datasource/Wesbservice API calls recovers.
 *
 * <br/><br/><b>Thread Local Propagation</b><p/>
 *
 * {@link ThreadLocal} don't propagate between threads. Thus it won't propagate in a typical Executor Framework. In certain scenarios
 * you want ThreadLocal to be propagated between threads, especially in concurrent processing framework. In multi-threaded execution mode, you
 * might want workers threads to get Parent threads {@link ThreadLocal}. {@link FastThreadContext} is a
 * propagating ThreadLocal between parent threads and worker threads.
 *
 * <br/><br/><b>Graceful Exception Handling</b><p/>
 *
 * @author Ram Lakshmanan
 */
public class FastThreadExecutor {

	protected static final Logger s_logger = LogManager.getLogger(FastThreadExecutor.class);

	/**
	 * Object that executes submitted Runnable tasks. Provides a way of
	 * decoupling task submission from the mechanics of how each task
	 * will be run, including details of thread use, scheduling, etc
	 *
	 */
	private final ExecutorService executorService;

	public FastThreadExecutor(String poolName) {

		this.executorService = Executors.newCachedThreadPool(new NamedThreadFactory(poolName));
	}

	/**
	 * Creates an underlying Executor with a bounded queue.
	 */
	public FastThreadExecutor(String poolName, int minThreads, int queueCapacity ) {
		this(poolName, minThreads, minThreads, queueCapacity, new NamedThreadFactory(poolName));
	}

	/**
	 * Creates an underlying Executor with a bounded queue.
	 */
	public FastThreadExecutor(String poolName, int minThreads, int maxThreads, int queueCapacity ) {
		this(poolName, minThreads, maxThreads, queueCapacity, new NamedThreadFactory(poolName));
	}

	protected FastThreadExecutor(String poolName, int minThreads, int queueCapacity, ThreadFactory tf) {

		this(poolName, minThreads, minThreads, queueCapacity, tf);
	}

	protected FastThreadExecutor(String poolName, int minThreads, int maxThreads, int queueCapacity, ThreadFactory tf) {

		if(minThreads < 1 ||
		   maxThreads < 1 ||
		   minThreads > maxThreads ||
		   queueCapacity < 1 ||
		   !StringUtil.isValid(poolName)) {

			throw new IllegalArgumentException("Invalid Arguments");
		}

		this.executorService = new ThreadPoolExecutor(minThreads, maxThreads,
					   60L, TimeUnit.SECONDS,
					   new ArrayBlockingQueue<Runnable>(queueCapacity),
					   tf,
					   getRejectionHandler(poolName, queueCapacity));

		//((ThreadPoolExecutor)executor).
	}
	/**
	 * Uses an externally configured Executor.
	 *
	 * @param executor
	 */
	public FastThreadExecutor(ExecutorService executor) {
		if(executor==null) {
			throw new IllegalArgumentException("pExecutor is null");
		}
		this.executorService = executor;
	}

	/**
	 * Executes a list of Jobs.
	 *
	 * The order in which results of execution are returned is the same as the order
	 * in which the jobs are submitted. Call to this method blocks till execution
	 * of all submitted jobs is complete.
	 *
	 * @param callables List of Callables (i.e. Jobs) that needs to be executed.
	 * @return  List of CallableResults, which resulted in execution of Jobs.
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public List<JobResult> executeJobs(List<? extends Job> callables) {

		// Drop all the Jobs in to the Executor.
		List<TimerFutureTask> futureResults = dropJobsToExecutor(callables);

		// Retrieve the results from FutureTask
		return getResults(futureResults);
	}

    public void executeJobsNoWait(List<Job> callables) {

        // Drop all the Jobs in to the Executor.
        dropJobsToExecutor(callables);
    }

	/**
	 * Executes a single job.
	 *
	 * @param job
	 * @return
	 */
	public JobResult executeJob(Job job) {

		if (job == null) {
			throw new IllegalArgumentException("Callable Job is empty!");
		}

		List<Job> requests = new ArrayList<Job>();
		requests.add(job);

		// Invoke the Polymorphic API and return the result.
		List<JobResult> results = executeJobs(requests);
		return results.get(0);
	}

	/**
	 * Executes a list of Jobs within a specified period of time.
	 *
	 * The order in which results of execution are returned is the same as the order
	 * in which the jobs are submitted. Call to this method does block and results
	 * are returned within time specified as parameter timeoutPeriod. TimeoutException
	 * is returned for jobs that fail to complete execution within the specified time.
	 * The worker threads, however, may continue to execute the job till completion
	 * even though the client thread is no longer waiting for its results.
	 *
	 * @param jobs            		List of jobs that needs to be executed.
	 * @param timeOutPeriod        	Period in milliseconds, upto which client thread block for the completion
	 *                              of execution. If a job takes more time to complete then the specified
	 *                              timeoutPeriod, then it will result in a TimeOutException in the corresponding CallableResult
	 * @return                      List of CallableResults, which resulted in execution
	 *                              of Jobs.
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	public List<JobResult> executeJobs(List<? extends Job> jobs, long timeOutPeriod) {

		// Drop all the Jobs in to the Executor.
		List<TimerFutureTask> futureResults = dropJobsToExecutor(jobs);

		return getResults(futureResults, timeOutPeriod);
	}

	/**
	 * Executes a job within a specified period of time.
	 *
	 * @param job
	 * @return
	 */
	public JobResult executeJob(Job job, long timeoutPeriod) {

		if (job == null) {
			throw new IllegalArgumentException("Callable Job is empty!");
		}

		List<Job> requests = new ArrayList<Job>();
		requests.add(job);

		// Invoke the Polymorphic API and return the result.
		List<JobResult> results = executeJobs(requests, timeoutPeriod);
		return results.get(0);
	}

	public TimerFutureTask dropJobToExecutor(Job job) {

		if (job == null) {
			throw new IllegalArgumentException("Callable Job is empty!");
		}

		List<Job> requests = new ArrayList<Job>();
		requests.add(job);

		// Invoke the Polymorphic API and return the result.
		return dropJobsToExecutor(requests).get(0);
	}


	/**
	 * Utility method that drops all the Jobs in to the Executor.
	 *
	 * @param jobs
	 * @return list of future result sets on which client thread will wait.
	 */
	public List<TimerFutureTask> dropJobsToExecutor(List<? extends Job> jobs) {

		if (jobs == null || jobs.size() == 0) {

			throw new IllegalArgumentException("Callable Jobs list is empty!");
		}

		// Drop all the Jobs in to the Executor
		List<TimerFutureTask> futureResults = new ArrayList<TimerFutureTask>(jobs.size());

		for (Job job : jobs) {

			// Put the CancellableSessionAwareJob in to a FutureTask.
			TimerFutureTask futureTask = new TimerFutureTask(createWrapper(job));

	        try {

	        	dropJobToExecutor(futureTask);
	        	//this.executor.execute(futureTask);
	        } catch(Throwable t) {

	        	s_logger.error("Job failed to execute: " + ExceptionUtil.getDetails(t));
	        	futureTask.setException(t);
	        }

	        futureResults.add(futureTask);
		}

		return futureResults;
	}

	/**
	 * Some of the child classes of FastThreadExecutor might want to create wrapper
	 * job. To support that functionality, this method is exposed.
	 *
	 * @param job
	 * @return
	 */
	public Job createWrapper(Job job) {

		return job;
	}

	/**
	 * submits jobs to the underlying Executor. Check for pre-conditions if any.
	 *
	 * @param lFutureTask
	 */
	protected void dropJobToExecutor(TimerFutureTask lFutureTask) {
		// no pre-condition check is required here.
		this.executorService.execute(lFutureTask);
	}


	public JobResult getResult(TimerFutureTask futureResult, long timeOutPeriod) {

		if (futureResult == null) {
			throw new IllegalArgumentException("Future Result is empty!");
		}

		List<TimerFutureTask> futureResults = new ArrayList<TimerFutureTask>(1);
		futureResults.add(futureResult);


		// Invoke the Polymorphic API and return the result.
		return getResults(futureResults, timeOutPeriod).get(0);
	}


	public List<JobResult> getResults(List<TimerFutureTask> futureResults, long timeOutPeriod) {

		if (futureResults == null || futureResults.size() == 0) {
			throw new IllegalArgumentException("Future Results is empty!");
		}

		// Retrieve the results from FutureTask
		List<JobResult> results = new ArrayList<JobResult>(futureResults.size());

		Iterator<TimerFutureTask> lIterator = futureResults.iterator();
		while(lIterator.hasNext()) {

			TimerFutureTask futureTask = (TimerFutureTask)lIterator.next();
		    try {
		    	Object lValue = futureTask.timedGet(timeOutPeriod);
		    	results.add(new JobResult(lValue));
			}  catch(TimeoutException lException) {

				handleTimeOutException(lException);
				// If any exception arises during the execution then exception is added to the Result set.
				results.add(new JobResult(lException));
			} catch(Exception lException) {
				// If any exception arises during the execution then exception is added to the Result set.
				results.add(new JobResult(lException));
			}
		}

		// Return the retrieved results.
		return results;
	}

	public List<JobResult> getResults(List<TimerFutureTask> futureResults) {

		if (futureResults == null || futureResults.size() == 0) {
			throw new IllegalArgumentException("Future Results is empty!");
		}

		// Retrieve the results from FutureTask
		List<JobResult> results = new ArrayList<JobResult>();
		Iterator<TimerFutureTask> lIterator = futureResults.iterator();
		while(lIterator.hasNext()) {

			FutureTask<?> futureTask = (FutureTask<?>)lIterator.next();
		    try {
		    	Object lValue = futureTask.get();
		    	results.add(new JobResult(lValue));
			} catch(Exception lException) {
				// If any exception arises during the execution then exception is added to the Result set.
				results.add(new JobResult(lException));
			}
		}

		// Return the retrieved results.
		return results;
	}

	protected RejectedExecutionHandler getRejectionHandler(String poolName, int queueCapacity) {

		return new AbortTaskExecution(poolName, queueCapacity);
	}

	protected void handleTimeOutException(TimeoutException exception) {

		s_logger.error("Job Timed Out. Message: " + ExceptionUtil.getDetails(exception));
	}

	public ExecutorService getExecutorService() {
		return executorService;
	}

	/**
	 * Shuts down the executor gracefully, after executing the current
	 * jobs. No new jobs will be processed. All the worker threads will
	 * be evicted after successful execution of current jobs.
	 *
	 */
	public void shutdown() {

		executorService.shutdown();
	}

    /**
     * Extends RejectedExecutionHandler collects the current status of the
     * thread pool executor and adds that to the exception message.
     */
    static class AbortTaskExecution implements RejectedExecutionHandler {

    	private final String poolName;
    	private final int queueSize;

    	public AbortTaskExecution(String poolName, int queueSize) {

    		this.poolName = poolName;
    		this.queueSize = queueSize;
    	}

    	public void rejectedExecution(Runnable r, ThreadPoolExecutor tpe) {

    		String msg = new StringBuffer()
    					 .append("PoolName-").append(poolName)
    		             .append(", ThreadCount-").append(tpe.getCorePoolSize())
    		             .append(", ActiveThreadCount-").append(tpe.getActiveCount())
    		             .append(", QueueCapacity-").append(queueSize)
    		             .append(", RemainingQCapacity-").append(tpe.getQueue().remainingCapacity())
    		             .toString();

    		throw new JobAbortedException(msg);
    	}
    }

	/**
	 * Creates Threads named based on pool names and belonging to parent
	 * threads thread group.
	 */
    static class NamedThreadFactory implements ThreadFactory {
        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);
        final String namePrefix;

        NamedThreadFactory(String poolName) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null)? s.getThreadGroup() :
                                 Thread.currentThread().getThreadGroup();
            namePrefix = poolName + "-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                                  namePrefix + threadNumber.getAndIncrement());

            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
    }

	/**
	 * Creates re-usable thread names derived out of pool name. When threads
	 * die, their names are taken back by this factory to be re-issued when
	 * new thread creation request is received.
	 *
	 *
	 */
	static class ReUsableNamedThreadFactory implements ThreadFactory {

		final ThreadGroup group;
		final List threadNames;

		ReUsableNamedThreadFactory(String poolName, int nThreads) {
			SecurityManager s = System.getSecurityManager();
			group = (s != null)? s.getThreadGroup() :
				Thread.currentThread().getThreadGroup();
			threadNames = Collections.synchronizedList(new LinkedList());
			for(int i=0;i<nThreads;i++) {
				threadNames.add(poolName + "-" + i);
			}
		}

		public Thread newThread(final Runnable r) {

			// create a local runnable wrapper around the actual
			// runnable task. This wrapper returns the name of
			// exiting thread back to the threadNames list.

			// Note the wrapper is usefull only if the underlying
			// executor allows core thread timeout.
			Runnable r_local = new Runnable() {
				public void run() {
					try {
						r.run();
					} finally {
						threadNames.add(Thread.currentThread().getName());
					}
				}
			};
			Thread t = new Thread(group, r_local, (String)threadNames.remove(0));
			if (t.isDaemon())
				t.setDaemon(false);
			if (t.getPriority() != Thread.NORM_PRIORITY)
				t.setPriority(Thread.NORM_PRIORITY);
			return t;
		}
	}
}
