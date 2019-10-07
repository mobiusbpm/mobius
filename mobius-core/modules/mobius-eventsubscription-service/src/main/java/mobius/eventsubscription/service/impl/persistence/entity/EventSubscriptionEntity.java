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

import java.util.Date;

import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.eventsubscription.api.EventSubscription;

/**
 *
 *
 */
public interface EventSubscriptionEntity extends EventSubscription, Entity, HasRevision {

    void setEventType(String eventType);

    void setEventName(String eventName);

    void setExecutionId(String executionId);

    void setProcessInstanceId(String processInstanceId);

    void setConfiguration(String configuration);

    void setActivityId(String activityId);

    void setCreated(Date created);

    void setProcessDefinitionId(String processDefinitionId);
    
    void setSubScopeId(String subScopeId);
    
    void setScopeId(String scopeId);
    
    void setScopeDefinitionId(String scopeDefinitionId);
    
    void setScopeType(String scopeType);

    void setTenantId(String tenantId);
}
