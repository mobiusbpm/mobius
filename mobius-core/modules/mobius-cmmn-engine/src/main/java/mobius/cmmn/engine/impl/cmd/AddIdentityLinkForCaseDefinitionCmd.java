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

import java.io.Serializable;

import mobius.cmmn.api.repository.CaseDefinition;
import mobius.cmmn.engine.impl.persistence.entity.CaseDefinitionEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.identitylink.service.impl.persistence.entity.IdentityLinkEntity;

/**
 * @author Tijs Rademakers
 */
public class AddIdentityLinkForCaseDefinitionCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String caseDefinitionId;

    protected String userId;

    protected String groupId;

    public AddIdentityLinkForCaseDefinitionCmd(String caseDefinitionId, String userId, String groupId) {
        validateParams(userId, groupId, caseDefinitionId);
        this.caseDefinitionId = caseDefinitionId;
        this.userId = userId;
        this.groupId = groupId;
    }

    protected void validateParams(String userId, String groupId, String caseDefinitionId) {
        if (caseDefinitionId == null) {
            throw new FlowableIllegalArgumentException("caseDefinitionId is null");
        }

        if (userId == null && groupId == null) {
            throw new FlowableIllegalArgumentException("userId and groupId cannot both be null");
        }
    }

    @Override
    public Void execute(CommandContext commandContext) {
        CaseDefinitionEntity caseDefinition = CommandContextUtil.getCaseDefinitionEntityManager(commandContext).findById(caseDefinitionId);

        if (caseDefinition == null) {
            throw new FlowableObjectNotFoundException("Cannot find case definition with id " + caseDefinitionId, CaseDefinition.class);
        }

        IdentityLinkEntity identityLinkEntity = CommandContextUtil.getIdentityLinkService().createScopeDefinitionIdentityLink(
                        caseDefinition.getId(), ScopeTypes.CMMN, userId, groupId);
        caseDefinition.getIdentityLinks().add(identityLinkEntity);

        return null;
    }

}
