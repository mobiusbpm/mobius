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
package mobius.engine.impl.cmd;

import java.io.Serializable;
import java.util.List;

import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.impl.persistence.entity.ProcessDefinitionEntity;
import mobius.engine.impl.util.CommandContextUtil;
import mobius.engine.repository.ProcessDefinition;
import mobius.identitylink.api.IdentityLink;

/**
 *
 */
public class GetIdentityLinksForProcessDefinitionCmd implements Command<List<IdentityLink>>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String processDefinitionId;

    public GetIdentityLinksForProcessDefinitionCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public List<IdentityLink> execute(CommandContext commandContext) {
        ProcessDefinitionEntity processDefinition = CommandContextUtil.getProcessDefinitionEntityManager(commandContext).findById(processDefinitionId);

        if (processDefinition == null) {
            throw new FlowableObjectNotFoundException("Cannot find process definition with id " + processDefinitionId, ProcessDefinition.class);
        }

        List<IdentityLink> identityLinks = (List) processDefinition.getIdentityLinks();
        return identityLinks;
    }

}
