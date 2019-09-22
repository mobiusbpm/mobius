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

package mobius.rest.service.api.history;

import io.swagger.annotations.ApiModelProperty;
import mobius.rest.service.api.engine.variable.RestVariable;

/**
 * @author Tijs Rademakers
 */
public class HistoricVariableInstanceResponse {

    protected String id;
    protected String processInstanceId;
    protected String processInstanceUrl;
    protected String taskId;
    protected RestVariable variable;

    @ApiModelProperty(example = "14")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @ApiModelProperty(example = "5")
    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    @ApiModelProperty(example = "http://localhost:8182/history/historic-process-instances/5")
    public String getProcessInstanceUrl() {
        return processInstanceUrl;
    }

    public void setProcessInstanceUrl(String processInstanceUrl) {
        this.processInstanceUrl = processInstanceUrl;
    }

    @ApiModelProperty(example = "6")
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public RestVariable getVariable() {
        return variable;
    }

    public void setVariable(RestVariable variable) {
        this.variable = variable;
    }
}
