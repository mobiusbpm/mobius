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

package mobius.engine.impl;

import java.util.List;

import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.engine.HistoryService;
import mobius.engine.history.HistoricActivityInstanceQuery;
import mobius.engine.history.HistoricDetailQuery;
import mobius.engine.history.HistoricProcessInstanceQuery;
import mobius.engine.history.NativeHistoricActivityInstanceQuery;
import mobius.engine.history.NativeHistoricDetailQuery;
import mobius.engine.history.NativeHistoricProcessInstanceQuery;
import mobius.engine.history.ProcessInstanceHistoryLogQuery;
import mobius.engine.impl.cfg.ProcessEngineConfigurationImpl;
import mobius.engine.impl.cmd.DeleteHistoricProcessInstanceCmd;
import mobius.engine.impl.cmd.DeleteHistoricTaskInstanceCmd;
import mobius.engine.impl.cmd.DeleteHistoricTaskLogEntryByLogNumberCmd;
import mobius.engine.impl.cmd.GetHistoricEntityLinkChildrenForProcessInstanceCmd;
import mobius.engine.impl.cmd.GetHistoricEntityLinkChildrenForTaskCmd;
import mobius.engine.impl.cmd.GetHistoricEntityLinkParentsForProcessInstanceCmd;
import mobius.engine.impl.cmd.GetHistoricEntityLinkParentsForTaskCmd;
import mobius.engine.impl.cmd.GetHistoricIdentityLinksForTaskCmd;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.identitylink.api.history.HistoricIdentityLink;
import mobius.task.api.history.NativeHistoricTaskLogEntryQuery;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.task.api.history.HistoricTaskInstanceQuery;
import mobius.task.service.history.NativeHistoricTaskInstanceQuery;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.NativeHistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.NativeHistoricTaskLogEntryQueryImpl;
import mobius.task.service.impl.HistoricTaskLogEntryBuilderImpl;
import mobius.task.service.impl.HistoricTaskLogEntryQueryImpl;
import mobius.variable.api.history.HistoricVariableInstanceQuery;
import mobius.variable.api.history.NativeHistoricVariableInstanceQuery;
import mobius.variable.service.impl.HistoricVariableInstanceQueryImpl;
import mobius.variable.service.impl.NativeHistoricVariableInstanceQueryImpl;

/**
 *
 * @author Bernd Ruecker (camunda)
 * @author Christian Stettler
 */
public class HistoryServiceImpl extends CommonEngineServiceImpl<ProcessEngineConfigurationImpl> implements HistoryService {

    public HistoryServiceImpl(ProcessEngineConfigurationImpl processEngineConfiguration) {
        super(processEngineConfiguration);
    }

    @Override
    public HistoricProcessInstanceQuery createHistoricProcessInstanceQuery() {
        return new HistoricProcessInstanceQueryImpl(commandExecutor);
    }

    @Override
    public HistoricActivityInstanceQuery createHistoricActivityInstanceQuery() {
        return new HistoricActivityInstanceQueryImpl(commandExecutor);
    }

    @Override
    public HistoricTaskInstanceQuery createHistoricTaskInstanceQuery() {
        return new HistoricTaskInstanceQueryImpl(commandExecutor, configuration.getDatabaseType());
    }

    @Override
    public HistoricDetailQuery createHistoricDetailQuery() {
        return new HistoricDetailQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricDetailQuery createNativeHistoricDetailQuery() {
        return new NativeHistoricDetailQueryImpl(commandExecutor);
    }

    @Override
    public HistoricVariableInstanceQuery createHistoricVariableInstanceQuery() {
        return new HistoricVariableInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricVariableInstanceQuery createNativeHistoricVariableInstanceQuery() {
        return new NativeHistoricVariableInstanceQueryImpl(commandExecutor);
    }

    @Override
    public void deleteHistoricTaskInstance(String taskId) {
        commandExecutor.execute(new DeleteHistoricTaskInstanceCmd(taskId));
    }

    @Override
    public void deleteHistoricProcessInstance(String processInstanceId) {
        commandExecutor.execute(new DeleteHistoricProcessInstanceCmd(processInstanceId));
    }

    @Override
    public NativeHistoricProcessInstanceQuery createNativeHistoricProcessInstanceQuery() {
        return new NativeHistoricProcessInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricTaskInstanceQuery createNativeHistoricTaskInstanceQuery() {
        return new NativeHistoricTaskInstanceQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricActivityInstanceQuery createNativeHistoricActivityInstanceQuery() {
        return new NativeHistoricActivityInstanceQueryImpl(commandExecutor);
    }

    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForProcessInstance(String processInstanceId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForTaskCmd(null, processInstanceId));
    }

    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForTask(String taskId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForTaskCmd(taskId, null));
    }
    
    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkChildrenForProcessInstance(String processInstanceId) {
        return commandExecutor.execute(new GetHistoricEntityLinkChildrenForProcessInstanceCmd(processInstanceId));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkChildrenForTask(String taskId) {
        return commandExecutor.execute(new GetHistoricEntityLinkChildrenForTaskCmd(taskId));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkParentsForProcessInstance(String processInstanceId) {
        return commandExecutor.execute(new GetHistoricEntityLinkParentsForProcessInstanceCmd(processInstanceId));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkParentsForTask(String taskId) {
        return commandExecutor.execute(new GetHistoricEntityLinkParentsForTaskCmd(taskId));
    }

    @Override
    public ProcessInstanceHistoryLogQuery createProcessInstanceHistoryLogQuery(String processInstanceId) {
        return new ProcessInstanceHistoryLogQueryImpl(commandExecutor, processInstanceId);
    }

    @Override
    public void deleteHistoricTaskLogEntry(long logNumber) {
        commandExecutor.execute(new DeleteHistoricTaskLogEntryByLogNumberCmd(logNumber));
    }

    @Override
    public HistoricTaskLogEntryBuilder createHistoricTaskLogEntryBuilder(TaskInfo task) {
        return new HistoricTaskLogEntryBuilderImpl(commandExecutor, task);
    }

    @Override
    public HistoricTaskLogEntryBuilder createHistoricTaskLogEntryBuilder() {
        return new HistoricTaskLogEntryBuilderImpl(commandExecutor);
    }

    @Override
    public HistoricTaskLogEntryQuery createHistoricTaskLogEntryQuery() {
        return new HistoricTaskLogEntryQueryImpl(commandExecutor);
    }

    @Override
    public NativeHistoricTaskLogEntryQuery createNativeHistoricTaskLogEntryQuery() {
        return new NativeHistoricTaskLogEntryQueryImpl(commandExecutor);
    }

}
