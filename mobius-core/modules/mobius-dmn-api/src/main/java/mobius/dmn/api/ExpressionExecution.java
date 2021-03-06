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
package mobius.dmn.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Yvo Swillens
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExpressionExecution {

    protected String id;

    @JsonProperty("exception")
    protected String exceptionMessage;

    protected Object result;

    private ExpressionExecution() {

    }

    public ExpressionExecution(String id, Object result) {
        this.id = id;
        this.result = result;
    }

    public ExpressionExecution(String id, String exceptionMessage, Object result) {
        this.id = id;
        this.exceptionMessage = exceptionMessage;
        this.result = result;
    }

    public String getId() {
        return id;
    }

    public String getException() {
        return exceptionMessage;
    }

    public Object getResult() {
        return result;
    }
}
