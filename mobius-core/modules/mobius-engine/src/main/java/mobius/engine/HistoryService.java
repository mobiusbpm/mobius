/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mobius.engine;

import java.util.List;

import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.history.HistoricActivityInstanceQuery;
import mobius.engine.history.HistoricDetail;
import mobius.engine.history.HistoricDetailQuery;
import mobius.engine.history.HistoricProcessInstance;
import mobius.engine.history.HistoricProcessInstanceQuery;
import mobius.engine.history.NativeHistoricActivityInstanceQuery;
import mobius.engine.history.NativeHistoricDetailQuery;
import mobius.engine.history.NativeHistoricProcessInstanceQuery;
import mobius.engine.history.ProcessInstanceHistoryLog;
import mobius.engine.history.ProcessInstanceHistoryLogQuery;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.identitylink.api.IdentityLink;
import mobius.identitylink.api.history.HistoricIdentityLink;
import mobius.task.api.history.NativeHistoricTaskLogEntryQuery;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskLogEntry;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.task.api.history.HistoricTaskInstance;
import mobius.task.api.history.HistoricTaskInstanceQuery;
import mobius.task.service.history.NativeHistoricTaskInstanceQuery;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.api.history.HistoricVariableInstanceQuery;
import mobius.variable.api.history.NativeHistoricVariableInstanceQuery;

/**
 * Service exposing information about ongoing and past process instances. This is different from the runtime information in the sense that this runtime information only contains the actual runtime
 * state at any given moment and it is optimized for runtime process execution performance. The history information is optimized for easy querying and remains permanent in the persistent storage.
 * 
 * @author Christian Stettler
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public interface HistoryService {

    /**
     * Creates a new programmatic query to search for {@link HistoricProcessInstance}s.
     */
    HistoricProcessInstanceQuery createHistoricProcessInstanceQuery();

    /**
     * Creates a new programmatic query to search for {@link HistoricActivityInstance}s.
     */
    HistoricActivityInstanceQuery createHistoricActivityInstanceQuery();

    /**
     * Creates a new programmatic query to search for {@link HistoricTaskInstance}s.
     */
    HistoricTaskInstanceQuery createHistoricTaskInstanceQuery();

    /** Creates a new programmatic query to search for {@link HistoricDetail}s. */
    HistoricDetailQuery createHistoricDetailQuery();

    /**
     * Returns a new {@link mobius.common.engine.api.query.NativeQuery} for process definitions.
     */
    NativeHistoricDetailQuery createNativeHistoricDetailQuery();

    /**
     * Creates a new programmatic query to search for {@link HistoricVariableInstance}s.
     */
    HistoricVariableInstanceQuery createHistoricVariableInstanceQuery();

    /**
     * Returns a new {@link mobius.common.engine.api.query.NativeQuery} for process definitions.
     */
    NativeHistoricVariableInstanceQuery createNativeHistoricVariableInstanceQuery();

    /**
     * Deletes historic task instance. This might be useful for tasks that are {@link TaskService#newTask() dynamically created} and then {@link TaskService#complete(String) completed}. If the
     * historic task instance doesn't exist, no exception is thrown and the method returns normal.
     */
    void deleteHistoricTaskInstance(String taskId);

    /**
     * Deletes historic process instance. All historic activities, historic task and historic details (variable updates, form properties) are deleted as well.
     */
    void deleteHistoricProcessInstance(String processInstanceId);

    /**
     * creates a native query to search for {@link HistoricProcessInstance}s via SQL
     */
    NativeHistoricProcessInstanceQuery createNativeHistoricProcessInstanceQuery();

    /**
     * creates a native query to search for {@link HistoricTaskInstance}s via SQL
     */
    NativeHistoricTaskInstanceQuery createNativeHistoricTaskInstanceQuery();

    /**
     * creates a native query to search for {@link HistoricActivityInstance}s via SQL
     */
    NativeHistoricActivityInstanceQuery createNativeHistoricActivityInstanceQuery();

    /**
     * Retrieves the {@link HistoricIdentityLink}s associated with the given task. Such an {@link IdentityLink} informs how a certain identity (eg. group or user) is associated with a certain task
     * (eg. as candidate, assignee, etc.), even if the task is completed as opposed to {@link IdentityLink}s which only exist for active tasks.
     */
    List<HistoricIdentityLink> getHistoricIdentityLinksForTask(String taskId);

    /**
     * Retrieves the {@link HistoricIdentityLink}s associated with the given process instance. Such an {@link IdentityLink} informs how a certain identity (eg. group or user) is associated with a
     * certain process instance, even if the instance is completed as opposed to {@link IdentityLink}s which only exist for active instances.
     */
    List<HistoricIdentityLink> getHistoricIdentityLinksForProcessInstance(String processInstanceId);
    
    /**
     * Retrieves the {@link HistoricEntityLink}s associated with the given process instance.
     */
    List<HistoricEntityLink> getHistoricEntityLinkChildrenForProcessInstance(String processInstanceId);

    /**
     * Retrieves the {@link HistoricEntityLink}s associated with the given task.
     */
    List<HistoricEntityLink> getHistoricEntityLinkChildrenForTask(String taskId);

    /**
     * Retrieves the {@link HistoricEntityLink}s where the given process instance is referenced.
     */
    List<HistoricEntityLink> getHistoricEntityLinkParentsForProcessInstance(String processInstanceId);

    /**
     * Retrieves the {@link HistoricEntityLink}s where the given task is referenced.
     */
    List<HistoricEntityLink> getHistoricEntityLinkParentsForTask(String taskId);

    /**
     * Allows to retrieve the {@link ProcessInstanceHistoryLog} for one process instance.
     */
    ProcessInstanceHistoryLogQuery createProcessInstanceHistoryLogQuery(String processInstanceId);

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
