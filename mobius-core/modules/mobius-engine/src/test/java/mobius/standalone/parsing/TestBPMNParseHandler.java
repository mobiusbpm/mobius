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

package mobius.standalone.parsing;

import mobius.bpmn.model.BaseElement;
import mobius.bpmn.model.Process;
import mobius.engine.impl.bpmn.parser.BpmnParse;
import mobius.engine.impl.bpmn.parser.handler.AbstractBpmnParseHandler;

/**
 *
 *
 */
public class TestBPMNParseHandler extends AbstractBpmnParseHandler<Process> {

    @Override
    protected Class<? extends BaseElement> getHandledType() {
        return Process.class;
    }

    @Override
    protected void executeParse(BpmnParse bpmnParse, Process process) {
        // Change the key of all deployed process-definitions
        process.setId(bpmnParse.getCurrentProcessDefinition().getKey() + "-modified");
        bpmnParse.getCurrentProcessDefinition().setKey(bpmnParse.getCurrentProcessDefinition().getKey() + "-modified");
    }

}
