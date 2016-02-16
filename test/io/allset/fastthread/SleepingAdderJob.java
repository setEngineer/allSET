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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A simple Job which counts from 1 to specified number. In between
 * each count he sleeps as specified by the 'sleepPeriod' state.
 *
 * @author Ram Lakshmanan
 */
public class SleepingAdderJob extends Job {

    /**
     * Name of the Mathematician
     */
    private String name;

    /**
     * How long should this Mathematician should count.
     */
    private int counter;

    /**
     * Sleeping period (in milli seconds) between every count.
     */
    private long sleepPeriod;

    /**
     * Logger object.
     */
    protected static final Logger s_logger = LogManager.getLogger(SleepingAdderJob.class);

    /**
     *
     * @param pCounter
     * @param pSleepPeriod
     */
    public SleepingAdderJob(String pMathematicianName, int pCounter, long pSleepPeriod) {

        this.name = pMathematicianName;
        this.counter = pCounter;
        this.sleepPeriod = pSleepPeriod;
    }

    public Object doWork() throws Exception {

        int lResult = 0;
        for(int lInc = 0; lInc < this.counter;) {
            // Increment the counter and add it to result.
            lResult += ++lInc;
            // Sleep for 1 sec.
            Thread.sleep(this.sleepPeriod);
        }

        // Return the computed value.
        if (s_logger.isDebugEnabled()) {

            s_logger.debug("Job: "
                              + this.name
                              + " has been executed and it resulted in "
                              + lResult
                              + " and slept for "
                              + this.sleepPeriod
                              + " (ms)");
        }

        return new Integer(lResult);
    }

}
