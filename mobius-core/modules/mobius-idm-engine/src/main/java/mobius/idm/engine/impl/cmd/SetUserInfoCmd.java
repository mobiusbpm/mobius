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
import java.util.Map;

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.idm.engine.impl.persistence.entity.IdentityInfoEntity;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 *
 */
public class SetUserInfoCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String userId;
    protected String userPassword;
    protected String type;
    protected String key;
    protected String value;
    protected String accountPassword;
    protected Map<String, String> accountDetails;

    public SetUserInfoCmd(String userId, String key, String value) {
        this.userId = userId;
        this.type = IdentityInfoEntity.TYPE_USERINFO;
        this.key = key;
        this.value = value;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        CommandContextUtil.getIdentityInfoEntityManager(commandContext).updateUserInfo(userId, userPassword, type, key, value, accountPassword, accountDetails);
        return null;
    }
}
