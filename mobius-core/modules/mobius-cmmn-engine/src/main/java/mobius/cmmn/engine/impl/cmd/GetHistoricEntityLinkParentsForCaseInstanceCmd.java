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
import java.util.List;

import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.scope.ScopeTypes;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.entitylink.api.EntityLinkType;
import mobius.entitylink.api.history.HistoricEntityLink;

/**
 * @author Javier Casal
 */
public class GetHistoricEntityLinkParentsForCaseInstanceCmd implements Command<List<HistoricEntityLink>>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String caseInstanceId;

    public GetHistoricEntityLinkParentsForCaseInstanceCmd(String caseInstanceId) {
        if (caseInstanceId == null) {
            throw new FlowableIllegalArgumentException("caseInstanceId is required");
        }
        this.caseInstanceId = caseInstanceId;
    }

    @Override
    public List<HistoricEntityLink> execute(CommandContext commandContext) {
        return CommandContextUtil.getHistoricEntityLinkService().findHistoricEntityLinksByReferenceScopeIdAndType(
                        caseInstanceId, ScopeTypes.CMMN, EntityLinkType.CHILD);
    }

}
