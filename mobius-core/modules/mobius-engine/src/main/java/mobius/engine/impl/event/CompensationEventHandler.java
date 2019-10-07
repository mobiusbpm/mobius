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

package mobius.engine.impl.event;

import java.util.List;

import mobius.bpmn.model.Activity;
import mobius.bpmn.model.Association;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.CompensateEventDefinition;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SubProcess;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.delegate.event.FlowableEngineEventType;
import mobius.common.engine.api.delegate.event.FlowableEventDispatcher;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.delegate.event.impl.FlowableEventBuilder;
import mobius.engine.impl.bpmn.helper.ScopeUtil;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.eventsubscription.service.impl.persistence.entity.CompensateEventSubscriptionEntity;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;

/**
 *
 */
public class CompensationEventHandler implements EventHandler {

    @Override
    public String getEventHandlerType() {
        return CompensateEventSubscriptionEntity.EVENT_TYPE;
    }

    @Override
    public void handleEvent(EventSubscriptionEntity eventSubscription, Object payload, CommandContext commandContext) {

        String configuration = eventSubscription.getConfiguration();
        if (configuration == null) {
            throw new FlowableException("Compensating execution not set for compensate event subscription with id " + eventSubscription.getId());
        }

        ExecutionEntity compensatingExecution = CommandContextUtil.getExecutionEntityManager(commandContext).findById(configuration);

        String processDefinitionId = compensatingExecution.getProcessDefinitionId();
        Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
        if (process == null) {
            throw new FlowableException("Cannot start process instance. Process model (id = " + processDefinitionId + ") could not be found");
        }

        FlowElement flowElement = process.getFlowElement(eventSubscription.getActivityId(), true);

        if (flowElement instanceof SubProcess && !((SubProcess) flowElement).isForCompensation()) {

            // descend into scope:
            compensatingExecution.setScope(true);
            List<CompensateEventSubscriptionEntity> eventsForThisScope = CommandContextUtil.getEventSubscriptionService(commandContext).findCompensateEventSubscriptionsByExecutionId(compensatingExecution.getId());
            ScopeUtil.throwCompensationEvent(eventsForThisScope, compensatingExecution, false);

        } else {

            try {

                FlowableEventDispatcher eventDispatcher = CommandContextUtil.getProcessEngineConfiguration(commandContext).getEventDispatcher();
                if (eventDispatcher != null && eventDispatcher.isEnabled()) {
                    eventDispatcher.dispatchEvent(
                            FlowableEventBuilder.createActivityEvent(FlowableEngineEventType.ACTIVITY_COMPENSATE, flowElement.getId(), flowElement.getName(),
                                    compensatingExecution.getId(), compensatingExecution.getProcessInstanceId(), compensatingExecution.getProcessDefinitionId(), flowElement));
                }
                
                Activity compensationActivity = null;
                Activity activity = (Activity) flowElement;
                if (!activity.isForCompensation() && activity.getBoundaryEvents().size() > 0) {
                    for (BoundaryEvent boundaryEvent : activity.getBoundaryEvents()) {
                        if (boundaryEvent.getEventDefinitions().size() > 0 && boundaryEvent.getEventDefinitions().get(0) instanceof CompensateEventDefinition) {
                            List<Association> associations = process.findAssociationsWithSourceRefRecursive(boundaryEvent.getId());
                            for (Association association : associations) {
                                FlowElement targetElement = process.getFlowElement(association.getTargetRef(), true);
                                if (targetElement instanceof Activity) {
                                    Activity targetActivity = (Activity) targetElement;
                                    if (targetActivity.isForCompensation()) {
                                        compensationActivity = targetActivity;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                
                if (compensationActivity != null) {
                    flowElement = compensationActivity;
                }
                
                compensatingExecution.setCurrentFlowElement(flowElement);
                CommandContextUtil.getAgenda().planContinueProcessInCompensation(compensatingExecution);

            } catch (Exception e) {
                throw new FlowableException("Error while handling compensation event " + eventSubscription, e);
            }

        }
    }

}
