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
package mobius.cmmn.engine.interceptor;

import java.util.Map;

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;

public class StartCaseInstanceBeforeContext extends AbstractStartCaseInstanceBeforeContext {

    protected String callbackId;
    protected String callbackType;
    protected String parentId;
    protected Map<String, Object> transientVariables;
    protected String tenantId;
    protected String initiatorVariableName;
    protected String overrideDefinitionTenantId;
    protected String predefinedCaseInstanceId;
    
    public StartCaseInstanceBeforeContext() {
        
    }
    
    public StartCaseInstanceBeforeContext(String businessKey, String caseInstanceName, String callbackId, String callbackType, 
                    String parentId, Map<String, Object> variables, Map<String, Object> transientVariables, String tenantId, 
                    String initiatorVariableName, Case caseModel, CaseDefinition caseDefinition, CmmnModel cmmnModel,
                    String overrideDefinitionTenantId, String predefinedCaseInstanceId) {
        
        super(businessKey, caseInstanceName, variables, caseModel, caseDefinition, cmmnModel);
        
        this.callbackId = callbackId;
        this.callbackType = callbackType;
        this.parentId = parentId;
        this.transientVariables = transientVariables;
        this.tenantId = tenantId;
        this.initiatorVariableName = initiatorVariableName;
        this.overrideDefinitionTenantId = overrideDefinitionTenantId;
        this.predefinedCaseInstanceId = predefinedCaseInstanceId;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackType() {
        return callbackType;
    }

    public void setCallbackType(String callbackType) {
        this.callbackType = callbackType;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Map<String, Object> getTransientVariables() {
        return transientVariables;
    }

    public void setTransientVariables(Map<String, Object> transientVariables) {
        this.transientVariables = transientVariables;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getInitiatorVariableName() {
        return initiatorVariableName;
    }

    public void setInitiatorVariableName(String initiatorVariableName) {
        this.initiatorVariableName = initiatorVariableName;
    }

    public String getOverrideDefinitionTenantId() {
        return overrideDefinitionTenantId;
    }

    public void setOverrideDefinitionTenantId(String overrideDefinitionTenantId) {
        this.overrideDefinitionTenantId = overrideDefinitionTenantId;
    }

    public String getPredefinedCaseInstanceId() {
        return predefinedCaseInstanceId;
    }

    public void setPredefinedCaseInstanceId(String predefinedCaseInstanceId) {
        this.predefinedCaseInstanceId = predefinedCaseInstanceId;
    }
}
