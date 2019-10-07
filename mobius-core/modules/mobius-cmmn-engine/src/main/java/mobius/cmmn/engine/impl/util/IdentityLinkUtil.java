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
package mobius.cmmn.engine.impl.util;

import java.util.List;

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.identity.Authentication;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import mobius.task.api.history.HistoricTaskLogEntryType;
import mobius.task.service.TaskServiceConfiguration;
import mobius.task.service.impl.BaseHistoricTaskLogEntryBuilderImpl;
import mobius.task.service.impl.persistence.CountingTaskEntity;
import mobius.task.service.impl.persistence.entity.TaskEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class IdentityLinkUtil {

    public static IdentityLinkEntity createCaseInstanceIdentityLink(CaseInstance caseInstance, String userId, String groupId, String type) {
        IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createScopeIdentityLink(
                        null, caseInstance.getId(), ScopeTypes.CMMN, userId, groupId, type);
        
        CommandContextUtil.getCmmnHistoryManager().recordIdentityLinkCreated(identityLinkEntity);
        
        return identityLinkEntity;
    }
    
    public static void deleteTaskIdentityLinks(TaskEntity taskEntity, String userId, String groupId, String type) {
        List<IdentityLinkEntity> removedIdentityLinkEntities = CommandContextUtil.getIdentityLinkService().deleteTaskIdentityLink(
                        taskEntity.getId(), taskEntity.getIdentityLinks(), userId, groupId, type);
        handleTaskIdentityLinkDeletions(taskEntity, removedIdentityLinkEntities, true);
    }

    public static void deleteCaseInstanceIdentityLinks(CaseInstance caseInstance, String userId, String groupId, String type) {
        List<IdentityLinkEntity> removedIdentityLinkEntities = CommandContextUtil.getIdentityLinkService().deleteScopeIdentityLink(
                        caseInstance.getId(), ScopeTypes.CMMN, userId, groupId, type);
        
        for (IdentityLinkEntity identityLinkEntity : removedIdentityLinkEntities) {
            CommandContextUtil.getCmmnHistoryManager().recordIdentityLinkDeleted(identityLinkEntity);
        }
    }

    public static void handleTaskIdentityLinkAdditions(TaskEntity taskEntity, List<IdentityLinkEntity> identityLinkEntities) {
        for (IdentityLinkEntity identityLinkEntity : identityLinkEntities) {
            handleTaskIdentityLinkAddition(taskEntity, identityLinkEntity);
        }
    }

    public static void handleTaskIdentityLinkAddition(TaskEntity taskEntity, IdentityLinkEntity identityLinkEntity) {
        CommandContextUtil.getCmmnHistoryManager().recordIdentityLinkCreated(identityLinkEntity);

        CountingTaskEntity countingTaskEntity = (CountingTaskEntity) taskEntity;
        if (countingTaskEntity.isCountEnabled()) {
            countingTaskEntity.setIdentityLinkCount(countingTaskEntity.getIdentityLinkCount() + 1);
        }

        logTaskIdentityLinkEvent(HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_ADDED.name(), taskEntity, identityLinkEntity);

        taskEntity.getIdentityLinks().add(identityLinkEntity);
        
        CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration();
        if (cmmnEngineConfiguration.getIdentityLinkInterceptor() != null) {
            cmmnEngineConfiguration.getIdentityLinkInterceptor().handleAddIdentityLinkToTask(taskEntity, identityLinkEntity);
        }
    }

    public static void handleTaskIdentityLinkDeletions(TaskEntity taskEntity, List<IdentityLinkEntity> identityLinks, boolean cascaseHistory) {
        for (IdentityLinkEntity identityLinkEntity : identityLinks) {
            CountingTaskEntity countingTaskEntity = (CountingTaskEntity) taskEntity;
            if (countingTaskEntity.isCountEnabled()) {
                countingTaskEntity.setIdentityLinkCount(countingTaskEntity.getIdentityLinkCount() - 1);
            }
            if (cascaseHistory) {
                CommandContextUtil.getCmmnHistoryManager().recordIdentityLinkDeleted(identityLinkEntity);
            }
            logTaskIdentityLinkEvent(HistoricTaskLogEntryType.USER_TASK_IDENTITY_LINK_REMOVED.name(), taskEntity, identityLinkEntity);
        }

        taskEntity.getIdentityLinks().removeAll(identityLinks);
    }

    protected static void logTaskIdentityLinkEvent(String eventType, TaskEntity taskEntity, IdentityLinkEntity identityLinkEntity) {
        TaskServiceConfiguration taskServiceConfiguration = CommandContextUtil.getTaskServiceConfiguration();
        if (taskServiceConfiguration.isEnableHistoricTaskLogging()) {
            BaseHistoricTaskLogEntryBuilderImpl taskLogEntryBuilder = new BaseHistoricTaskLogEntryBuilderImpl(taskEntity);
            ObjectNode data = CommandContextUtil.getTaskServiceConfiguration().getObjectMapper().createObjectNode();
            if (identityLinkEntity.isUser()) {
                data.put("userId", identityLinkEntity.getUserId());
            } else if (identityLinkEntity.isGroup()) {
                data.put("groupId", identityLinkEntity.getGroupId());
            }
            data.put("type", identityLinkEntity.getType());
            taskLogEntryBuilder.timeStamp(taskServiceConfiguration.getClock().getCurrentTime());
            taskLogEntryBuilder.userId(Authentication.getAuthenticatedUserId());
            taskLogEntryBuilder.data(data.toString());
            taskLogEntryBuilder.type(eventType);
            taskServiceConfiguration.getInternalHistoryTaskManager().recordHistoryUserTaskLog(taskLogEntryBuilder);
        }
    }

}