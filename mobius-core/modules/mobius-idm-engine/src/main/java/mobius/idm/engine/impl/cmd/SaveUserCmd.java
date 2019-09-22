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

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.common.engine.impl.persistence.entity.Entity;
import mobius.idm.api.PasswordEncoder;
import mobius.idm.api.PasswordSalt;
import mobius.idm.api.User;
import mobius.idm.engine.impl.persistence.entity.UserEntity;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 * @author Joram Barrez
 */
public class SaveUserCmd implements Command<Void>, Serializable {

    private static final long serialVersionUID = 1L;
    
    protected User user;

    public SaveUserCmd(User user) {
        this.user = user;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        if (user == null) {
            throw new FlowableIllegalArgumentException("user is null");
        }
        
        if (CommandContextUtil.getUserEntityManager(commandContext).isNewUser(user)) {
            if (user.getPassword() != null) {
                PasswordEncoder passwordEncoder = CommandContextUtil.getIdmEngineConfiguration().getPasswordEncoder();
                PasswordSalt passwordSalt = CommandContextUtil.getIdmEngineConfiguration().getPasswordSalt();
                user.setPassword(passwordEncoder.encode(user.getPassword(), passwordSalt));
            }
            
            if (user instanceof UserEntity) {
                CommandContextUtil.getUserEntityManager(commandContext).insert((UserEntity) user, true);
            } else {
                CommandContextUtil.getDbSqlSession(commandContext).insert((Entity) user);
            }
        } else {
            UserEntity dbUser = CommandContextUtil.getUserEntityManager().findById(user.getId());
            user.setPassword(dbUser.getPassword());
            CommandContextUtil.getUserEntityManager().updateUser(user);
        }

        return null;
    }
}