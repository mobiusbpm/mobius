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

import mobius.cmmn.api.history.HistoricCaseInstance;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableException;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 *
 */
public class DeleteHistoricCaseInstanceCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String caseInstanceId;

    public DeleteHistoricCaseInstanceCmd(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        if (caseInstanceId == null) {
            throw new FlowableIllegalArgumentException("caseInstanceId is null");
        }
        // Check if case instance is still running
        HistoricCaseInstance instance = CommandContextUtil.getHistoricCaseInstanceEntityManager(commandContext).findById(caseInstanceId);

        if (instance == null) {
            throw new FlowableObjectNotFoundException("No historic case instance found with id: " + caseInstanceId, HistoricCaseInstance.class);
        }
        if (instance.getEndTime() == null) {
            throw new FlowableException("Case instance is still running, cannot delete historic case instance: " + caseInstanceId);
        }

        CommandContextUtil.getCmmnHistoryManager(commandContext).recordHistoricCaseInstanceDeleted(caseInstanceId);

        return null;
    }

}
