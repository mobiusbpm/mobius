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
package mobius.engine.impl.history.async.json.transformer;

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getDateFromJson;
import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

import java.util.Collections;
import java.util.List;

import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.history.async.HistoryJsonConstants;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.identitylink.service.HistoricIdentityLinkService;
import mobius.identitylink.api.IdentityLinkType;
import mobius.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class TaskOwnerChangedHistoryJsonTransformer extends AbstractNeedsTaskHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(HistoryJsonConstants.TYPE_TASK_OWNER_CHANGED);
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        String owner = getStringFromJson(historicalData, HistoryJsonConstants.OWNER);
        String taskId = getStringFromJson(historicalData, HistoryJsonConstants.ID);
        
        HistoricIdentityLinkService historicIdentityLinkService = CommandContextUtil.getHistoricIdentityLinkService();
        HistoricIdentityLinkEntity historicIdentityLinkEntity = historicIdentityLinkService.createHistoricIdentityLink();
        historicIdentityLinkEntity.setTaskId(taskId);
        historicIdentityLinkEntity.setType(IdentityLinkType.OWNER);
        historicIdentityLinkEntity.setUserId(owner);
        historicIdentityLinkEntity.setCreateTime(getDateFromJson(historicalData, HistoryJsonConstants.CREATE_TIME)); 
        historicIdentityLinkService.insertHistoricIdentityLink(historicIdentityLinkEntity, false);
    }

}
