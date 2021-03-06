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
package mobius.form.engine.impl.cmd;

import java.io.Serializable;

import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;
import mobius.form.engine.impl.persistence.entity.FormInstanceEntity;
import mobius.form.engine.impl.util.CommandContextUtil;

public class GetFormInstanceValuesCmd implements Command<byte[]>, Serializable {

    private static final long serialVersionUID = 1L;
    
    protected String formInstanceId;

    public GetFormInstanceValuesCmd(String formInstanceId) {
        this.formInstanceId = formInstanceId;
    }

    @Override
    public byte[] execute(CommandContext commandContext) {
        if (formInstanceId == null) {
            throw new FlowableIllegalArgumentException("formInstanceId is null");
        }

        FormInstanceEntity formInstance = CommandContextUtil.getFormInstanceEntityManager(commandContext).findById(formInstanceId);
        if (formInstance != null && formInstance.getFormValuesId() != null) {
            return formInstance.getFormValueBytes();
        }
        
        return null;
    }
}