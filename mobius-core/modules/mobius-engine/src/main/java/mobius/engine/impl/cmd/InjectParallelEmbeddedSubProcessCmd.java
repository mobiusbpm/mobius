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

import java.util.List;

import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SubProcess;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.dynamic.DynamicEmbeddedSubProcessBuilder;
import mobius.engine.impl.dynamic.DynamicSubProcessJoinInjectUtil;
import mobius.engine.impl.persistence.entity.DeploymentEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntityManager;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.task.service.impl.persistence.entity.TaskEntity;

public class InjectParallelEmbeddedSubProcessCmd extends AbstractDynamicInjectionCmd implements Command<Void> {

    protected String taskId;
    protected DynamicEmbeddedSubProcessBuilder dynamicEmbeddedSubProcessBuilder;

    public InjectParallelEmbeddedSubProcessCmd(String taskId, DynamicEmbeddedSubProcessBuilder dynamicEmbeddedSubProcessBuilder) {
        this.taskId = taskId;
        this.dynamicEmbeddedSubProcessBuilder = dynamicEmbeddedSubProcessBuilder;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        createDerivedProcessDefinitionForTask(commandContext, taskId);
        return null;
    }

    @Override
    protected void updateBpmnProcess(CommandContext commandContext, Process process,
            BpmnModel bpmnModel, ProcessDefinitionEntity originalProcessDefinitionEntity, DeploymentEntity newDeploymentEntity) {

        DynamicSubProcessJoinInjectUtil.injectSubProcessWithJoin(taskId, process, bpmnModel, dynamicEmbeddedSubProcessBuilder, 
                        originalProcessDefinitionEntity, newDeploymentEntity, commandContext);
    }

    @Override
    protected void updateExecutions(CommandContext commandContext, ProcessDefinitionEntity processDefinitionEntity,
                                    ExecutionEntity processInstance, List<ExecutionEntity> childExecutions) {

        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);

        TaskEntity taskEntity = CommandContextUtil.getTaskService().getTask(taskId);
        ExecutionEntity executionAtTask = executionEntityManager.findById(taskEntity.getExecutionId());
        
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(processDefinitionEntity.getId());
        FlowElement taskElement = bpmnModel.getFlowElement(executionAtTask.getCurrentActivityId());
        FlowElement subProcessElement = bpmnModel.getFlowElement(((SubProcess) taskElement.getParentContainer()).getId());
        ExecutionEntity subProcessExecution = executionEntityManager.createChildExecution(executionAtTask.getParent());
        subProcessExecution.setScope(true);
        subProcessExecution.setCurrentFlowElement(subProcessElement);
        CommandContextUtil.getActivityInstanceEntityManager(commandContext).recordActivityStart(subProcessExecution);
        
        executionAtTask.setParent(subProcessExecution);
       
        ExecutionEntity execution = executionEntityManager.createChildExecution(subProcessExecution);
        FlowElement newSubProcess = bpmnModel.getMainProcess().getFlowElement(dynamicEmbeddedSubProcessBuilder.getDynamicSubProcessId(), true);
        execution.setCurrentFlowElement(newSubProcess);

        CommandContextUtil.getAgenda().planContinueProcessOperation(execution);
    }

}