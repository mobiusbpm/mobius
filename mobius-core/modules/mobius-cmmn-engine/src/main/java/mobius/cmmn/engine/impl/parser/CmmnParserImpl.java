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
package mobius.cmmn.engine.impl.parser;

import mobius.cmmn.converter.CmmnXMLException;
import mobius.cmmn.converter.CmmnXmlConverter;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.persistence.entity.CaseDefinitionEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.repository.EngineResource;
import mobius.common.engine.impl.el.ExpressionManager;
import mobius.common.engine.impl.util.io.InputStreamSource;
import mobius.common.engine.impl.util.io.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CmmnParserImpl implements CmmnParser {

    private final Logger logger = LoggerFactory.getLogger(CmmnParserImpl.class);

    protected CmmnParseHandlers cmmnParseHandlers;
    protected CmmnActivityBehaviorFactory activityBehaviorFactory;
    protected ExpressionManager expressionManager;

    @Override
    public CmmnParseResult parse(EngineResource resourceEntity) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(resourceEntity.getBytes())) {
            CmmnParseResult cmmnParseResult = parse(resourceEntity, new InputStreamSource(inputStream));
            processDI(cmmnParseResult.getCmmnModel(), cmmnParseResult.getAllCaseDefinitions());
            return cmmnParseResult;

        } catch (IOException e) {
            logger.error("Could not read bytes from CMMN resource", e);
            return new CmmnParseResult();
        }
    }

    public CmmnParseResult parse(EngineResource resourceEntity, StreamSource cmmnSource) {
        try {
            boolean enableSafeBpmnXml = false;
            String encoding = null;
            CmmnEngineConfiguration cmmnEngineConfiguration = CommandContextUtil.getCmmnEngineConfiguration();
            if (cmmnEngineConfiguration != null) {
                enableSafeBpmnXml = cmmnEngineConfiguration.isEnableSafeCmmnXml();
                encoding = cmmnEngineConfiguration.getXmlEncoding();
            }

            CmmnParseResult cmmnParseResult = new CmmnParseResult();
            cmmnParseResult.setResourceEntity(resourceEntity);

            boolean validateCmmnXml = true;
            if (cmmnEngineConfiguration.isDisableCmmnXmlValidation()) {
                validateCmmnXml = false;
            }
            
            CmmnModel cmmnModel = new CmmnXmlConverter().convertToCmmnModel(cmmnSource, validateCmmnXml, enableSafeBpmnXml, encoding);
            cmmnParseResult.setCmmnModel(cmmnModel);

            processCmmnElements(cmmnModel, cmmnParseResult);

            return cmmnParseResult;

        } catch (Exception e) {
            if (e instanceof FlowableException) {
                throw (FlowableException) e;
            } else if (e instanceof CmmnXMLException) {
                throw (CmmnXMLException) e;
            } else {
                throw new FlowableException("Error parsing XML", e);
            }
        }
    }

    public void processCmmnElements(CmmnModel cmmnModel, CmmnParseResult parseResult) {
        for (Case caze : cmmnModel.getCases()) {
            cmmnParseHandlers.parseElement(this, parseResult, caze);
        }
    }

    public void processDI(CmmnModel cmmnModel, List<CaseDefinitionEntity> caseDefinitions) {

        if (caseDefinitions.isEmpty()) {
            return;
        }

        if (!cmmnModel.getLocationMap().isEmpty()) {

            List<String> planModelIds = new ArrayList<>();
            for (Case caseObject : cmmnModel.getCases()) {
                planModelIds.add(caseObject.getPlanModel().getId());
            }

            // Verify if all referenced elements exist
            for (String cmmnReference : cmmnModel.getLocationMap().keySet()) {

                if (planModelIds.contains(cmmnReference)) {
                    continue;
                }

                if (cmmnModel.findPlanItem(cmmnReference) == null && cmmnModel.getCriterion(cmmnReference) == null) {
                    logger.warn("Invalid reference in diagram interchange definition: could not find {}", cmmnReference);
                }
            }

            for (Case caseObject : cmmnModel.getCases()) {
                CaseDefinitionEntity caseDefinition = getCaseDefinition(caseObject.getId(), caseDefinitions);
                if (caseDefinition != null) {
                    caseDefinition.setHasGraphicalNotation(true);
                }
            }
        }
    }

    public CaseDefinitionEntity getCaseDefinition(String caseDefinitionKey, List<CaseDefinitionEntity> caseDefinitions) {
        for (CaseDefinitionEntity caseDefinition : caseDefinitions) {
            if (caseDefinition.getKey().equals(caseDefinitionKey)) {
                return caseDefinition;
            }
        }
        return null;
    }

    public CmmnParseHandlers getCmmnParseHandlers() {
        return cmmnParseHandlers;
    }

    public void setCmmnParseHandlers(CmmnParseHandlers cmmnParseHandlers) {
        this.cmmnParseHandlers = cmmnParseHandlers;
    }

    public CmmnActivityBehaviorFactory getActivityBehaviorFactory() {
        return activityBehaviorFactory;
    }

    public void setActivityBehaviorFactory(CmmnActivityBehaviorFactory activityBehaviorFactory) {
        this.activityBehaviorFactory = activityBehaviorFactory;
    }

    public ExpressionManager getExpressionManager() {
        return expressionManager;
    }

    public void setExpressionManager(ExpressionManager expressionManager) {
        this.expressionManager = expressionManager;
    }

}
