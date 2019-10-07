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
import mobius.idm.api.PrivilegeMapping;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class GetPrivilegeMappingsByPrivilegeIdCmd implements Command<List<PrivilegeMapping>>, Serializable {

    private static final long serialVersionUID = 1L;

    protected String privilegeId;

    public GetPrivilegeMappingsByPrivilegeIdCmd(String privilegeId) {
        if (privilegeId == null) {
            throw new FlowableIllegalArgumentException("privilegeId is null");
        }
        this.privilegeId = privilegeId;
    }

    @Override
    public List<PrivilegeMapping> execute(CommandContext commandContext) {
        return CommandContextUtil.getPrivilegeMappingEntityManager(commandContext).getPrivilegeMappingsByPrivilegeId(privilegeId);
    }
}
