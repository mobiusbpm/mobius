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
package mobius.cmmn.engine.impl.history;

import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.MilestoneInstanceEntity;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.entitylink.service.impl.persistence.entity.EntityLinkEntity;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.service.impl.persistence.entity.TaskEntity;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;

import java.util.Date;

/**
 *
 */
public interface CmmnHistoryManager {

    void recordCaseInstanceStart(CaseInstanceEntity caseInstanceEntity);

    void recordCaseInstanceEnd(CaseInstanceEntity caseInstanceEntity, String state, Date endTime);
    
    void recordUpdateCaseInstanceName(CaseInstanceEntity caseInstanceEntity, String name);

    void recordUpdateBusinessKey(CaseInstanceEntity caseInstanceEntity, String businessKey);

    void recordMilestoneReached(MilestoneInstanceEntity milestoneInstanceEntity);

    void recordHistoricCaseInstanceDeleted(String caseInstanceId);

    void recordIdentityLinkCreated(IdentityLinkEntity identityLink);

    void recordIdentityLinkDeleted(IdentityLinkEntity identityLink);
    
    void recordEntityLinkCreated(EntityLinkEntity entityLink);

    void recordEntityLinkDeleted(EntityLinkEntity entityLink);

    void recordVariableCreate(VariableInstanceEntity variable, Date createTime);

    void recordVariableUpdate(VariableInstanceEntity variable, Date updateTime);

    void recordVariableRemoved(VariableInstanceEntity variable);

    void recordTaskCreated(TaskEntity task);

    void recordTaskEnd(TaskEntity task, String deleteReason, Date endTime);

    void recordTaskInfoChange(TaskEntity taskEntity, Date changeTime);

    void recordPlanItemInstanceCreated(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceAvailable(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceEnabled(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceDisabled(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceStarted(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceSuspended(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceCompleted(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceOccurred(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceTerminated(PlanItemInstanceEntity planItemInstanceEntity);

    void recordPlanItemInstanceExit(PlanItemInstanceEntity planItemInstanceEntity);

    /**
     * Record historic user task log entry
     * @param taskLogEntryBuilder historic user task log entry description
     */
    void recordHistoricUserTaskLogEntry(HistoricTaskLogEntryBuilder taskLogEntryBuilder);

    /**
     * Delete historic user task log entry
     * @param logNumber log identifier
     */
    void deleteHistoricUserTaskLogEntry(long logNumber);
}
