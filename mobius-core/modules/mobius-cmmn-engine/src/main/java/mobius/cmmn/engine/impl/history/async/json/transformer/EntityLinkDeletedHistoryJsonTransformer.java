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

import static mobius.job.service.impl.history.async.util.AsyncHistoryJsonUtil.getStringFromJson;

import java.util.Collections;
import java.util.List;

import mobius.cmmn.engine.impl.history.async.CmmnAsyncHistoryConstants;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.entitylink.api.history.HistoricEntityLink;
import mobius.entitylink.api.history.HistoricEntityLinkService;
import mobius.job.service.impl.persistence.entity.HistoryJobEntity;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 *
 */
public class EntityLinkDeletedHistoryJsonTransformer extends AbstractHistoryJsonTransformer {

    @Override
    public List<String> getTypes() {
        return Collections.singletonList(CmmnAsyncHistoryConstants.TYPE_ENTITY_LINK_DELETED);
    }

    @Override
    public boolean isApplicable(ObjectNode historicalData, CommandContext commandContext) {
        return getHistoricEntityLink(historicalData, commandContext) != null;
    }
    
    protected HistoricEntityLink getHistoricEntityLink(ObjectNode historicalData, CommandContext commandContext) {
        return CommandContextUtil.getHistoricEntityLinkService(commandContext)
                .getHistoricEntityLink(getStringFromJson(historicalData, CmmnAsyncHistoryConstants.FIELD_ID));
    }

    @Override
    public void transformJson(HistoryJobEntity job, ObjectNode historicalData, CommandContext commandContext) {
        HistoricEntityLinkService historicEntityLinkService = CommandContextUtil.getHistoricEntityLinkService();
        HistoricEntityLink historicEntityLink = getHistoricEntityLink(historicalData, commandContext);
        if (historicEntityLink != null) {
            historicEntityLinkService.deleteHistoricEntityLink(historicEntityLink);
        }
    }

}
