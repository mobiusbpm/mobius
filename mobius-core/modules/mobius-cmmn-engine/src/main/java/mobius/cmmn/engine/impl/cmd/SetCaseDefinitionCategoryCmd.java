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
package mobius.cmmn.engine.impl.cmd;

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.engine.impl.persistence.entity.CaseDefinitionEntity;
import mobius.cmmn.engine.impl.persistence.entity.deploy.CaseDefinitionCacheEntry;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;

/**
 *
 */
public class SetCaseDefinitionCategoryCmd implements Command<Void> {

    protected String caseDefinitionId;
    protected String category;

    public SetCaseDefinitionCategoryCmd(String caseDefinitionId, String category) {
        this.caseDefinitionId = caseDefinitionId;
        this.category = category;
    }

    @Override
    public Void execute(CommandContext commandContext) {

        if (caseDefinitionId == null) {
            throw new FlowableIllegalArgumentException("Case definition id is null");
        }

        CaseDefinitionEntity caseDefinition = CommandContextUtil.getCaseDefinitionEntityManager(commandContext).findById(caseDefinitionId);

        if (caseDefinition == null) {
            throw new FlowableObjectNotFoundException("No case definition found for id = '" + caseDefinitionId + "'", CaseDefinition.class);
        }

        // Update category
        caseDefinition.setCategory(category);

        // Remove case definition from cache, it will be refetched later
        DeploymentCache<CaseDefinitionCacheEntry> caseDefinitionCache = CommandContextUtil.getCmmnEngineConfiguration(commandContext).getCaseDefinitionCache();
        if (caseDefinitionCache != null) {
            caseDefinitionCache.remove(caseDefinitionId);
        }

        return null;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setCaseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

}
