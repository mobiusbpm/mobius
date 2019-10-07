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
package mobius.app.engine.impl;

import java.io.InputStream;
import java.util.List;

import mobius.app.engine.impl.repository.AppDeploymentBuilderImpl;
import mobius.app.api.AppRepositoryService;
import mobius.app.api.repository.AppDefinition;
import mobius.app.api.repository.AppDefinitionQuery;
import mobius.app.api.repository.AppDeployment;
import mobius.app.api.repository.AppDeploymentBuilder;
import mobius.app.api.repository.AppDeploymentQuery;
import mobius.app.api.repository.AppModel;
import mobius.app.engine.AppEngineConfiguration;
import mobius.app.engine.impl.cmd.DeleteDeploymentCmd;
import mobius.app.engine.impl.cmd.DeployCmd;
import mobius.app.engine.impl.cmd.GetAppModelCmd;
import mobius.app.engine.impl.cmd.GetAppModelJsonCmd;
import mobius.app.engine.impl.cmd.GetDeploymentAppDefinitionCmd;
import mobius.app.engine.impl.cmd.GetDeploymentResourceCmd;
import mobius.app.engine.impl.cmd.GetDeploymentResourceNamesCmd;
import mobius.app.engine.impl.cmd.SetAppDefinitionCategoryCmd;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;

/**
 *
 *
 */
public class AppRepositoryServiceImpl extends CommonEngineServiceImpl<AppEngineConfiguration> implements AppRepositoryService {
    
    public AppRepositoryServiceImpl(AppEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public AppDeploymentBuilder createDeployment() {
        return commandExecutor.execute(new Command<AppDeploymentBuilder>() {
            @Override
            public AppDeploymentBuilder execute(CommandContext commandContext) {
                return new AppDeploymentBuilderImpl();
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
    
    public AppDeployment deploy(AppDeploymentBuilderImpl deploymentBuilder) {
        return commandExecutor.execute(new DeployCmd(deploymentBuilder));
    }
    
    @Override
    public AppDefinition getAppDefinition(String appDefinitionId) {
        return commandExecutor.execute(new GetDeploymentAppDefinitionCmd(appDefinitionId));
    }
    
    @Override
    public AppModel getAppModel(String appDefinitionId) {
        return commandExecutor.execute(new GetAppModelCmd(appDefinitionId));
    }
    
    @Override
    public String convertAppModelToJson(String appDefinitionId) {
        return commandExecutor.execute(new GetAppModelJsonCmd(appDefinitionId));
    }

    @Override
    public void deleteDeployment(String deploymentId, boolean cascade) {
        commandExecutor.execute(new DeleteDeploymentCmd(deploymentId, cascade));
    }
    
    @Override
    public AppDeploymentQuery createDeploymentQuery() {
        return configuration.getAppDeploymentEntityManager().createDeploymentQuery();
    }
    
    @Override
    public AppDefinitionQuery createAppDefinitionQuery() {
        return configuration.getAppDefinitionEntityManager().createAppDefinitionQuery();
    }

    @Override
    public void setAppDefinitionCategory(String appDefinitionId, String category) {
        commandExecutor.execute(new SetAppDefinitionCategoryCmd(appDefinitionId, category));
    }
}
