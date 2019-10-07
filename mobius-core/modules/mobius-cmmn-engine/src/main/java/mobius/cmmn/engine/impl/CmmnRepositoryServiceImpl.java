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
package mobius.cmmn.engine.impl;

import java.io.InputStream;
import java.util.List;

import mobius.cmmn.api.CmmnRepositoryService;
import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.api.repository.CaseDefinitionQuery;
import mobius.cmmn.api.repository.CmmnDeployment;
import mobius.cmmn.api.repository.CmmnDeploymentBuilder;
import mobius.cmmn.api.repository.CmmnDeploymentQuery;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.cmmn.engine.impl.cmd.AddIdentityLinkForCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.DeleteDeploymentCmd;
import mobius.cmmn.engine.impl.cmd.DeleteIdentityLinkForCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.DeployCmd;
import mobius.cmmn.engine.impl.cmd.GetCmmnModelCmd;
import mobius.cmmn.engine.impl.cmd.GetDecisionTablesForCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.GetDeploymentCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.GetDeploymentCaseDiagramCmd;
import mobius.cmmn.engine.impl.cmd.GetDeploymentResourceCmd;
import mobius.cmmn.engine.impl.cmd.GetDeploymentResourceNamesCmd;
import mobius.cmmn.engine.impl.cmd.GetFormDefinitionsForCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.GetIdentityLinksForCaseDefinitionCmd;
import mobius.cmmn.engine.impl.cmd.SetCaseDefinitionCategoryCmd;
import mobius.cmmn.engine.impl.cmd.SetDeploymentParentDeploymentIdCmd;
import mobius.cmmn.engine.impl.repository.CmmnDeploymentBuilderImpl;
import mobius.cmmn.model.CmmnModel;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.dmn.api.DmnDecisionTable;
import mobius.form.api.FormDefinition;
import mobius.identitylink.api.IdentityLink;

/**
 *
 *
 */
public class CmmnRepositoryServiceImpl extends CommonEngineServiceImpl<CmmnEngineConfiguration> implements CmmnRepositoryService {

    public CmmnRepositoryServiceImpl(CmmnEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public CmmnDeploymentBuilder createDeployment() {
        return commandExecutor.execute(new Command<CmmnDeploymentBuilder>() {
            @Override
            public CmmnDeploymentBuilder execute(CommandContext commandContext) {
                return new CmmnDeploymentBuilderImpl();
            }
        });
    }
    
    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
       return commandExecutor.execute(new GetDeploymentResourceNamesCmd(deploymentId));
    }

    @Override
    public InputStream getResourceAsStream(String deploymentId, String resourceName) {
        return commandExecutor.execute(new GetDeploymentResourceCmd(deploymentId, resourceName));
    }
    
    public CmmnDeployment deploy(CmmnDeploymentBuilderImpl deploymentBuilder) {
        return commandExecutor.execute(new DeployCmd(deploymentBuilder));
    }
    
    @Override
    public CaseDefinition getCaseDefinition(String caseDefinitionId) {
        return commandExecutor.execute(new GetDeploymentCaseDefinitionCmd(caseDefinitionId));
    }
    
    @Override
    public CmmnModel getCmmnModel(String caseDefinitionId) {
        return commandExecutor.execute(new GetCmmnModelCmd(caseDefinitionId));
    }
    
    @Override
    public InputStream getCaseDiagram(String caseDefinitionId) {
        return commandExecutor.execute(new GetDeploymentCaseDiagramCmd(caseDefinitionId));
    }
    
    @Override
    public void deleteDeployment(String deploymentId, boolean cascade) {
        commandExecutor.execute(new DeleteDeploymentCmd(deploymentId, cascade));
    }
    
    @Override
    public CmmnDeploymentQuery createDeploymentQuery() {
        return configuration.getCmmnDeploymentEntityManager().createDeploymentQuery();
    }
    
    @Override
    public CaseDefinitionQuery createCaseDefinitionQuery() {
        return configuration.getCaseDefinitionEntityManager().createCaseDefinitionQuery();
    }
    
    @Override
    public void addCandidateStarterUser(String caseDefinitionId, String userId) {
        commandExecutor.execute(new AddIdentityLinkForCaseDefinitionCmd(caseDefinitionId, userId, null));
    }

    @Override
    public void addCandidateStarterGroup(String caseDefinitionId, String groupId) {
        commandExecutor.execute(new AddIdentityLinkForCaseDefinitionCmd(caseDefinitionId, null, groupId));
    }

    @Override
    public void deleteCandidateStarterGroup(String caseDefinitionId, String groupId) {
        commandExecutor.execute(new DeleteIdentityLinkForCaseDefinitionCmd(caseDefinitionId, null, groupId));
    }

    @Override
    public void deleteCandidateStarterUser(String caseDefinitionId, String userId) {
        commandExecutor.execute(new DeleteIdentityLinkForCaseDefinitionCmd(caseDefinitionId, userId, null));
    }

    @Override
    public List<IdentityLink> getIdentityLinksForCaseDefinition(String caseDefinitionId) {
        return commandExecutor.execute(new GetIdentityLinksForCaseDefinitionCmd(caseDefinitionId));
    }

    @Override
    public void setCaseDefinitionCategory(String caseDefinitionId, String category) {
        commandExecutor.execute(new SetCaseDefinitionCategoryCmd(caseDefinitionId, category));
    }
    
    @Override
    public void changeDeploymentParentDeploymentId(String deploymentId, String newParentDeploymentId) {
        commandExecutor.execute(new SetDeploymentParentDeploymentIdCmd(deploymentId, newParentDeploymentId));
    }
    
    @Override
    public List<DmnDecisionTable> getDecisionTablesForCaseDefinition(String caseDefinitionId) {
        return commandExecutor.execute(new GetDecisionTablesForCaseDefinitionCmd(caseDefinitionId));
    }
    
    @Override
    public List<FormDefinition> getFormDefinitionsForCaseDefinition(String caseDefinitionId) {
        return commandExecutor.execute(new GetFormDefinitionsForCaseDefinitionCmd(caseDefinitionId));
    }
}
