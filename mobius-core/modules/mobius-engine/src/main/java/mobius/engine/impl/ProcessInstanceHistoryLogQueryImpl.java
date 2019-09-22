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
package mobius.engine.impl;

import java.util.List;

import mobius.common.engine.api.history.HistoricData;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.interceptor.CommandExecutor;
import mobius.engine.history.HistoricActivityInstance;
import mobius.engine.history.HistoricVariableUpdate;
import mobius.engine.history.ProcessInstanceHistoryLog;
import mobius.engine.history.ProcessInstanceHistoryLogQuery;
import mobius.engine.impl.persistence.entity.HistoricProcessInstanceEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.service.impl.HistoricVariableInstanceQueryImpl;
import mobius.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import mobius.variable.service.impl.types.CacheableVariable;
import mobius.variable.service.impl.types.JPAEntityListVariableType;
import mobius.variable.service.impl.types.JPAEntityVariableType;

/**
 * @author Joram Barrez
 */
public class ProcessInstanceHistoryLogQueryImpl implements ProcessInstanceHistoryLogQuery, Command<ProcessInstanceHistoryLog> {

    protected CommandExecutor commandExecutor;

    protected String processInstanceId;
    protected boolean includeTasks;
    protected boolean includeActivities;
    protected boolean includeVariables;
    protected boolean includeComments;
    protected boolean includeVariableUpdates;
    protected boolean includeFormProperties;

    public ProcessInstanceHistoryLogQueryImpl(CommandExecutor commandExecutor, String processInstanceId) {
        this.commandExecutor = commandExecutor;
        this.processInstanceId = processInstanceId;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeTasks() {
        this.includeTasks = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeComments() {
        this.includeComments = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeActivities() {
        this.includeActivities = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeVariables() {
        this.includeVariables = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeVariableUpdates() {
        this.includeVariableUpdates = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLogQuery includeFormProperties() {
        this.includeFormProperties = true;
        return this;
    }

    @Override
    public ProcessInstanceHistoryLog singleResult() {
        return commandExecutor.execute(this);
    }

    @Override
    public ProcessInstanceHistoryLog execute(CommandContext commandContext) {

        // Fetch historic process instance
        HistoricProcessInstanceEntity historicProcessInstance = CommandContextUtil.getHistoricProcessInstanceEntityManager(commandContext).findById(processInstanceId);

        if (historicProcessInstance == null) {
            return null;
        }

        // Create a log using this historic process instance
        ProcessInstanceHistoryLogImpl processInstanceHistoryLog = new ProcessInstanceHistoryLogImpl(historicProcessInstance);

        // Add events, based on query settings

        // Tasks
        if (includeTasks) {
            List<? extends HistoricData> tasks = CommandContextUtil.getHistoricTaskService().findHistoricTaskInstancesByQueryCriteria(
                            new HistoricTaskInstanceQueryImpl(commandExecutor).processInstanceId(processInstanceId));
            processInstanceHistoryLog.addHistoricData(tasks);
        }

        // Activities
        if (includeActivities) {
            List<HistoricActivityInstance> activities = CommandContextUtil.getHistoricActivityInstanceEntityManager(commandContext).findHistoricActivityInstancesByQueryCriteria(
                    new HistoricActivityInstanceQueryImpl(commandExecutor).processInstanceId(processInstanceId));
            processInstanceHistoryLog.addHistoricData(activities);
        }

        // Variables
        if (includeVariables) {
            List<HistoricVariableInstance> variables = CommandContextUtil.getHistoricVariableService().findHistoricVariableInstancesByQueryCriteria(
                    new HistoricVariableInstanceQueryImpl(commandExecutor).processInstanceId(processInstanceId));

            // Make sure all variables values are fetched (similar to the HistoricVariableInstance query)
            for (HistoricVariableInstance historicVariableInstance : variables) {
                historicVariableInstance.getValue();

                // make sure JPA entities are cached for later retrieval
                HistoricVariableInstanceEntity variableEntity = (HistoricVariableInstanceEntity) historicVariableInstance;
                if (JPAEntityVariableType.TYPE_NAME.equals(variableEntity.getVariableType().getTypeName()) || JPAEntityListVariableType.TYPE_NAME.equals(variableEntity.getVariableType().getTypeName())) {
                    ((CacheableVariable) variableEntity.getVariableType()).setForceCacheable(true);
                }
            }

            processInstanceHistoryLog.addHistoricData(variables);
        }

        // Comment
        if (includeComments) {
            List<? extends HistoricData> comments = CommandContextUtil.getCommentEntityManager(commandContext).findCommentsByProcessInstanceId(processInstanceId);
            processInstanceHistoryLog.addHistoricData(comments);
        }

        // Details: variables
        if (includeVariableUpdates) {
            List<? extends HistoricData> variableUpdates = CommandContextUtil.getHistoricDetailEntityManager(commandContext).findHistoricDetailsByQueryCriteria(
                    new HistoricDetailQueryImpl(commandExecutor).variableUpdates());

            // Make sure all variables values are fetched (similar to the HistoricVariableInstance query)
            for (HistoricData historicData : variableUpdates) {
                HistoricVariableUpdate variableUpdate = (HistoricVariableUpdate) historicData;
                variableUpdate.getValue();
            }

            processInstanceHistoryLog.addHistoricData(variableUpdates);
        }

        // Details: form properties
        if (includeFormProperties) {
            List<? extends HistoricData> formProperties = CommandContextUtil.getHistoricDetailEntityManager(commandContext).findHistoricDetailsByQueryCriteria(
                    new HistoricDetailQueryImpl(commandExecutor).formProperties());
            processInstanceHistoryLog.addHistoricData(formProperties);
        }

        // All events collected. Sort them by date.
        processInstanceHistoryLog.orderHistoricData();

        return processInstanceHistoryLog;
    }

}
