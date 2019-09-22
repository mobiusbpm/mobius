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
package mobius.engine.impl.bpmn.parser.handler;

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.HttpServiceTask;
import mobius.bpmn.model.ServiceTask;
import mobius.engine.impl.bpmn.parser.BpmnParse;

/**
 * @author Tijs Rademakers
 */
public class HttpServiceTaskParseHandler extends AbstractActivityBpmnParseHandler<ServiceTask> {

    @Override
    public Class<? extends BaseElement> getHandledType() {
        return HttpServiceTask.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, ServiceTask serviceTask) {
        serviceTask.setBehavior(bpmnParse.getActivityBehaviorFactory().createHttpActivityBehavior(serviceTask));
    }
}
