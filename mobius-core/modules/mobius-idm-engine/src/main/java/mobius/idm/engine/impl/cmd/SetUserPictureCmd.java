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
import mobius.common.engine.api.FlowableObjectNotFoundException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.idm.api.Picture;
import mobius.idm.api.User;
import mobius.idm.engine.impl.util.CommandContextUtil;

/**
 * @author Tom Baeyens
 */
public class SetUserPictureCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String userId;
    protected Picture picture;

    public SetUserPictureCmd(String userId, Picture picture) {
        this.userId = userId;
        this.picture = picture;
    }

    @Override
    public Object execute(CommandContext commandContext) {
        if (userId == null) {
            throw new FlowableIllegalArgumentException("userId is null");
        }

        User user = CommandContextUtil.getIdmEngineConfiguration().getIdmIdentityService()
                .createUserQuery().userId(userId)
                .singleResult();

        if (user == null) {
            throw new FlowableObjectNotFoundException("user " + userId + " doesn't exist", User.class);
        }

        CommandContextUtil.getUserEntityManager(commandContext).setUserPicture(user, picture);
        return null;
    }

}
