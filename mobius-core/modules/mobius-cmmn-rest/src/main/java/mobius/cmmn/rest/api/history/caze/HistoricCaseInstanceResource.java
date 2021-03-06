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

package mobius.cmmn.rest.api.history.caze;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import mobius.cmmn.api.CmmnHistoryService;
import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.CmmnRuntimeService;
import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.api.history.HistoricPlanItemInstance;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.runtime.PlanItemDefinitionType;
import mobius.cmmn.api.runtime.PlanItemInstanceState;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.Stage;
import mobius.cmmn.rest.api.CmmnRestResponseFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;

/**
 *
 *
 */
@RestController
@Api(tags = { "History Case" }, description = "Manage History Case Instances", authorizations = { @Authorization(value = "basicAuth") })
public class HistoricCaseInstanceResource extends HistoricCaseInstanceBaseResource {

    @Autowired
    protected CmmnRestResponseFactory restResponseFactory;

    @Autowired
    protected CmmnRepositoryService cmmnRepositoryService;

    @Autowired
    protected CmmnHistoryService cmmnhistoryService;
    
    @Autowired
    protected CmmnRuntimeService cmmnRuntimeService;

    @ApiOperation(value = "Get a historic case instance", tags = { "History Case" }, nickname = "getHistoricCaseInstance")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates that the historic process instances could be found."),
            @ApiResponse(code = 404, message = "Indicates that the historic process instances could not be found.") })
    @GetMapping(value = "/cmmn-history/historic-case-instances/{caseInstanceId}", produces = "application/json")
    public HistoricCaseInstanceResponse getCaseInstance(@ApiParam(name = "caseInstanceId") @PathVariable String caseInstanceId) {
        HistoricCaseInstanceResponse caseInstanceResponse = restResponseFactory.createHistoricCaseInstanceResponse(getHistoricCaseInstanceFromRequest(caseInstanceId));
        
        CaseDefinition caseDefinition = cmmnRepositoryService.createCaseDefinitionQuery().caseDefinitionId(caseInstanceResponse.getCaseDefinitionId()).singleResult();
        if (caseDefinition != null) {
            caseInstanceResponse.setCaseDefinitionName(caseDefinition.getName());
            caseInstanceResponse.setCaseDefinitionDescription(caseDefinition.getDescription());
        }
        
        return caseInstanceResponse;
    }

    @ApiOperation(value = " Delete a historic case instance", tags = { "History Case" }, nickname = "deleteHistoricCaseInstance")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Indicates that the historic process instance was deleted."),
            @ApiResponse(code = 404, message = "Indicates that the historic process instance could not be found.") })
    @DeleteMapping(value = "/cmmn-history/historic-case-instances/{caseInstanceId}")
    public void deleteCaseInstance(@ApiParam(name = "caseInstanceId") @PathVariable String caseInstanceId, HttpServletResponse response) {
        HistoricCaseInstance caseInstance = getHistoricCaseInstanceFromRequest(caseInstanceId);
        if (restApiInterceptor != null) {
            restApiInterceptor.deleteHistoricCase(caseInstance);
        }
        
        cmmnhistoryService.deleteHistoricCaseInstance(caseInstance.getId());
        response.setStatus(HttpStatus.NO_CONTENT.value());
    }
    
    @GetMapping(value = "/cmmn-history/historic-case-instances/{caseInstanceId}/stage-overview", produces = "application/json")
    public List<StageResponse> getStageOverview(@ApiParam(name = "caseInstanceId") @PathVariable String caseInstanceId) {

        HistoricCaseInstance caseInstance = getHistoricCaseInstanceFromRequest(caseInstanceId);

        List<HistoricPlanItemInstance> stagePlanItemInstances = historyService.createHistoricPlanItemInstanceQuery()
            .planItemInstanceCaseInstanceId(caseInstanceId)
            .planItemInstanceDefinitionType(PlanItemDefinitionType.STAGE)
            .orderByEndedTime().asc()
            .list();

        CmmnModel cmmnModel = repositoryService.getCmmnModel(caseInstance.getCaseDefinitionId());
        List<Stage> stages = cmmnModel.getPrimaryCase().getPlanModel().findPlanItemDefinitionsOfType(Stage.class, true);

        // If one stage has a display order, they are ordered by that.
        // Otherwise, the order as it comes back from the query is used.
        stages.sort(Comparator.comparing(Stage::getDisplayOrder, Comparator.nullsFirst(Comparator.naturalOrder()))
            .thenComparing(stage -> getPlanItemInstanceEndTime(stagePlanItemInstances, stage), Comparator.nullsLast(Comparator.naturalOrder()))
        );
        List<StageResponse> stageResponses = new ArrayList<>(stages.size());
        for (Stage stage : stages) {
            if (stage.isIncludeInStageOverview()) {
                StageResponse stageResponse = new StageResponse(stage.getId(), stage.getName());
                Optional<HistoricPlanItemInstance> planItemInstance = getPlanItemInstance(stagePlanItemInstances, stage);

                // If not ended or current, it's implicitly a future one
                if (planItemInstance.isPresent()) {
                    stageResponse.setEnded(planItemInstance.get().getEndedTime() != null);
                    stageResponse.setCurrent(PlanItemInstanceState.ACTIVE.equals(planItemInstance.get().getState()));
                }

                stageResponses.add(stageResponse);
            }
        }

        return stageResponses;
    }
    
    protected Date getPlanItemInstanceEndTime(List<HistoricPlanItemInstance> stagePlanItemInstances, Stage stage) {
        return getPlanItemInstance(stagePlanItemInstances, stage)
            .map(HistoricPlanItemInstance::getEndedTime)
            .orElse(null);
    }

    protected Optional<HistoricPlanItemInstance> getPlanItemInstance(List<HistoricPlanItemInstance> stagePlanItemInstances, Stage stage) {
        HistoricPlanItemInstance planItemInstance = null;
        for (HistoricPlanItemInstance p : stagePlanItemInstances) {
            if (p.getPlanItemDefinitionId().equals(stage.getId())) {
                if (p.getEndedTime() == null) {
                    planItemInstance = p; // one that's not ended yet has precedence
                } else {
                    if (planItemInstance == null) {
                        planItemInstance = p;
                    }
                }

            }
        }
        return Optional.ofNullable(planItemInstance);
    }

}
