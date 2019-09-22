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

import mobius.cmmn.engine.impl.history.async.CmmnAsyncHistoryConstants;
import mobius.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntity;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;

import java.util.Collections;
import java.util.List;

/**
 * @author Joram Barrez
 */
@Deprecated
public class PlanItemInstanceTerminatedHistoryJsonTransformer extends AbstractNeedsHistoricPlanItemInstanceHistoryJsonTransformer {
    
    @Override
    public List<String> getTypes() {
        return Collections.singletonList(CmmnAsyncHistoryConstants.TYPE_PLAN_ITEM_INSTANCE_TERMINATED);
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricPlanItemInstanceEntity historicPlanItemInstanceEntity = updateCommonProperties(historicalData, commandContext);
        historicPlanItemInstanceEntity.setTerminatedTime(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_TERMINATED_TIME));
        historicPlanItemInstanceEntity.setEndedTime(getDateFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_END_TIME));
    }

}
