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
package mobius.engine.impl.agenda;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import mobius.bpmn.model.ConditionalEventDefinition;
import mobius.bpmn.model.Event;
import mobius.bpmn.model.EventSubProcess;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.FlowNode;
import mobius.bpmn.model.StartEvent;
import mobius.bpmn.model.SubProcess;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.debug.ExecutionTreeUtil;
import mobius.engine.impl.delegate.ActivityBehavior;
import mobius.engine.impl.delegate.TriggerableActivityBehavior;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.persistence.entity.ExecutionEntityManager;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;

/**
 * Operation that triggers conditional events for which the condition evaluate to true and continues the process, leaving that activity.
 * 
 *
 */
public class EvaluateConditionalEventsOperation extends AbstractOperation {

    public EvaluateConditionalEventsOperation(CommandContext commandContext, ExecutionEntity execution) {
        super(commandContext, execution);
    }

    @Override
    public void run() {
        List<ExecutionEntity> allExecutions = new ArrayList<>();
        ExecutionTreeUtil.collectChildExecutions(execution, allExecutions);
        
        String processDefinitionId = execution.getProcessDefinitionId();
        mobius.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        
        List<EventSubProcess> eventSubProcesses = process.findFlowElementsOfType(EventSubProcess.class, false);
        evaluateEventSubProcesses(eventSubProcesses, execution);
        
        for (ExecutionEntity childExecutionEntity : allExecutions) {
            String activityId = childExecutionEntity.getCurrentActivityId();
            FlowElement currentFlowElement = process.getFlowElement(activityId, true);
            if (currentFlowElement != null && currentFlowElement instanceof Event) {
                Event event = (Event) currentFlowElement;
                if (!event.getEventDefinitions().isEmpty() && event.getEventDefinitions().get(0) instanceof ConditionalEventDefinition) {
                
                    ActivityBehavior activityBehavior = (ActivityBehavior) ((FlowNode) currentFlowElement).getBehavior();
                    if (activityBehavior instanceof TriggerableActivityBehavior) {
                        ((TriggerableActivityBehavior) activityBehavior).trigger(childExecutionEntity, null, null);
                    }
                }
            
            } else if (currentFlowElement != null && currentFlowElement instanceof SubProcess) {
                SubProcess subProcess = (SubProcess) currentFlowElement;
                List<EventSubProcess> childEventSubProcesses = subProcess.findAllSubFlowElementInFlowMapOfType(EventSubProcess.class);
                evaluateEventSubProcesses(childEventSubProcesses, childExecutionEntity);
            }
        }
    }
    
    protected void evaluateEventSubProcesses(List<EventSubProcess> eventSubProcesses, ExecutionEntity parentExecution) {
        if (eventSubProcesses != null) {
            for (EventSubProcess eventSubProcess : eventSubProcesses) {
                List<StartEvent> startEvents = eventSubProcess.findAllSubFlowElementInFlowMapOfType(StartEvent.class);
                if (startEvents != null) {
                    for (StartEvent startEvent : startEvents) {
                        
                        if (startEvent.getEventDefinitions() != null && !startEvent.getEventDefinitions().isEmpty() && 
                                        startEvent.getEventDefinitions().get(0) instanceof ConditionalEventDefinition) {
                            
                            CommandContext commandContext = CommandContextUtil.getCommandContext();
                            ConditionalEventDefinition conditionalEventDefinition = (ConditionalEventDefinition) startEvent.getEventDefinitions().get(0);
                            
                            boolean conditionIsTrue = false;
                            String conditionExpression = conditionalEventDefinition.getConditionExpression();
                            if (StringUtils.isNotEmpty(conditionExpression)) {
                                Expression expression = CommandContextUtil.getProcessEngineConfiguration(commandContext).getExpressionManager().createExpression(conditionExpression);
                                Object result = expression.getValue(parentExecution);
                                if (result != null && result instanceof Boolean && (Boolean) result) {
                                    conditionIsTrue = true;
                                }
                            
                            } else {
                                conditionIsTrue = true;
                            }
                            
                            if (conditionIsTrue) {
                                ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
                                if (startEvent.isInterrupting()) {
                                    executionEntityManager.deleteChildExecutions(parentExecution, null, true);
                                }
    
                                ExecutionEntity eventSubProcessExecution = executionEntityManager.createChildExecution(parentExecution);
                                eventSubProcessExecution.setScope(true);
                                eventSubProcessExecution.setCurrentFlowElement(eventSubProcess);
                                
                                ExecutionEntity startEventSubProcessExecution = executionEntityManager.createChildExecution(eventSubProcessExecution);
                                startEventSubProcessExecution.setCurrentFlowElement(startEvent);
                                
                                CommandContextUtil.getAgenda(commandContext).planContinueProcessOperation(startEventSubProcessExecution);
                            }
                        }
                    }
                }
            }
        }
    }

}
