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
package mobius.form.engine.impl.util;

import mobius.common.engine.api.FlowableException;
import mobius.form.engine.FormEngineConfiguration;
import mobius.form.engine.impl.persistence.deploy.DeploymentManager;
import mobius.form.engine.impl.persistence.deploy.FormDefinitionCacheEntry;
import mobius.form.engine.impl.persistence.entity.FormDefinitionEntity;
import mobius.form.engine.impl.persistence.entity.FormDefinitionEntityManager;
import mobius.form.model.SimpleFormModel;

/**
 * A utility class that hides the complexity of {@link FormDefinitionEntity} and {@link SimpleFormModel} lookup. Use this class rather than accessing the decision table cache or {@link DeploymentManager}
 * directly.
 * 
 *
 *
 */
public class FormUtil {

    public static FormDefinitionEntity getFormDefinitionEntity(String formDefinitionId) {
        return getFormDefinitionEntity(formDefinitionId, false);
    }

    public static FormDefinitionEntity getFormDefinitionEntity(String formDefinitionId, boolean checkCacheOnly) {
        if (checkCacheOnly) {
            FormDefinitionCacheEntry cacheEntry = CommandContextUtil.getFormEngineConfiguration().getFormDefinitionCache().get(formDefinitionId);
            if (cacheEntry != null) {
                return cacheEntry.getFormDefinitionEntity();
            }
            return null;
        } else {
            // This will check the cache in the findDeployedFormDefinitionById method
            return CommandContextUtil.getFormEngineConfiguration().getDeploymentManager().findDeployedFormDefinitionById(formDefinitionId);
        }
    }

    public static SimpleFormModel getFormDefinition(String formDefinitionId) {
        FormEngineConfiguration formEngineConfiguration = CommandContextUtil.getFormEngineConfiguration();
        DeploymentManager deploymentManager = formEngineConfiguration.getDeploymentManager();

        // This will check the cache in the findDeployedFormDefinitionById and resolveFormDefinition method
        FormDefinitionEntity formDefinitionEntity = deploymentManager.findDeployedFormDefinitionById(formDefinitionId);
        FormDefinitionCacheEntry cacheEntry = deploymentManager.resolveFormDefinition(formDefinitionEntity);
        return formEngineConfiguration.getFormJsonConverter().convertToFormModel(cacheEntry.getFormDefinitionJson());
    }

    public static SimpleFormModel getFormDefinitionFromCache(String formId) {
        FormEngineConfiguration formEngineConfiguration = CommandContextUtil.getFormEngineConfiguration();
        FormDefinitionCacheEntry cacheEntry = formEngineConfiguration.getFormDefinitionCache().get(formId);
        if (cacheEntry != null) {
            return formEngineConfiguration.getFormJsonConverter().convertToFormModel(cacheEntry.getFormDefinitionJson());
        }
        return null;
    }

    public static FormDefinitionEntity getFormDefinitionFromDatabase(String formDefinitionId) {
        FormDefinitionEntityManager formDefinitionEntityManager = CommandContextUtil.getFormEngineConfiguration().getFormDefinitionEntityManager();
        FormDefinitionEntity formDefinition = formDefinitionEntityManager.findById(formDefinitionId);
        if (formDefinition == null) {
            throw new FlowableException("No form definitionfound with id " + formDefinitionId);
        }

        return formDefinition;
    }
}
