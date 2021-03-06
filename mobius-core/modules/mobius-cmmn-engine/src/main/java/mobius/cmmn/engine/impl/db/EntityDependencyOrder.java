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
package mobius.cmmn.engine.impl.db;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mobius.cmmn.engine.impl.persistence.entity.CaseDefinitionEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.CmmnDeploymentEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.CmmnResourceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.HistoricCaseInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.HistoricMilestoneInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.MilestoneInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntityImpl;
import mobius.cmmn.engine.impl.persistence.entity.SentryPartInstanceEntityImpl;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntityImpl;
import mobius.entitylink.service.impl.persistence.entity.HistoricEntityLinkEntityImpl;
import mobius.eventsubscription.service.impl.persistence.entity.CompensateEventSubscriptionEntityImpl;
import mobius.eventsubscription.service.impl.persistence.entity.EventSubscriptionEntityImpl;
import mobius.eventsubscription.service.impl.persistence.entity.MessageEventSubscriptionEntityImpl;
import mobius.eventsubscription.service.impl.persistence.entity.SignalEventSubscriptionEntityImpl;
import mobius.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntityImpl;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntityImpl;
import mobius.job.service.impl.persistence.entity.DeadLetterJobEntityImpl;
import mobius.job.service.impl.persistence.entity.HistoryJobEntityImpl;
import mobius.job.service.impl.persistence.entity.JobByteArrayEntityImpl;
import mobius.job.service.impl.persistence.entity.JobEntityImpl;
import mobius.job.service.impl.persistence.entity.SuspendedJobEntityImpl;
import mobius.job.service.impl.persistence.entity.TimerJobEntityImpl;
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntityImpl;
import mobius.variable.service.impl.persistence.entity.VariableByteArrayEntityImpl;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntityImpl;

/**
 *
 */
public class EntityDependencyOrder {

    public static List<Class<? extends Entity>> DELETE_ORDER = new ArrayList<>();
    public static List<Class<? extends Entity>> INSERT_ORDER;

    static {

        DELETE_ORDER.add(JobEntityImpl.class);
        DELETE_ORDER.add(TimerJobEntityImpl.class);
        DELETE_ORDER.add(SuspendedJobEntityImpl.class);
        DELETE_ORDER.add(DeadLetterJobEntityImpl.class);
        DELETE_ORDER.add(JobByteArrayEntityImpl.class);
        DELETE_ORDER.add(HistoryJobEntityImpl.class);
        DELETE_ORDER.add(HistoricEntityLinkEntityImpl.class);
        DELETE_ORDER.add(HistoricIdentityLinkEntityImpl.class);
        DELETE_ORDER.add(HistoricMilestoneInstanceEntityImpl.class);
        DELETE_ORDER.add(HistoricCaseInstanceEntityImpl.class);
        DELETE_ORDER.add(VariableInstanceEntityImpl.class);
        DELETE_ORDER.add(VariableByteArrayEntityImpl.class);
        DELETE_ORDER.add(HistoricVariableInstanceEntityImpl.class);
        DELETE_ORDER.add(SignalEventSubscriptionEntityImpl.class);
        DELETE_ORDER.add(MessageEventSubscriptionEntityImpl.class);
        DELETE_ORDER.add(CompensateEventSubscriptionEntityImpl.class);
        DELETE_ORDER.add(EventSubscriptionEntityImpl.class);
        DELETE_ORDER.add(EntityLinkEntityImpl.class);
        DELETE_ORDER.add(IdentityLinkEntityImpl.class);
        DELETE_ORDER.add(MilestoneInstanceEntityImpl.class);
        DELETE_ORDER.add(SentryPartInstanceEntityImpl.class);
        DELETE_ORDER.add(PlanItemInstanceEntityImpl.class);
        DELETE_ORDER.add(HistoricPlanItemInstanceEntityImpl.class);
        DELETE_ORDER.add(CaseInstanceEntityImpl.class);
        DELETE_ORDER.add(CaseDefinitionEntityImpl.class);
        DELETE_ORDER.add(CmmnResourceEntityImpl.class);
        DELETE_ORDER.add(CmmnDeploymentEntityImpl.class);
        
        INSERT_ORDER = new ArrayList<>(DELETE_ORDER);
        Collections.reverse(INSERT_ORDER);

    }
    
}
