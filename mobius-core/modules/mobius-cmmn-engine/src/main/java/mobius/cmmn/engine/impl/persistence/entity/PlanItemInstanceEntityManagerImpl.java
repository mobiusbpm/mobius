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

import mobius.cmmn.api.runtime.PlanItemDefinitionType;
import mobius.cmmn.api.runtime.PlanItemInstance;
import mobius.cmmn.api.runtime.PlanItemInstanceQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.data.PlanItemInstanceDataManager;
import mobius.cmmn.engine.impl.runtime.PlanItemInstanceQueryImpl;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemDefinition;
import mobius.common.engine.api.delegate.Expression;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.persistence.entity.data.DataManager;
import mobius.job.service.impl.persistence.entity.TimerJobEntity;
import mobius.job.service.impl.persistence.entity.TimerJobEntityManager;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntityManager;

import java.util.List;

/**
 *
 */
public class PlanItemInstanceEntityManagerImpl extends AbstractCmmnEntityManager<PlanItemInstanceEntity>
		implements PlanItemInstanceEntityManager {

    protected PlanItemInstanceDataManager planItemInstanceDataManager;
    
    public PlanItemInstanceEntityManagerImpl(CmmnEngineConfiguration cmmnEngineConfiguration, PlanItemInstanceDataManager planItemInstanceDataManager) {
        super(cmmnEngineConfiguration);
        this.planItemInstanceDataManager = planItemInstanceDataManager;
    }
    
    @Override
    protected DataManager<PlanItemInstanceEntity> getDataManager() {
        return planItemInstanceDataManager;
    }
    
    @Override
    public PlanItemInstanceEntity createChildPlanItemInstance(PlanItem planItem, String caseDefinitionId, String caseInstanceId,
            String stagePlanItemInstanceId, String tenantId, boolean addToParent) {
        
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        ExpressionManager expressionManager = cmmnEngineConfiguration.getExpressionManager();
        CaseInstanceEntity caseInstanceEntity = getCaseInstanceEntityManager().findById(caseInstanceId);
        
        PlanItemInstanceEntity planItemInstanceEntity = create();
        planItemInstanceEntity.setCaseDefinitionId(caseDefinitionId);
        planItemInstanceEntity.setCaseInstanceId(caseInstanceId);
        if (planItem.getName() != null) {
            Expression nameExpression = expressionManager.createExpression(planItem.getName());
            planItemInstanceEntity.setName(nameExpression.getValue(caseInstanceEntity).toString());
        }
        planItemInstanceEntity.setCreateTime(CommandContextUtil.getCmmnEngineConfiguration(commandContext).getClock().getCurrentTime());
        planItemInstanceEntity.setElementId(planItem.getId());
        PlanItemDefinition planItemDefinition = planItem.getPlanItemDefinition();
        if (planItemDefinition != null) {
            planItemInstanceEntity.setPlanItemDefinitionId(planItemDefinition.getId());

            String planItemDefinitionType = planItemDefinition.getClass().getSimpleName().toLowerCase();
            planItemInstanceEntity.setPlanItemDefinitionType(planItemDefinitionType);
            planItemInstanceEntity.setStage(PlanItemDefinitionType.STAGE.equals(planItemDefinitionType));
        } else {
            planItemInstanceEntity.setStage(false);
        }
        planItemInstanceEntity.setStageInstanceId(stagePlanItemInstanceId);
        planItemInstanceEntity.setTenantId(tenantId);
       
        insert(planItemInstanceEntity);
        
        if (addToParent) {
            addPlanItemInstanceToParent(commandContext, planItemInstanceEntity);
        }
        
        return planItemInstanceEntity;
    }
    
    protected void addPlanItemInstanceToParent(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        if (planItemInstanceEntity.getStageInstanceId() != null) {
            PlanItemInstanceEntity stagePlanItemInstanceEntity = CommandContextUtil.getPlanItemInstanceEntityManager(commandContext)
                    .findById(planItemInstanceEntity.getStageInstanceId());
            stagePlanItemInstanceEntity.getChildPlanItemInstances().add(planItemInstanceEntity);
        } else {
            CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCaseInstanceEntityManager(commandContext).findById(planItemInstanceEntity.getCaseInstanceId());
            caseInstanceEntity.getChildPlanItemInstances().add(planItemInstanceEntity);
        }
    }
    
    @Override
    public void deleteByCaseDefinitionId(String caseDefinitionId) {
        planItemInstanceDataManager.deleteByCaseDefinitionId(caseDefinitionId);
    }
    
    @Override
    public void deleteByStageInstanceId(String stageInstanceId) {
        planItemInstanceDataManager.deleteByStageInstanceId(stageInstanceId);
    }
    
    @Override
    public void deleteByCaseInstanceId(String caseInstanceId) {
        planItemInstanceDataManager.deleteByCaseInstanceId(caseInstanceId);
    }
    
    @Override
    public PlanItemInstanceQuery createPlanItemInstanceQuery() {
        return new PlanItemInstanceQueryImpl(cmmnEngineConfiguration.getCommandExecutor());
    }

    @Override
    public long countByCriteria(PlanItemInstanceQuery planItemInstanceQuery) {
        return planItemInstanceDataManager.countByCriteria((PlanItemInstanceQueryImpl) planItemInstanceQuery);
    }

    @Override
    public List<PlanItemInstance> findByCriteria(PlanItemInstanceQuery planItemInstanceQuery) {
        return planItemInstanceDataManager.findByCriteria((PlanItemInstanceQueryImpl) planItemInstanceQuery);
    }
    
    @Override
    public List<PlanItemInstanceEntity> findByCaseInstanceId(String caseInstanceId) {
        return planItemInstanceDataManager.findByCaseInstanceId(caseInstanceId);
    }

    @Override
    public List<PlanItemInstanceEntity> findByCaseInstanceIdAndPlanItemId(String caseInstanceId, String planitemId) {
        return planItemInstanceDataManager.findByCaseInstanceIdAndPlanItemId(caseInstanceId, planitemId);
    }

    @Override
    public void delete(PlanItemInstanceEntity planItemInstanceEntity, boolean fireEvent) {
        CommandContext commandContext = CommandContextUtil.getCommandContext();
        
        CountingPlanItemInstanceEntity countingPlanItemInstanceEntity = (CountingPlanItemInstanceEntity) planItemInstanceEntity;
        
        // Variables
        if (countingPlanItemInstanceEntity.getVariableCount() > 0) {
            VariableInstanceEntityManager variableInstanceEntityManager
                = CommandContextUtil.getVariableServiceConfiguration(commandContext).getVariableInstanceEntityManager();
            List<VariableInstanceEntity> variableInstanceEntities = variableInstanceEntityManager
                    .findVariableInstanceBySubScopeIdAndScopeType(planItemInstanceEntity.getId(), ScopeTypes.CMMN);
            for (VariableInstanceEntity variableInstanceEntity : variableInstanceEntities) {
                variableInstanceEntityManager.delete(variableInstanceEntity);
            }
        }
        
        if (planItemInstanceEntity.isStage()) {
            if (planItemInstanceEntity.getChildPlanItemInstances() != null && !planItemInstanceEntity.getChildPlanItemInstances().isEmpty()) {
                for (PlanItemInstanceEntity childPlanItem : planItemInstanceEntity.getChildPlanItemInstances()) {
                    delete(childPlanItem, fireEvent);
                }
            }
        }

        if (planItemInstanceEntity.getPlanItemDefinitionType().equals(PlanItemDefinitionType.TIMER_EVENT_LISTENER)) {
            TimerJobEntityManager timerJobEntityManager = CommandContextUtil.getCmmnEngineConfiguration(commandContext).getJobServiceConfiguration().getTimerJobEntityManager();
            List<TimerJobEntity> timerJobsEntities = timerJobEntityManager
                .findJobsByScopeIdAndSubScopeId(planItemInstanceEntity.getCaseInstanceId(), planItemInstanceEntity.getId());
            for (TimerJobEntity timerJobEntity : timerJobsEntities) {
                timerJobEntityManager.delete(timerJobEntity);
            }
        }
        
        getDataManager().delete(planItemInstanceEntity);
    }
    
}
