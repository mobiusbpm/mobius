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
package mobius.cmmn.api;

import java.util.List;

import mobius.cmmn.api.history.HistoricCaseInstanceQuery;
import mobius.cmmn.api.history.HistoricMilestoneInstanceQuery;
import mobius.cmmn.api.history.HistoricPlanItemInstanceQuery;
import mobius.cmmn.api.history.HistoricVariableInstanceQuery;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.identitylink.api.IdentityLink;
import mobius.identitylink.api.history.HistoricIdentityLink;
import mobius.task.api.history.NativeHistoricTaskLogEntryQuery;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskLogEntry;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.task.api.history.HistoricTaskInstanceQuery;

/**
 *
 */
public interface CmmnHistoryService {
    
    HistoricCaseInstanceQuery createHistoricCaseInstanceQuery();

    HistoricMilestoneInstanceQuery createHistoricMilestoneInstanceQuery();
    
    HistoricVariableInstanceQuery createHistoricVariableInstanceQuery();
    
    HistoricTaskInstanceQuery createHistoricTaskInstanceQuery();

    HistoricPlanItemInstanceQuery createHistoricPlanItemInstanceQuery();

    void deleteHistoricCaseInstance(String caseInstanceId);
    
    /**
     * Deletes historic task instance. This might be useful for tasks that are {@link CmmnTaskService#newTask() dynamically created} and then {@link CmmnTaskService#complete(String) completed}. If the
     * historic task instance doesn't exist, no exception is thrown and the method returns normal.
     */
    void deleteHistoricTaskInstance(String taskId);
    
    /**
     * Retrieves the {@link HistoricIdentityLink}s associated with the given task. Such an {@link IdentityLink} informs how a certain identity (eg. group or user) is associated with a certain task
     * (eg. as candidate, assignee, etc.), even if the task is completed as opposed to {@link IdentityLink}s which only exist for active tasks.
     */
    List<HistoricIdentityLink> getHistoricIdentityLinksForTask(String taskId);

    /**
     * Retrieves the {@link HistoricIdentityLink}s associated with the given case instance. Such an {@link IdentityLink} informs how a certain identity (eg. group or user) is associated with a
     * certain case instance, even if the instance is completed as opposed to {@link IdentityLink}s which only exist for active instances.
     */
    List<HistoricIdentityLink> getHistoricIdentityLinksForCaseInstance(String caseInstanceId);
    
    /**
     * Retrieves the {@link HistoricEntityLink}s associated with the given case instance.
     */
    List<HistoricEntityLink> getHistoricEntityLinkChildrenForCaseInstance(String caseInstanceId);

    /**
     * Retrieves the {@link HistoricEntityLink}s where the given case instance is referenced.
     */
    List<HistoricEntityLink> getHistoricEntityLinkParentsForCaseInstance(String caseInstanceId);

    /**
     * Deletes user task log entry by its log number
     *
     * @param logNumber user task log entry identifier
     */
    void deleteHistoricTaskLogEntry(long logNumber);

    /**
     * Create new task log entry builder to the log task event
     *
     * @param task to which is log related to
     */
    HistoricTaskLogEntryBuilder createHistoricTaskLogEntryBuilder(TaskInfo task);

    /**
     * Create new task log entry builder to the log task event without predefined values from the task
     *
     */
    HistoricTaskLogEntryBuilder createHistoricTaskLogEntryBuilder();

    /**
     * Returns a new {@link HistoricTaskLogEntryQuery} that can be used to dynamically query task log entries.
     */
    HistoricTaskLogEntryQuery createHistoricTaskLogEntryQuery();

    /**
     * Returns a new {@link NativeHistoricTaskLogEntryQuery} for {@link HistoricTaskLogEntry}s.
     */
    NativeHistoricTaskLogEntryQuery createNativeHistoricTaskLogEntryQuery();

}
