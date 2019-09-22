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
package mobius.cmmn.editor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemDefinition;
import mobius.cmmn.model.ProcessTask;
import mobius.cmmn.model.Stage;

/**
 * @author martin.grofcik
 */
public class ProcessTaskJsonConverterTest extends AbstractConverterTest {
    @Override
    protected String getResource() {
        return "test.processTaskModel.json";
    }

    @Override
    protected void validateModel(CmmnModel model) {
        Case caseModel = model.getPrimaryCase();
        assertEquals("processTaskModelId", caseModel.getId());
        assertEquals("processTaskModelName", caseModel.getName());

        Stage planModelStage = caseModel.getPlanModel();
        assertNotNull(planModelStage);
        assertEquals("casePlanModel", planModelStage.getId());

        PlanItem planItem = planModelStage.findPlanItemInPlanFragmentOrUpwards("planItem1");
        assertNotNull(planItem);
        assertEquals("planItem1", planItem.getId());
        assertEquals("processTaskName", planItem.getName());
        PlanItemDefinition planItemDefinition = planItem.getPlanItemDefinition();
        assertNotNull(planItemDefinition);
        assertTrue(planItemDefinition instanceof ProcessTask);
        ProcessTask processTask = (ProcessTask) planItemDefinition;
        assertEquals("sid-5E1BEB30-72F7-463C-A1CB-77F000CA7E0F", processTask.getId());
        assertEquals("processTaskName", processTask.getName());
        assertTrue(processTask.getFallbackToDefaultTenant());
    }
}
