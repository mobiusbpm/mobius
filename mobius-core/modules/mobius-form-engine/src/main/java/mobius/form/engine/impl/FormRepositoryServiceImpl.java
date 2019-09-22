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
package mobius.form.engine.impl;

import java.io.InputStream;
import java.util.List;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.service.CommonEngineServiceImpl;
import mobius.form.api.FormDefinition;
import mobius.form.api.FormDefinitionQuery;
import mobius.form.api.FormDeployment;
import mobius.form.api.FormDeploymentBuilder;
import mobius.form.api.FormDeploymentQuery;
import mobius.form.api.FormInfo;
import mobius.form.api.FormRepositoryService;
import mobius.form.api.NativeFormDefinitionQuery;
import mobius.form.api.NativeFormDeploymentQuery;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.engine.impl.cmd.DeleteDeploymentCmd;
import mobius.form.engine.impl.cmd.DeployCmd;
import mobius.form.engine.impl.cmd.GetDeploymentResourceCmd;
import mobius.form.engine.impl.cmd.GetDeploymentResourceNamesCmd;
import mobius.form.engine.impl.cmd.GetFormDefinitionCmd;
import mobius.form.engine.impl.cmd.GetFormDefinitionResourceCmd;
import mobius.form.engine.impl.cmd.GetFormModelCmd;
import mobius.form.engine.impl.cmd.SetDeploymentCategoryCmd;
import mobius.form.engine.impl.cmd.SetDeploymentParentDeploymentIdCmd;
import mobius.form.engine.impl.cmd.SetDeploymentTenantIdCmd;
import mobius.form.engine.impl.cmd.SetFormDefinitionCategoryCmd;
import mobius.form.engine.impl.repository.FormDeploymentBuilderImpl;

/**
 * @author Tijs Rademakers
 */
public class FormRepositoryServiceImpl extends CommonEngineServiceImpl<FormEngineConfiguration> implements FormRepositoryService {

    public FormRepositoryServiceImpl(FormEngineConfiguration engineConfiguration) {
        super(engineConfiguration);
    }

    @Override
    public FormDeploymentBuilder createDeployment() {
        return commandExecutor.execute(new Command<FormDeploymentBuilder>() {
            @Override
            public FormDeploymentBuilder execute(CommandContext commandContext) {
                return new FormDeploymentBuilderImpl();
            }
        });
    }

    public FormDeployment deploy(FormDeploymentBuilderImpl deploymentBuilder) {
        return commandExecutor.execute(new DeployCmd<FormDeployment>(deploymentBuilder));
    }

    @Override
    public void deleteDeployment(String deploymentId) {
        commandExecutor.execute(new DeleteDeploymentCmd(deploymentId));
    }

    @Override
    public FormDefinitionQuery createFormDefinitionQuery() {
        return new FormDefinitionQueryImpl(commandExecutor);
    }

    @Override
    public NativeFormDefinitionQuery createNativeFormDefinitionQuery() {
        return new NativeFormDefinitionQueryImpl(commandExecutor);
    }

    @Override
    public List<String> getDeploymentResourceNames(String deploymentId) {
        return commandExecutor.execute(new GetDeploymentResourceNamesCmd(deploymentId));
    }

    @Override
    public InputStream getResourceAsStream(String deploymentId, String resourceName) {
        return commandExecutor.execute(new GetDeploymentResourceCmd(deploymentId, resourceName));
    }

    @Override
    public void setDeploymentCategory(String deploymentId, String category) {
        commandExecutor.execute(new SetDeploymentCategoryCmd(deploymentId, category));
    }

    @Override
    public void setDeploymentTenantId(String deploymentId, String newTenantId) {
        commandExecutor.execute(new SetDeploymentTenantIdCmd(deploymentId, newTenantId));
    }
    
    @Override
    public void changeDeploymentParentDeploymentId(String deploymentId, String newParentDeploymentId) {
        commandExecutor.execute(new SetDeploymentParentDeploymentIdCmd(deploymentId, newParentDeploymentId));
    }

    @Override
    public FormDeploymentQuery createDeploymentQuery() {
        return new FormDeploymentQueryImpl(commandExecutor);
    }

    @Override
    public NativeFormDeploymentQuery createNativeDeploymentQuery() {
        return new NativeFormDeploymentQueryImpl(commandExecutor);
    }

    @Override
    public FormDefinition getFormDefinition(String formDefinitionId) {
        return commandExecutor.execute(new GetFormDefinitionCmd(formDefinitionId));
    }

    @Override
    public FormInfo getFormModelById(String formId) {
        return commandExecutor.execute(new GetFormModelCmd(null, formId));
    }

    @Override
    public FormInfo getFormModelByKey(String formDefinitionKey) {
        return commandExecutor.execute(new GetFormModelCmd(formDefinitionKey, null));
    }

    @Override
    public FormInfo getFormModelByKey(String formDefinitionKey, String tenantId, boolean fallbackToDefaultTenant) {
        return commandExecutor.execute(new GetFormModelCmd(formDefinitionKey, null, tenantId, fallbackToDefaultTenant));
    }

    @Override
    public FormInfo getFormModelByKeyAndParentDeploymentId(String formDefinitionKey, String parentDeploymentId) {
        return commandExecutor.execute(new GetFormModelCmd(formDefinitionKey, null, null, parentDeploymentId, false));
    }

    @Override
    public FormInfo getFormModelByKeyAndParentDeploymentId(String formDefinitionKey, String parentDeploymentId, String tenantId, boolean fallbackToDefaultTenant) {
        return commandExecutor.execute(new GetFormModelCmd(formDefinitionKey, null, tenantId, parentDeploymentId, fallbackToDefaultTenant));
    }

    @Override
    public InputStream getFormDefinitionResource(String formId) {
        return commandExecutor.execute(new GetFormDefinitionResourceCmd(formId));
    }

    @Override
    public void setFormDefinitionCategory(String formId, String category) {
        commandExecutor.execute(new SetFormDefinitionCategoryCmd(formId, category));
    }
}
