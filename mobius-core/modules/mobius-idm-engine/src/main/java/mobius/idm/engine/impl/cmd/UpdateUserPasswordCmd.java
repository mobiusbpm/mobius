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

import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.idm.api.PasswordEncoder;
import mobius.idm.api.PasswordSalt;
import mobius.idm.api.User;
import mobius.idm.engine.impl.util.CommandContextUtil;

import java.io.Serializable;

/**
 * @author faizal-manan
 */
public class UpdateUserPasswordCmd implements Command<User>, Serializable {

    private static final long serialVersionUID = 1L;
    
    private User user;

    public UpdateUserPasswordCmd(User user) {
        this.user = user;
    }

    @Override
    public User execute(CommandContext commandContext) {
        if (!CommandContextUtil.getUserEntityManager(commandContext).isNewUser(user)) {
            PasswordEncoder passwordEncoder = CommandContextUtil.getIdmEngineConfiguration().getPasswordEncoder();
            PasswordSalt passwordSalt = CommandContextUtil.getIdmEngineConfiguration().getPasswordSalt();
            
            user.setUserPassword(passwordEncoder.encode(user.getUserPassword(), passwordSalt));
            CommandContextUtil.getUserEntityManager(commandContext).updateUser(user);
        }
        return user;
    }
}
