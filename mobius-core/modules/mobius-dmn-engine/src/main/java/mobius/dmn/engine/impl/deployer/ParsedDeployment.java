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
package mobius.dmn.engine.impl.deployer;

import java.util.List;
import java.util.Map;

import mobius.common.engine.api.repository.EngineResource;
import mobius.dmn.engine.impl.parser.DmnParse;
import mobius.dmn.engine.impl.persistence.entity.DecisionTableEntity;
import mobius.dmn.engine.impl.persistence.entity.DmnDeploymentEntity;
import mobius.dmn.model.Decision;
import mobius.dmn.model.DmnDefinition;

/**
 * An intermediate representation of a DeploymentEntity which keeps track of all of the entity's DecisionTableEntities and resources and processes associated with each
 * DecisionTableEntity - all produced by parsing the deployment.
 * 
 * The DecisionTableEntities are expected to be "not fully set-up" - they may be inconsistent with the DeploymentEntity and/or the persisted versions, and if the deployment is new, they will not yet
 * be persisted.
 */
public class ParsedDeployment {

    protected DmnDeploymentEntity deploymentEntity;

    protected List<DecisionTableEntity> decisionTables;
    protected Map<DecisionTableEntity, DmnParse> mapDecisionTablesToParses;
    protected Map<DecisionTableEntity, EngineResource> mapDecisionTablesToResources;

    public ParsedDeployment(
            DmnDeploymentEntity entity, List<DecisionTableEntity> decisionTables,
            Map<DecisionTableEntity, DmnParse> mapDecisionTablesToParses,
            Map<DecisionTableEntity, EngineResource> mapDecisionTablesToResources) {

        this.deploymentEntity = entity;
        this.decisionTables = decisionTables;
        this.mapDecisionTablesToParses = mapDecisionTablesToParses;
        this.mapDecisionTablesToResources = mapDecisionTablesToResources;
    }

    public DmnDeploymentEntity getDeployment() {
        return deploymentEntity;
    }

    public List<DecisionTableEntity> getAllDecisionTables() {
        return decisionTables;
    }

    public EngineResource getResourceForDecisionTable(DecisionTableEntity decisionTable) {
        return mapDecisionTablesToResources.get(decisionTable);
    }

    public DmnParse getDmnParseForDecisionTable(DecisionTableEntity decisionTable) {
        return mapDecisionTablesToParses.get(decisionTable);
    }

    public DmnDefinition getDmnDefinitionForDecisionTable(DecisionTableEntity decisionTable) {
        DmnParse parse = getDmnParseForDecisionTable(decisionTable);

        return (parse == null ? null : parse.getDmnDefinition());
    }

    public Decision getDecisionForDecisionTable(DecisionTableEntity decisionTable) {
        DmnDefinition dmnDefinition = getDmnDefinitionForDecisionTable(decisionTable);

        return (dmnDefinition == null ? null : dmnDefinition.getDecisionById(decisionTable.getKey()));
    }

}