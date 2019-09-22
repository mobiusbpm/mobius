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
package mobius.eventsubscription.service.impl.persistence.entity;

import java.util.List;

import mobius.common.engine.impl.persistence.entity.EntityManager;
import mobius.eventsubscription.api.EventSubscription;
import mobius.eventsubscription.api.EventSubscriptionBuilder;
import mobius.eventsubscription.service.impl.EventSubscriptionQueryImpl;

/**
 * @author Joram Barrez
 */
public interface EventSubscriptionEntityManager extends EntityManager<EventSubscriptionEntity> {

    /* Create entity */

    MessageEventSubscriptionEntity createMessageEventSubscription();

    SignalEventSubscriptionEntity createSignalEventSubscription();

    CompensateEventSubscriptionEntity createCompensateEventSubscription();

    /* Create and insert */

    EventSubscription createEventSubscription(EventSubscriptionBuilder eventSubscriptionBuilder);

    /* Update */

    void updateEventSubscriptionTenantId(String oldTenantId, String newTenantId);

    /* Delete */

    void deleteEventSubscriptionsForProcessDefinition(String processDefinitionId);
    
    void deleteEventSubscriptionsByExecutionId(String executionId);
    
    void deleteEventSubscriptionsForScopeIdAndType(String scopeId, String scopeType);

    /* Find (generic) */

    List<EventSubscriptionEntity> findEventSubscriptionsByName(String type, String eventName, String tenantId);

    List<EventSubscriptionEntity> findEventSubscriptionsByNameAndExecution(String type, String eventName, String executionId);

    List<EventSubscriptionEntity> findEventSubscriptionsByExecution(String executionId);

    List<EventSubscriptionEntity> findEventSubscriptionsByExecutionAndType(String executionId, String type);
    
    List<EventSubscriptionEntity> findEventSubscriptionsBySubScopeId(final String subScopeId);

    List<EventSubscriptionEntity> findEventSubscriptionsByProcessInstanceAndActivityId(String processInstanceId, String activityId, String type);

    List<EventSubscriptionEntity> findEventSubscriptionsByTypeAndProcessDefinitionId(String type, String processDefinitionId, String tenantId);

    List<EventSubscription> findEventSubscriptionsByQueryCriteria(EventSubscriptionQueryImpl eventSubscriptionQueryImpl);

    long findEventSubscriptionCountByQueryCriteria(EventSubscriptionQueryImpl eventSubscriptionQueryImpl);

    /* Find (signal) */

    List<SignalEventSubscriptionEntity> findSignalEventSubscriptionsByEventName(String eventName, String tenantId);

    List<SignalEventSubscriptionEntity> findSignalEventSubscriptionsByProcessInstanceAndEventName(String processInstanceId, String eventName);
    
    List<SignalEventSubscriptionEntity> findSignalEventSubscriptionsByScopeAndEventName(String scopeId, String scopeType, String eventName);

    List<SignalEventSubscriptionEntity> findSignalEventSubscriptionsByNameAndExecution(String name, String executionId);

    /* Find (message) */

    MessageEventSubscriptionEntity findMessageStartEventSubscriptionByName(String messageName, String tenantId);

    List<MessageEventSubscriptionEntity> findMessageEventSubscriptionsByProcessInstanceAndEventName(String processInstanceId, String eventName);

    /* Find (compensation) */

    List<CompensateEventSubscriptionEntity> findCompensateEventSubscriptionsByExecutionId(String executionId);

    List<CompensateEventSubscriptionEntity> findCompensateEventSubscriptionsByExecutionIdAndActivityId(String executionId, String activityId);

    List<CompensateEventSubscriptionEntity> findCompensateEventSubscriptionsByProcessInstanceIdAndActivityId(String processInstanceId, String activityId);

}