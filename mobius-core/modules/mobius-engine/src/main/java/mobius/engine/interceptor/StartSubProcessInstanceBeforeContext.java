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
package mobius.engine.interceptor;

import java.util.List;
import java.util.Map;

import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.IOParameter;
import mobius.bpmn.model.Process;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.repository.ProcessDefinition;

public class StartSubProcessInstanceBeforeContext extends AbstractStartProcessInstanceBeforeContext {

    protected ExecutionEntity callActivityExecution;
    protected List<IOParameter> inParameters;
    protected boolean inheritVariables;
    
    public StartSubProcessInstanceBeforeContext() {
        
    }
    
    public StartSubProcessInstanceBeforeContext(String businessKey, String processInstanceName, Map<String, Object> variables, 
                    ExecutionEntity callActivityExecution, List<IOParameter> inParameters,
                    boolean inheritVariables, String initialActivityId, FlowElement initialFlowElement, 
                    Process process, ProcessDefinition processDefinition) {
        
        super(businessKey, processInstanceName, variables, initialActivityId, initialFlowElement, process, processDefinition);
        
        this.callActivityExecution = callActivityExecution;
        this.inParameters = inParameters;
        this.inheritVariables = inheritVariables;
    }

    public ExecutionEntity getCallActivityExecution() {
        return callActivityExecution;
    }

    public void setCallActivityExecution(ExecutionEntity callActivityExecution) {
        this.callActivityExecution = callActivityExecution;
    }

    public List<IOParameter> getInParameters() {
        return inParameters;
    }

    public void setInParameters(List<IOParameter> inParameters) {
        this.inParameters = inParameters;
    }

    public boolean isInheritVariables() {
        return inheritVariables;
    }

    public void setInheritVariables(boolean inheritVariables) {
        this.inheritVariables = inheritVariables;
    }
}