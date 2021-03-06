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

import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public abstract class AbstractNeedsCaseInstanceCmd implements Command<Void>, Serializable {

    protected String caseInstanceId;

    public AbstractNeedsCaseInstanceCmd(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (caseInstanceId == null) {
            throw new FlowableIllegalArgumentException("Case instance id is null");
        }
        CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCaseInstanceEntityManager(commandContext).findById(caseInstanceId);
        if (caseInstanceEntity == null) {
            throw new FlowableObjectNotFoundException("Cannot find case instance for id " + caseInstanceId, CaseInstanceEntity.class);
        }
        
        internalExecute(commandContext, caseInstanceEntity);
        return null;
    }
    
    protected abstract void internalExecute(CommandContext commandContext, CaseInstanceEntity caseInstanceEntity);
   
}
