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
package mobius.cmmn.engine.impl.persistence.entity;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.common.engine.impl.db.HasRevision;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.variable.api.delegate.VariableScope;

import java.util.Date;

/**
 *
 */
public interface CaseInstanceEntity extends Entity, EntityWithSentryPartInstances, VariableScope, HasRevision, PlanItemInstanceContainer, CaseInstance {

    void setBusinessKey(String businessKey);
    void setName(String name);
    void setParentId(String parentId);
    void setCaseDefinitionId(String caseDefinitionId);
    void setState(String state);
    void setStartTime(Date startTime);
    void setStartUserId(String startUserId);
    void setCallbackId(String callbackId);
    void setCallbackType(String callbackType);
    void setCompleteable(boolean completeable);
    void setTenantId(String tenantId);
}
