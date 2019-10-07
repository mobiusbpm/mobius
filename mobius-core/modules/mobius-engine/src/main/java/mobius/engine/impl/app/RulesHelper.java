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

package mobius.engine.impl.app;

import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.persistence.deploy.DeploymentCache;
import mobius.engine.impl.persistence.entity.DeploymentEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.repository.Deployment;
import org.kie.api.KieBase;

/**
 *
 */
public class RulesHelper {

    public static KieBase findKnowledgeBaseByDeploymentId(String deploymentId) {
        DeploymentCache<Object> knowledgeBaseCache = CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager().getKnowledgeBaseCache();

        KieBase knowledgeBase = (KieBase) knowledgeBaseCache.get(deploymentId);
        if (knowledgeBase == null) {
            DeploymentEntity deployment = CommandContextUtil.getDeploymentEntityManager().findById(deploymentId);
            if (deployment == null) {
                throw new FlowableObjectNotFoundException("no deployment with id " + deploymentId, Deployment.class);
            }
            CommandContextUtil.getProcessEngineConfiguration().getDeploymentManager().deploy(deployment);
            knowledgeBase = (KieBase) knowledgeBaseCache.get(deploymentId);
            if (knowledgeBase == null) {
                throw new FlowableException("deployment " + deploymentId + " doesn't contain any rules");
            }
        }
        return knowledgeBase;
    }
}
