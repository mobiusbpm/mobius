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
package mobius.cmmn.engine.impl;

import java.util.List;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.history.HistoricCaseInstanceQuery;
import mobius.cmmn.api.history.HistoricMilestoneInstanceQuery;
import mobius.cmmn.api.history.HistoricPlanItemInstanceQuery;
import mobius.cmmn.api.history.HistoricVariableInstanceQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.cmd.CmmnDeleteHistoricTaskLogEntryCmd;
import mobius.cmmn.engine.impl.cmd.DeleteHistoricCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.DeleteHistoricTaskInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetHistoricEntityLinkChildrenForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetHistoricEntityLinkParentsForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetHistoricIdentityLinksForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetHistoricIdentityLinksForTaskCmd;
import mobius.cmmn.engine.impl.history.CmmnHistoricVariableInstanceQueryImpl;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.identitylink.api.history.HistoricIdentityLink;
import mobius.task.api.history.NativeHistoricTaskLogEntryQuery;
import mobius.task.api.TaskInfo;
import mobius.task.api.history.HistoricTaskLogEntryBuilder;
import mobius.task.api.history.HistoricTaskLogEntryQuery;
import mobius.task.api.history.HistoricTaskInstanceQuery;
import mobius.task.service.impl.HistoricTaskInstanceQueryImpl;
import mobius.task.service.impl.NativeHistoricTaskLogEntryQueryImpl;
import mobius.task.service.impl.HistoricTaskLogEntryBuilderImpl;
import mobius.task.service.impl.HistoricTaskLogEntryQueryImpl;

/**
 *
 */
public class CmmnHistoryServiceImpl extends CommonEngineServiceImpl<CmmnEngineConfiguration> implements CmmnHistoryService {

    public CmmnHistoryServiceImpl(CmmnEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public HistoricCaseInstanceQuery createHistoricCaseInstanceQuery() {
        return configuration.getHistoricCaseInstanceEntityManager().createHistoricCaseInstanceQuery();
    }

    @Override
    public HistoricMilestoneInstanceQuery createHistoricMilestoneInstanceQuery() {
        return configuration.getHistoricMilestoneInstanceEntityManager().createHistoricMilestoneInstanceQuery();
    }
    
    @Override
    public HistoricVariableInstanceQuery createHistoricVariableInstanceQuery() {
        return new CmmnHistoricVariableInstanceQueryImpl(commandExecutor);
    }

    @Override
    public HistoricPlanItemInstanceQuery createHistoricPlanItemInstanceQuery() {
        return configuration.getHistoricPlanItemInstanceEntityManager().createHistoricPlanItemInstanceQuery();
    }

    @Override
    public void deleteHistoricCaseInstance(String caseInstanceId) {
        commandExecutor.execute(new DeleteHistoricCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public HistoricTaskInstanceQuery createHistoricTaskInstanceQuery() {
        return new HistoricTaskInstanceQueryImpl(commandExecutor);
    }
    
    @Override
    public void deleteHistoricTaskInstance(String taskId) {
        commandExecutor.execute(new DeleteHistoricTaskInstanceCmd(taskId));
    }
    
    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public List<HistoricIdentityLink> getHistoricIdentityLinksForTask(String taskId) {
        return commandExecutor.execute(new GetHistoricIdentityLinksForTaskCmd(taskId));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkChildrenForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetHistoricEntityLinkChildrenForCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public List<HistoricEntityLink> getHistoricEntityLinkParentsForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetHistoricEntityLinkParentsForCaseInstanceCmd(caseInstanceId));
    }


    @Override
    public void deleteHistoricTaskLogEntry(long logNumber) {
        commandExecutor.execute(new CmmnDeleteHistoricTaskLogEntryCmd(logNumber));
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
