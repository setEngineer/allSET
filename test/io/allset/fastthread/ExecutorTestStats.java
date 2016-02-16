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
 * Object which houses the results of Executor Execution.
 *
 * @author lakshmra
 */
public class ExecutorTestStats {

    private int noOfTimeOutExceptions;
    private int otherExceptions;
    private int noOfSuccessfulResponses;

    public int getNoOfSuccessfulResponses() {
        return noOfSuccessfulResponses;
    }
    public void setNoOfSuccessfulResponses(int noOfSuccessfulResponses) {
        this.noOfSuccessfulResponses = noOfSuccessfulResponses;
    }
    public int getNoOfTimeOutExceptions() {
        return noOfTimeOutExceptions;
    }
    public void setNoOfTimeOutExceptions(int noOfTimeOutExceptions) {
        this.noOfTimeOutExceptions = noOfTimeOutExceptions;
    }
    public int getOtherExceptions() {
        return otherExceptions;
    }
    public void setOtherExceptions(int otherExceptions) {
        this.otherExceptions = otherExceptions;
    }
}
