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
package mobius.cmmn.engine.impl.callback;

import mobius.cmmn.api.runtime.CaseInstanceState;
import mobius.cmmn.engine.CmmnEngineConfiguration;
import mobius.common.engine.impl.callback.CallbackData;
import mobius.common.engine.impl.callback.RuntimeInstanceStateChangeCallback;

/**
 * Callback implementation for a child case instance returning it's state change to its parent.
 *
 *
 */
public class ChildProcessInstanceStateChangeCallback implements RuntimeInstanceStateChangeCallback {

    protected CmmnEngineConfiguration cmmnEngineConfiguration;
    
    public ChildProcessInstanceStateChangeCallback(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
    }
    
    @Override
    public void stateChanged(CallbackData callbackData) {
        if (CaseInstanceState.COMPLETED.equals(callbackData.getNewState()) || "cancelled".equals(callbackData.getNewState())) {
            cmmnEngineConfiguration.getCmmnRuntimeService().triggerPlanItemInstance(callbackData.getCallbackId());
        }
    }
    
}
