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
package mobius.cmmn.engine.impl.runtime;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.CaseInstanceBuilder;
import mobius.cmmn.api.runtime.CaseInstanceQuery;
import mobius.cmmn.api.runtime.ChangePlanItemStateBuilder;
import mobius.cmmn.api.runtime.GenericEventListenerInstanceQuery;
import mobius.cmmn.api.runtime.MilestoneInstanceQuery;
import mobius.cmmn.api.runtime.PlanItemInstanceQuery;
import mobius.cmmn.api.runtime.PlanItemInstanceTransitionBuilder;
import mobius.cmmn.api.runtime.SignalEventListenerInstanceQuery;
import mobius.cmmn.api.runtime.UserEventListenerInstanceQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.cmd.AddIdentityLinkForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.ChangePlanItemStateCmd;
import mobius.cmmn.engine.impl.cmd.CompleteCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.CompleteStagePlanItemInstanceCmd;
import mobius.cmmn.engine.impl.cmd.DeleteIdentityLinkForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.DisablePlanItemInstanceCmd;
import mobius.cmmn.engine.impl.cmd.EnablePlanItemInstanceCmd;
import mobius.cmmn.engine.impl.cmd.EvaluateCriteriaCmd;
import mobius.cmmn.engine.impl.cmd.GetEntityLinkChildrenForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetEntityLinkParentsForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetIdentityLinksForCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.GetLocalVariableCmd;
import mobius.cmmn.engine.impl.cmd.GetLocalVariablesCmd;
import mobius.cmmn.engine.impl.cmd.GetStartFormModelCmd;
import mobius.cmmn.engine.impl.cmd.GetVariableCmd;
import mobius.cmmn.engine.impl.cmd.GetVariablesCmd;
import mobius.cmmn.engine.impl.cmd.HasCaseInstanceVariableCmd;
import mobius.cmmn.engine.impl.cmd.RemoveLocalVariableCmd;
import mobius.cmmn.engine.impl.cmd.RemoveLocalVariablesCmd;
import mobius.cmmn.engine.impl.cmd.RemoveVariableCmd;
import mobius.cmmn.engine.impl.cmd.RemoveVariablesCmd;
import mobius.cmmn.engine.impl.cmd.SetCaseInstanceBusinessKeyCmd;
import mobius.cmmn.engine.impl.cmd.SetCaseInstanceNameCmd;
import mobius.cmmn.engine.impl.cmd.SetLocalVariableCmd;
import mobius.cmmn.engine.impl.cmd.SetLocalVariablesCmd;
import mobius.cmmn.engine.impl.cmd.SetVariableCmd;
import mobius.cmmn.engine.impl.cmd.SetVariablesCmd;
import mobius.cmmn.engine.impl.cmd.StartCaseInstanceAsyncCmd;
import mobius.cmmn.engine.impl.cmd.StartCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.StartPlanItemInstanceCmd;
import mobius.cmmn.engine.impl.cmd.TerminateCaseInstanceCmd;
import mobius.cmmn.engine.impl.cmd.TerminatePlanItemInstanceCmd;
import mobius.cmmn.engine.impl.cmd.TriggerPlanItemInstanceCmd;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.entitylink.api.EntityLink;
import mobius.eventsubscription.api.EventSubscriptionQuery;
import mobius.eventsubscription.service.impl.EventSubscriptionQueryImpl;
import mobius.form.api.FormInfo;
import mobius.identitylink.api.IdentityLink;

/**
 *
 */
public class CmmnRuntimeServiceImpl extends CommonEngineServiceImpl<CmmnEngineConfiguration> implements CmmnRuntimeService {

    public CmmnRuntimeServiceImpl(CmmnEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public CaseInstanceBuilder createCaseInstanceBuilder() {
        return new CaseInstanceBuilderImpl(this);
    }

    @Override
    public PlanItemInstanceTransitionBuilder createPlanItemInstanceTransitionBuilder(String planItemInstanceId) {
        return new PlanItemInstanceTransitionBuilderImpl(commandExecutor, planItemInstanceId);
    }

    public CaseInstance startCaseInstance(CaseInstanceBuilder caseInstanceBuilder) {
        return commandExecutor.execute(new StartCaseInstanceCmd(caseInstanceBuilder));
    }
    
    public CaseInstance startCaseInstanceAsync(CaseInstanceBuilder caseInstanceBuilder) {
        return commandExecutor.execute(new StartCaseInstanceAsyncCmd(caseInstanceBuilder));
    }

    @Override
    public FormInfo getStartFormModel(String caseDefinitionId, String caseInstanceId) {
        return commandExecutor.execute(new GetStartFormModelCmd(caseDefinitionId, caseInstanceId));
    }

    @Override 
    public void triggerPlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new TriggerPlanItemInstanceCmd(planItemInstanceId));
    }
    
    @Override
    public void enablePlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new EnablePlanItemInstanceCmd(planItemInstanceId));
    }
    
    @Override
    public void disablePlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new DisablePlanItemInstanceCmd(planItemInstanceId));
    }
    
    @Override
    public void completeStagePlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new CompleteStagePlanItemInstanceCmd(planItemInstanceId));
    }

    @Override
    public void completeStagePlanItemInstance(String planItemInstanceId, boolean force) {
        commandExecutor.execute(new CompleteStagePlanItemInstanceCmd(planItemInstanceId, true));
    }

    @Override
    public void startPlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new StartPlanItemInstanceCmd(planItemInstanceId));
    }
    
    @Override
    public void completeCaseInstance(String caseInstanceId) {
        commandExecutor.execute(new CompleteCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public void terminateCaseInstance(String caseInstanceId) {
        commandExecutor.execute(new TerminateCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public void terminatePlanItemInstance(String planItemInstanceId) {
        commandExecutor.execute(new TerminatePlanItemInstanceCmd(planItemInstanceId));
    }

    @Override
    public void evaluateCriteria(String caseInstanceId) {
        commandExecutor.execute(new EvaluateCriteriaCmd(caseInstanceId));
    }
    
    @Override
    public void completeGenericEventListenerInstance(String genericEventListenerInstanceId) {
        commandExecutor.execute(new TriggerPlanItemInstanceCmd(genericEventListenerInstanceId));
    }

    @Override
    public void completeUserEventListenerInstance(String userEventListenerInstanceId) {
        commandExecutor.execute(new TriggerPlanItemInstanceCmd(userEventListenerInstanceId));
    }

    @Override
    public Map<String, Object> getVariables(String caseInstanceId) {
        return commandExecutor.execute(new GetVariablesCmd(caseInstanceId));
    }
    
    @Override
    public Map<String, Object> getLocalVariables(String planItemInstanceId) {
        return commandExecutor.execute(new GetLocalVariablesCmd(planItemInstanceId));
    }

    @Override
    public Object getVariable(String caseInstanceId, String variableName) {
        return commandExecutor.execute(new GetVariableCmd(caseInstanceId, variableName));
    }
    
    @Override
    public Object getLocalVariable(String planItemInstanceId, String variableName) {
        return commandExecutor.execute(new GetLocalVariableCmd(planItemInstanceId, variableName));
    }
    
    @Override
    public boolean hasVariable(String caseInstanceId, String variableName) {
        return commandExecutor.execute(new HasCaseInstanceVariableCmd(caseInstanceId, variableName, false));
    }
    
    @Override
    public void setVariable(String caseInstanceId, String variableName, Object variableValue) {
        commandExecutor.execute(new SetVariableCmd(caseInstanceId, variableName, variableValue));
    }

    @Override
    public void setVariables(String caseInstanceId, Map<String, Object> variables) {
        commandExecutor.execute(new SetVariablesCmd(caseInstanceId, variables));
    }
    
    @Override
    public void setLocalVariable(String planItemInstanceId, String variableName, Object variableValue) {
        commandExecutor.execute(new SetLocalVariableCmd(planItemInstanceId, variableName, variableValue));
    }
    
    @Override
    public void setLocalVariables(String planItemInstanceId, Map<String, Object> variables) {
        commandExecutor.execute(new SetLocalVariablesCmd(planItemInstanceId, variables));
    }

    @Override
    public void removeVariable(String caseInstanceId, String variableName) {
        commandExecutor.execute(new RemoveVariableCmd(caseInstanceId, variableName));
    }
    
    @Override
    public void removeVariables(String caseInstanceId, Collection<String> variableNames) {
        commandExecutor.execute(new RemoveVariablesCmd(caseInstanceId, variableNames));
    }
    
    @Override
    public void removeLocalVariable(String planItemInstanceId, String variableName) {
        commandExecutor.execute(new RemoveLocalVariableCmd(planItemInstanceId, variableName));
    }
    
    @Override
    public void removeLocalVariables(String planItemInstanceId, Collection<String> variableNames) {
        commandExecutor.execute(new RemoveLocalVariablesCmd(planItemInstanceId, variableNames));
    }

    @Override
    public void setCaseInstanceName(String caseInstanceId, String caseName) {
        commandExecutor.execute(new SetCaseInstanceNameCmd(caseInstanceId, caseName));
    }

    @Override
    public CaseInstanceQuery createCaseInstanceQuery() {
        return configuration.getCaseInstanceEntityManager().createCaseInstanceQuery();
    }

    @Override
    public PlanItemInstanceQuery createPlanItemInstanceQuery() {
        return configuration.getPlanItemInstanceEntityManager().createPlanItemInstanceQuery();
    }

    @Override
    public MilestoneInstanceQuery createMilestoneInstanceQuery() {
        return configuration.getMilestoneInstanceEntityManager().createMilestoneInstanceQuery();
    }
    
    @Override
    public GenericEventListenerInstanceQuery createGenericEventListenerInstanceQuery() {
        return new GenericEventListenerInstanceQueryImpl(configuration.getCommandExecutor());
    }
    
    @Override
    public SignalEventListenerInstanceQuery createSignalEventListenerInstanceQuery() {
        return new SignalEventListenerInstanceQueryImpl(configuration.getCommandExecutor());
    }

    @Override
    public UserEventListenerInstanceQuery createUserEventListenerInstanceQuery() {
        return new UserEventListenerInstanceQueryImpl(configuration.getCommandExecutor());
    }
    
    @Override
    public EventSubscriptionQuery createEventSubscriptionQuery() {
        return new EventSubscriptionQueryImpl(configuration.getCommandExecutor());
    }

    @Override
    public void addUserIdentityLink(String caseInstanceId, String userId, String identityLinkType) {
        commandExecutor.execute(new AddIdentityLinkForCaseInstanceCmd(caseInstanceId, userId, null, identityLinkType));
    }

    @Override
    public void addGroupIdentityLink(String caseInstanceId, String groupId, String identityLinkType) {
        commandExecutor.execute(new AddIdentityLinkForCaseInstanceCmd(caseInstanceId, null, groupId, identityLinkType));
    }

    @Override
    public void deleteUserIdentityLink(String caseInstanceId, String userId, String identityLinkType) {
        commandExecutor.execute(new DeleteIdentityLinkForCaseInstanceCmd(caseInstanceId, userId, null, identityLinkType));
    }

    @Override
    public void deleteGroupIdentityLink(String caseInstanceId, String groupId, String identityLinkType) {
        commandExecutor.execute(new DeleteIdentityLinkForCaseInstanceCmd(caseInstanceId, null, groupId, identityLinkType));
    }

    @Override
    public List<IdentityLink> getIdentityLinksForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetIdentityLinksForCaseInstanceCmd(caseInstanceId));
    }
    
    @Override
    public List<EntityLink> getEntityLinkChildrenForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetEntityLinkChildrenForCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public List<EntityLink> getEntityLinkParentsForCaseInstance(String caseInstanceId) {
        return commandExecutor.execute(new GetEntityLinkParentsForCaseInstanceCmd(caseInstanceId));
    }

    @Override
    public ChangePlanItemStateBuilder createChangePlanItemStateBuilder() {
        return new ChangePlanItemStateBuilderImpl(this);
    }

    @Override
    public void updateBusinessKey(String caseInstanceId, String businessKey) {
        commandExecutor.execute(new SetCaseInstanceBusinessKeyCmd(caseInstanceId, businessKey));
    }

    public void changePlanItemState(ChangePlanItemStateBuilderImpl changePlanItemStateBuilder) {
        commandExecutor.execute(new ChangePlanItemStateCmd(changePlanItemStateBuilder));
    }
}
