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
package mobius.cmmn.engine.impl.history.async.json.transformer;

import com.fasterxml.jackson.databind.node.ObjectNode;
import mobius.cmmn.engine.impl.history.async.CmmnAsyncHistoryConstants;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntity;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;

/**
 * @author Joram Barrez
 */
@Deprecated
public class PlanItemInstanceStartedHistoryJsonTransformer extends AbstractNeedsHistoricPlanItemInstanceHistoryJsonTransformer {
    
    @Override
    public List<String> getTypes() {
        return Collections.singletonList(CmmnAsyncHistoryConstants.TYPE_PLAN_ITEM_INSTANCE_STARTED);
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricPlanItemInstanceEntity historicPlanItemInstanceEntity = updateCommonProperties(historicalData, commandContext);
        
        Date lastStartedTime = getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_LAST_STARTED_TIME);
        if (historicPlanItemInstanceEntity.getLastStartedTime() == null 
                || historicPlanItemInstanceEntity.getLastStartedTime().before(lastStartedTime)) {
            historicPlanItemInstanceEntity.setLastStartedTime(lastStartedTime);
        }
    }

}
