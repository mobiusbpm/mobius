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
package mobius.content.engine.impl.cmd;

import java.io.Serializable;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.content.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class DeleteContentItemsByScopeCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String scopeId;
    protected String scopeType;

    public DeleteContentItemsByScopeCmd(String scopeId, String scopeType) {
        this.scopeId = scopeId;
        this.scopeType = scopeType;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (scopeId == null && scopeType == null) {
            throw new FlowableIllegalArgumentException("scopeId and scopeType are null");
        }

        CommandContextUtil.getContentItemEntityManager().deleteContentItemsByScopeIdAndScopeType(scopeId, scopeType);

        return null;
    }

}
