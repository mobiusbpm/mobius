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

/**
 *
 */
public class HistoricIdentityLinkResponse {

    protected String type;
    protected String userId;
    protected String groupId;
    protected String taskId;
    protected String taskUrl;
    protected String processInstanceId;
    protected String processInstanceUrl;

    @ApiModelProperty(example = "participant")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @ApiModelProperty(example = "kermit")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ApiModelProperty(example = "sales")
    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @ApiModelProperty(example = "null")
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @ApiModelProperty(example = "null")
    public String getTaskUrl() {
        return taskUrl;
    }

    public void setTaskUrl(String taskUrl) {
        this.taskUrl = taskUrl;
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
}
