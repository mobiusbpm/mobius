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
package mobius.engine.impl.cmd;

import java.io.Serializable;
import java.util.Map;

import mobius.bpmn.model.CaseServiceTask;
import mobius.bpmn.model.FlowElement;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.bpmn.behavior.CaseTaskActivityBehavior;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class TriggerCaseTaskCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String executionId;
    protected Map<String, Object> variables;

    public TriggerCaseTaskCmd(String executionId, Map<String, Object> variables) {
        this.executionId = executionId;

        if (executionId == null) {
            throw new FlowableIllegalArgumentException("executionId is null");
        }
        
        this.variables = variables;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration(commandContext);
        ExecutionEntity execution = (ExecutionEntity) processEngineConfiguration.getExecutionEntityManager().findById(executionId);
        if (execution == null) {
            throw new FlowableException("No execution could be found for id " + executionId);
        }
        
        FlowElement flowElement = execution.getCurrentFlowElement();
        if (!(flowElement instanceof CaseServiceTask)) {
            throw new FlowableException("No execution could be found with a case service task for id " + executionId);
        }
        
        CaseServiceTask caseServiceTask = (CaseServiceTask) flowElement;
        
        CaseTaskActivityBehavior caseTaskActivityBehavior = (CaseTaskActivityBehavior) caseServiceTask.getBehavior();
        caseTaskActivityBehavior.triggerCaseTask(execution, variables);
        
        return null;
    }
}
