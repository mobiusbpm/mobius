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
package mobius.cmmn.engine.impl.agenda.operation;

import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.PlanItemTransition;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class EnablePlanItemInstanceOperation extends AbstractChangePlanItemInstanceStateOperation {

    protected String entryCriterionId;

    public EnablePlanItemInstanceOperation(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity, String entryCriterionId) {
        super(commandContext, planItemInstanceEntity);
        this.entryCriterionId = entryCriterionId;
    }
    
    @Override
    protected String getLifeCycleTransition() {
        return PlanItemTransition.ENABLE;
    }
    
    @Override
    protected String getNewState() {
        return PlanItemInstanceState.ENABLED;
    }
    
    @Override
    protected void internalExecute() {

        // Sentries are not needed to be kept around, as the plan item is being enabled
        removeSentryRelatedData();

        planItemInstanceEntity.setEntryCriterionId(entryCriterionId);
        planItemInstanceEntity.setLastEnabledTime(getCurrentTime(commandContext));
        CommandContextUtil.getCmmnHistoryManager(commandContext).recordPlanItemInstanceEnabled(planItemInstanceEntity);
    }

}
