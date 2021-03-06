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

package mobius.rest.service.api.history;

import static mobius.common.rest.api.PaginateListUtil.paginateList;

import java.util.HashMap;
import java.util.Map;

import mobius.rest.service.api.BpmnRestApiInterceptor;
import mobius.rest.service.api.RestResponseFactory;
import mobius.common.engine.api.query.QueryProperty;
import mobius.common.rest.api.DataResponse;
import mobius.engine.HistoryService;
import mobius.engine.history.HistoricActivityInstanceQuery;
import mobius.engine.impl.HistoricActivityInstanceQueryProperty;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class HistoricActivityInstanceBaseResource {

    private static Map<String, QueryProperty> allowedSortProperties = new HashMap<>();

    static {
        allowedSortProperties.put("activityId", HistoricActivityInstanceQueryProperty.ACTIVITY_ID);
        allowedSortProperties.put("activityName", HistoricActivityInstanceQueryProperty.ACTIVITY_NAME);
        allowedSortProperties.put("activityType", HistoricActivityInstanceQueryProperty.ACTIVITY_TYPE);
        allowedSortProperties.put("duration", HistoricActivityInstanceQueryProperty.DURATION);
        allowedSortProperties.put("endTime", HistoricActivityInstanceQueryProperty.END);
        allowedSortProperties.put("executionId", HistoricActivityInstanceQueryProperty.EXECUTION_ID);
        allowedSortProperties.put("activityInstanceId", HistoricActivityInstanceQueryProperty.HISTORIC_ACTIVITY_INSTANCE_ID);
        allowedSortProperties.put("processDefinitionId", HistoricActivityInstanceQueryProperty.PROCESS_DEFINITION_ID);
        allowedSortProperties.put("processInstanceId", HistoricActivityInstanceQueryProperty.PROCESS_INSTANCE_ID);
        allowedSortProperties.put("startTime", HistoricActivityInstanceQueryProperty.START);
        allowedSortProperties.put("tenantId", HistoricActivityInstanceQueryProperty.TENANT_ID);
    }

    @Autowired
    protected RestResponseFactory restResponseFactory;

    @Autowired
    protected HistoryService historyService;
    
    @Autowired(required=false)
    protected BpmnRestApiInterceptor restApiInterceptor;

    protected DataResponse<HistoricActivityInstanceResponse> getQueryResponse(HistoricActivityInstanceQueryRequest queryRequest, Map<String, String> allRequestParams) {
        HistoricActivityInstanceQuery query = historyService.createHistoricActivityInstanceQuery();

        // Populate query based on request
        if (queryRequest.getActivityId() != null) {
            query.activityId(queryRequest.getActivityId());
        }

        if (queryRequest.getActivityInstanceId() != null) {
            query.activityInstanceId(queryRequest.getActivityInstanceId());
        }

        if (queryRequest.getActivityName() != null) {
            query.activityName(queryRequest.getActivityName());
        }

        if (queryRequest.getActivityType() != null) {
            query.activityType(queryRequest.getActivityType());
        }

        if (queryRequest.getExecutionId() != null) {
            query.executionId(queryRequest.getExecutionId());
        }

        if (queryRequest.getFinished() != null) {
            Boolean finished = queryRequest.getFinished();
            if (finished) {
                query.finished();
            } else {
                query.unfinished();
            }
        }

        if (queryRequest.getTaskAssignee() != null) {
            query.taskAssignee(queryRequest.getTaskAssignee());
        }

        if (queryRequest.getProcessInstanceId() != null) {
            query.processInstanceId(queryRequest.getProcessInstanceId());
        }

        if (queryRequest.getProcessDefinitionId() != null) {
            query.processDefinitionId(queryRequest.getProcessDefinitionId());
        }

        if (queryRequest.getTenantId() != null) {
            query.activityTenantId(queryRequest.getTenantId());
        }

        if (queryRequest.getTenantIdLike() != null) {
            query.activityTenantIdLike(queryRequest.getTenantIdLike());
        }

        if (Boolean.TRUE.equals(queryRequest.getWithoutTenantId())) {
            query.activityWithoutTenantId();
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessHistoryActivityInfoWithQuery(query, queryRequest);
        }

        return paginateList(allRequestParams, queryRequest, query, "startTime", allowedSortProperties,
            restResponseFactory::createHistoricActivityInstanceResponseList);
    }
}
