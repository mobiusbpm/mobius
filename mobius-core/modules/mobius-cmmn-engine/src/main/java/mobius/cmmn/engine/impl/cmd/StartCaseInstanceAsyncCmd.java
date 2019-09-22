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

import mobius.cmmn.api.runtime.CaseInstance;
import mobius.cmmn.api.runtime.CaseInstanceBuilder;
import mobius.cmmn.engine.impl.persistence.entity.CaseInstanceEntity;
import mobius.cmmn.engine.impl.util.CommandContextUtil;
import mobius.common.engine.api.FlowableIllegalArgumentException;
import mobius.common.engine.impl.interceptor.Command;
import mobius.common.engine.impl.interceptor.CommandContext;

/**
 * @author martin.grofcik
 */
public class StartCaseInstanceAsyncCmd implements Command<CaseInstance>, Serializable {

    protected CaseInstanceBuilder caseInstanceBuilder;

    public StartCaseInstanceAsyncCmd(CaseInstanceBuilder caseInstanceBuilder) {
        this.caseInstanceBuilder = caseInstanceBuilder;
    }

    @Override
    public CaseInstance execute(CommandContext commandContext) {
        if (caseInstanceBuilder != null) {
            CaseInstanceEntity caseInstanceEntity = CommandContextUtil.getCmmnEngineConfiguration(commandContext).getCaseInstanceHelper()
                .startCaseInstanceAsync(caseInstanceBuilder);
            return caseInstanceEntity;
        } else {
            throw new FlowableIllegalArgumentException("Cannot start case instance: no case instance builder provided");
        }
    }

}
