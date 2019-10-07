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

package mobius.idm.engine.impl.cmd;

import java.io.Serializable;
import java.util.List;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.idm.api.User;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class GetUsersWithPrivilegeCmd implements Command<List<User>>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String name;

    public GetUsersWithPrivilegeCmd(String name) {
        if (name == null) {
            throw new FlowableIllegalArgumentException("name is null");
        }
        this.name = name;
    }

    @Override
    public List<User> execute(CommandContext commandContext) {
        return CommandContextUtil.getUserEntityManager(commandContext).findUsersByPrivilegeId(name);
    }
}
