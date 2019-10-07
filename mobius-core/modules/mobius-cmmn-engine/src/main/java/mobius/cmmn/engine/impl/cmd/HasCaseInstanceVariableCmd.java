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

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class HasCaseInstanceVariableCmd implements Command<Boolean>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String caseInstanceId;
    protected String variableName;
    protected boolean isLocal;

    public HasCaseInstanceVariableCmd(String caseInstanceId, String variableName, boolean isLocal) {
        this.caseInstanceId = caseInstanceId;
        this.variableName = variableName;
        this.isLocal = isLocal;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        if (caseInstanceId == null) {
            throw new FlowableIllegalArgumentException("caseInstanceId is null");
        }
        if (variableName == null) {
            throw new FlowableIllegalArgumentException("variableName is null");
        }

        CaseInstanceEntity caseInstance = CommandContextUtil.getCaseInstanceEntityManager(commandContext).findById(caseInstanceId);

        if (caseInstance == null) {
            throw new FlowableObjectNotFoundException("case instance " + caseInstanceId + " doesn't exist", CaseInstance.class);
        }

        boolean hasVariable = false;

        if (isLocal) {
            hasVariable = caseInstance.hasVariableLocal(variableName);
        } else {
            hasVariable = caseInstance.hasVariable(variableName);
        }

        return hasVariable;
    }
}
