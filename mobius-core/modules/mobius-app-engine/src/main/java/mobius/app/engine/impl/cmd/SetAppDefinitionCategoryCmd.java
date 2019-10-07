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
package mobius.app.engine.impl.cmd;

import mobius.app.engine.impl.util.CommandContextUtil;
import mobius.app.api.repository.AppDefinition;
import mobius.app.engine.impl.persistence.entity.AppDefinitionEntity;
import mobius.app.engine.impl.persistence.entity.deploy.AppDefinitionCacheEntry;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;

/**
 *
 */
public class SetAppDefinitionCategoryCmd implements Command<Void> {

    protected String appDefinitionId;
    protected String category;

    public SetAppDefinitionCategoryCmd(String appDefinitionId, String category) {
        this.appDefinitionId = appDefinitionId;
        this.category = category;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        if (appDefinitionId == null) {
            throw new FlowableIllegalArgumentException("App definition id is null");
        }

        AppDefinitionEntity appDefinition = CommandContextUtil.getAppDefinitionEntityManager(commandContext).findById(appDefinitionId);

        if (appDefinition == null) {
            throw new FlowableObjectNotFoundException("No app definition found for id = '" + appDefinitionId + "'", AppDefinition.class);
        }

        // Update category
        appDefinition.setCategory(category);

        // Remove app definition from cache, it will be refetched later
        DeploymentCache<AppDefinitionCacheEntry> appDefinitionCache = CommandContextUtil.getAppEngineConfiguration(commandContext).getAppDefinitionCache();
        if (appDefinitionCache != null) {
            appDefinitionCache.remove(appDefinitionId);
        }

        return null;
    }

}
