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
package mobius.engine.impl.bpmn.behavior;

import java.util.List;

import mobius.bpmn.model.Activity;
import mobius.bpmn.model.Association;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.CompensateEventDefinition;
import mobius.bpmn.model.FlowElement;
import mobius.bpmn.model.Process;
import mobius.bpmn.model.SubProcess;
import mobius.common.engine.api.FlowableException;
import mobius.engine.delegate.DelegateExecution;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.impl.util.CountingEntityUtil;
import mobius.engine.impl.util.ProcessDefinitionUtil;
import mobius.eventsubscription.service.EventSubscriptionService;
import mobius.eventsubscription.service.impl.persistence.entity.CompensateEventSubscriptionEntity;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntity;

/**
 * @author Tijs Rademakers
 */
public class BoundaryCompensateEventActivityBehavior extends BoundaryEventActivityBehavior {

    private static final long serialVersionUID = 1L;

    protected CompensateEventDefinition compensateEventDefinition;

    public BoundaryCompensateEventActivityBehavior(CompensateEventDefinition compensateEventDefinition, boolean interrupting) {
        super(interrupting);
        this.compensateEventDefinition = compensateEventDefinition;
    }

    @Override
    public void execute(DelegateExecution execution) {
        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        BoundaryEvent boundaryEvent = (BoundaryEvent) execution.getCurrentFlowElement();

        Process process = ProcessDefinitionUtil.getProcess(execution.getProcessDefinitionId());
        if (process == null) {
            throw new FlowableException("Process model (id = " + execution.getId() + ") could not be found");
        }

        Activity sourceActivity = null;
        Activity compensationActivity = null;
        List<Association> associations = process.findAssociationsWithSourceRefRecursive(boundaryEvent.getId());
        for (Association association : associations) {
            sourceActivity = boundaryEvent.getAttachedToRef();
            FlowElement targetElement = process.getFlowElement(association.getTargetRef(), true);
            if (targetElement instanceof Activity) {
                Activity activity = (Activity) targetElement;
                if (activity.isForCompensation()) {
                    compensationActivity = activity;
                    break;
                }
            }
        }
        
        if (sourceActivity == null) {
            throw new FlowableException("Parent activity for boundary compensation event could not be found");
        }

        if (compensationActivity == null) {
            throw new FlowableException("Compensation activity could not be found (or it is missing 'isForCompensation=\"true\"'");
        }

        // find SubProcess or Process instance execution
        ExecutionEntity scopeExecution = null;
        ExecutionEntity parentExecution = executionEntity.getParent();
        while (scopeExecution == null && parentExecution != null) {
            if (parentExecution.getCurrentFlowElement() instanceof SubProcess) {
                scopeExecution = parentExecution;

            } else if (parentExecution.isProcessInstanceType()) {
                scopeExecution = parentExecution;
            } else {
                parentExecution = parentExecution.getParent();
            }
        }

        if (scopeExecution == null) {
            throw new FlowableException("Could not find a scope execution for compensation boundary event " + boundaryEvent.getId());
        }

        EventSubscriptionEntity eventSubscription = (EventSubscriptionEntity) CommandContextUtil.getEventSubscriptionService().createEventSubscriptionBuilder()
                        .eventType(CompensateEventSubscriptionEntity.EVENT_TYPE)
                        .executionId(scopeExecution.getId())
                        .processInstanceId(scopeExecution.getProcessInstanceId())
                        .activityId(sourceActivity.getId())
                        .tenantId(scopeExecution.getTenantId())
                        .create();
        
        CountingEntityUtil.handleInsertEventSubscriptionEntityCount(eventSubscription);
    }

    @Override
    public void trigger(DelegateExecution execution, String triggerName, Object triggerData) {
        ExecutionEntity executionEntity = (ExecutionEntity) execution;
        BoundaryEvent boundaryEvent = (BoundaryEvent) execution.getCurrentFlowElement();

        if (boundaryEvent.isCancelActivity()) {
            EventSubscriptionService eventSubscriptionService = CommandContextUtil.getEventSubscriptionService();
            List<EventSubscriptionEntity> eventSubscriptions = executionEntity.getEventSubscriptions();
            for (EventSubscriptionEntity eventSubscription : eventSubscriptions) {
                if (eventSubscription instanceof CompensateEventSubscriptionEntity && eventSubscription.getActivityId().equals(compensateEventDefinition.getActivityRef())) {
                    eventSubscriptionService.deleteEventSubscription(eventSubscription);
                    CountingEntityUtil.handleDeleteEventSubscriptionEntityCount(eventSubscription);
                }
            }
        }

        super.trigger(executionEntity, triggerName, triggerData);
    }
}