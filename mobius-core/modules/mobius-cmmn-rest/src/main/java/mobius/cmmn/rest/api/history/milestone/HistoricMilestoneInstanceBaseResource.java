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

package mobius.cmmn.rest.api.history.milestone;

import static mobius.common.rest.api.PaginateListUtil.paginateList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.history.HistoricMilestoneInstanceQuery;
import mobius.cmmn.engine.impl.runtime.MilestoneInstanceQueryProperty;
import mobius.cmmn.rest.api.CmmnRestApiInterceptor;
import mobius.cmmn.rest.api.CmmnRestResponseFactory;
import mobius.common.engine.api.query.QueryProperty;
import mobius.common.rest.api.DataResponse;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Tijs Rademakers
 * @author Dennis Federico
 */
public abstract class HistoricMilestoneInstanceBaseResource {

    private static Map<String, QueryProperty> allowedSortProperties = new HashMap<>();

    static {
        allowedSortProperties.put("milestoneName", MilestoneInstanceQueryProperty.MILESTONE_NAME);
        allowedSortProperties.put("timestamp", MilestoneInstanceQueryProperty.MILESTONE_TIMESTAMP);
    }

    @Autowired
    protected CmmnRestResponseFactory restResponseFactory;

    @Autowired
    protected CmmnHistoryService historyService;
    
    @Autowired(required=false)
    protected CmmnRestApiInterceptor restApiInterceptor;

    protected DataResponse<HistoricMilestoneInstanceResponse> getQueryResponse(HistoricMilestoneInstanceQueryRequest queryRequest, Map<String, String> allRequestParams) {
        HistoricMilestoneInstanceQuery query = historyService.createHistoricMilestoneInstanceQuery();

        Optional.ofNullable(queryRequest.getId()).ifPresent(query::milestoneInstanceId);
        Optional.ofNullable(queryRequest.getName()).ifPresent(query::milestoneInstanceName);
        Optional.ofNullable(queryRequest.getCaseInstanceId()).ifPresent(query::milestoneInstanceCaseInstanceId);
        Optional.ofNullable(queryRequest.getCaseDefinitionId()).ifPresent(query::milestoneInstanceCaseInstanceId);
        Optional.ofNullable(queryRequest.getReachedBefore()).ifPresent(query::milestoneInstanceReachedBefore);
        Optional.ofNullable(queryRequest.getReachedAfter()).ifPresent(query::milestoneInstanceReachedAfter);
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessHistoryMilestoneInfoWithQuery(query, queryRequest);
        }

        return paginateList(allRequestParams, queryRequest, query, "timestamp", allowedSortProperties,
            restResponseFactory::createHistoricMilestoneInstanceResponseList);
    }

}
