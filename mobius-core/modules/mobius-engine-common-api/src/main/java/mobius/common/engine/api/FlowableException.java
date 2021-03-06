/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobius.common.engine.api;

/**
 * Runtime exception that is the superclass of all Flowable exceptions.
 * 
 *
 */
public class FlowableException extends RuntimeException {

    private static final long serialVersionUID = 1L;
  
    protected boolean isLogged;
    protected boolean reduceLogLevel;

    public FlowableException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public FlowableException(String message) {
        super(message);
    }

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    public boolean isReduceLogLevel() {
        return reduceLogLevel;
    }

    public void setReduceLogLevel(boolean reduceLogLevel) {
        this.reduceLogLevel = reduceLogLevel;
    }
    
}
