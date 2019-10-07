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

package mobius.cmmn.rest.api.history.variable;

import static mobius.common.rest.api.PaginateListUtil.paginateList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.history.HistoricVariableInstanceQuery;
import mobius.cmmn.rest.api.CmmnRestApiInterceptor;
import mobius.cmmn.rest.api.CmmnRestResponseFactory;
import mobius.cmmn.rest.api.engine.variable.QueryVariable;
import mobius.cmmn.rest.api.engine.variable.RestVariable;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.query.QueryProperty;
import mobius.common.rest.api.DataResponse;
import mobius.variable.api.history.HistoricVariableInstance;
import mobius.variable.service.impl.HistoricVariableInstanceQueryProperty;
import mobius.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class HistoricVariableInstanceBaseResource {

    private static Map<String, QueryProperty> allowedSortProperties = new HashMap<>();

    static {
        allowedSortProperties.put("caseInstanceId", HistoricVariableInstanceQueryProperty.SCOPE_ID);
        allowedSortProperties.put("variableName", HistoricVariableInstanceQueryProperty.VARIABLE_NAME);
    }

    @Autowired
    protected CmmnRestResponseFactory restResponseFactory;

    @Autowired
    protected CmmnHistoryService historyService;
    
    @Autowired(required=false)
    protected CmmnRestApiInterceptor restApiInterceptor;

    protected DataResponse<HistoricVariableInstanceResponse> getQueryResponse(HistoricVariableInstanceQueryRequest queryRequest, Map<String, String> allRequestParams) {
        HistoricVariableInstanceQuery query = historyService.createHistoricVariableInstanceQuery();

        // Populate query based on request
        if (queryRequest.getExcludeTaskVariables() != null) {
            if (queryRequest.getExcludeTaskVariables()) {
                query.excludeTaskVariables();
            }
        }

        if (queryRequest.getTaskId() != null) {
            query.taskId(queryRequest.getTaskId());
        }

        if (queryRequest.getPlanItemInstanceId() != null) {
            query.planItemInstanceId(queryRequest.getPlanItemInstanceId());
        }

        if (queryRequest.getCaseInstanceId() != null) {
            query.caseInstanceId(queryRequest.getCaseInstanceId());
        }

        if (queryRequest.getVariableName() != null) {
            query.variableName(queryRequest.getVariableName());
        }

        if (queryRequest.getVariableNameLike() != null) {
            query.variableNameLike(queryRequest.getVariableNameLike());

        }

        if (queryRequest.getVariables() != null) {
            addVariables(query, queryRequest.getVariables());
        }
        
        if (restApiInterceptor != null) {
            restApiInterceptor.accessHistoryVariableInfoWithQuery(query, queryRequest);
        }

        return paginateList(allRequestParams, query, "variableName", allowedSortProperties, restResponseFactory::createHistoricVariableInstanceResponseList);
    }
    
    public RestVariable getVariableFromRequest(boolean includeBinary, String varInstanceId, HttpServletRequest request) {
        HistoricVariableInstance varObject = historyService.createHistoricVariableInstanceQuery().id(varInstanceId).singleResult();

        if (varObject == null) {
            throw new FlowableObjectNotFoundException("Historic variable instance '" + varInstanceId + "' couldn't be found.", VariableInstanceEntity.class);
        } else {
            if (restApiInterceptor != null) {
                restApiInterceptor.accessHistoryVariableInfoById(varObject);
            }
            return restResponseFactory.createRestVariable(varObject.getVariableName(), varObject.getValue(), null, varInstanceId, CmmnRestResponseFactory.VARIABLE_HISTORY_VARINSTANCE, includeBinary);
        }
    }

    protected void addVariables(HistoricVariableInstanceQuery variableInstanceQuery, List<QueryVariable> variables) {
        for (QueryVariable variable : variables) {
            if (variable.getVariableOperation() == null) {
                throw new FlowableIllegalArgumentException("Variable operation is missing for variable: " + variable.getName());
            }
            if (variable.getValue() == null) {
                throw new FlowableIllegalArgumentException("Variable value is missing for variable: " + variable.getName());
            }

            boolean nameLess = variable.getName() == null;

            Object actualValue = restResponseFactory.getVariableValue(variable);

            // A value-only query is only possible using equals-operator
            if (nameLess) {
                throw new FlowableIllegalArgumentException("Value-only query (without a variable-name) is not supported");
            }

            switch (variable.getVariableOperation()) {

            case EQUALS:
                variableInstanceQuery.variableValueEquals(variable.getName(), actualValue);
                break;

            default:
                throw new FlowableIllegalArgumentException("Unsupported variable query operation: " + variable.getVariableOperation());
            }
        }
    }
}
