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
package mobius.examples.bpmn.tasklistener;

import java.util.Map;

import mobius.engine.delegate.TaskListener;
import mobius.engine.impl.persistence.entity.ExecutionEntity;
import mobius.engine.impl.util.ExecutionHelper;
import mobius.task.service.delegate.DelegateTask;

/**
 * @author Falko Menge <falko.menge@camunda.com>
 */
public class AssigneeOverwriteFromVariable implements TaskListener {

    @Override
    @SuppressWarnings("unchecked")
    public void notify(DelegateTask delegateTask) {
        // get mapping table from variable
        ExecutionEntity execution = ExecutionHelper.getExecution(delegateTask.getExecutionId());
        Map<String, String> assigneeMappingTable = (Map<String, String>) execution.getVariable("assigneeMappingTable");

        // get assignee from process
        String assigneeFromProcessDefinition = delegateTask.getAssignee();

        // overwrite assignee if there is an entry in the mapping table
        if (assigneeMappingTable.containsKey(assigneeFromProcessDefinition)) {
            String assigneeFromMappingTable = assigneeMappingTable.get(assigneeFromProcessDefinition);
            delegateTask.setAssignee(assigneeFromMappingTable);
        }
    }

}
