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

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.engine.compatibility.Flowable5CompatibilityHandler;
import mobius.engine.impl.util.Flowable5Util;
import mobius.engine.impl.util.ProcessDefinitionUtil;

/**
 *
 */
public class IsProcessDefinitionSuspendedCmd implements Command<Boolean>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String processDefinitionId;

    public IsProcessDefinitionSuspendedCmd(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public Boolean execute(CommandContext commandContext) {
        // Backwards compatibility
        if (Flowable5Util.isFlowable5ProcessDefinitionId(commandContext, processDefinitionId)) {
            Flowable5CompatibilityHandler compatibilityHandler = Flowable5Util.getFlowable5CompatibilityHandler();
            return compatibilityHandler.isProcessDefinitionSuspended(processDefinitionId);
        }

        return ProcessDefinitionUtil.isProcessDefinitionSuspended(processDefinitionId);
    }
}
