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
package mobius.scripting.secure.listener;

import mobius.engine.impl.bpmn.listener.ScriptTaskListener;
import mobius.scripting.secure.behavior.SecureJavascriptTaskParseHandler;
import mobius.scripting.secure.impl.SecureJavascriptUtil;
import mobius.task.service.delegate.DelegateTask;

/**
 *
 */
public class SecureJavascriptTaskListener extends ScriptTaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        validateParameters();
        if (SecureJavascriptTaskParseHandler.LANGUAGE_JAVASCRIPT.equalsIgnoreCase(language.getValue(delegateTask).toString())) {
            Object result = SecureJavascriptUtil.evaluateScript(delegateTask, script.getExpressionText());
            if (resultVariable != null) {
                delegateTask.setVariable(resultVariable.getExpressionText(), result);
            }
        } else {
            super.notify(delegateTask);
        }
    }

}
