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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.DecisionTask;
import mobius.cmmn.model.FieldExtension;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemDefinition;
import mobius.cmmn.model.Stage;

/**
 * @author martin.grofcik
 */
public class DecisionTaskJsonConverterTest extends AbstractConverterTest {
    @Override
    protected String getResource() {
        return "test.dmnTaskModel.json";
    }

    @Override
    protected void validateModel(CmmnModel model) {
        Case caseModel = model.getPrimaryCase();
        assertEquals("dmnExportCase", caseModel.getId());
        assertEquals("dmnExportCase", caseModel.getName());

        Stage planModelStage = caseModel.getPlanModel();
        assertNotNull(planModelStage);
        assertEquals("casePlanModel", planModelStage.getId());

        PlanItem planItem = planModelStage.findPlanItemInPlanFragmentOrUpwards("planItem1");
        assertNotNull(planItem);
        assertEquals("planItem1", planItem.getId());
        assertEquals("dmnTask", planItem.getName());
        PlanItemDefinition planItemDefinition = planItem.getPlanItemDefinition();
        assertNotNull(planItemDefinition);
        assertTrue(planItemDefinition instanceof DecisionTask);
        DecisionTask decisionTask = (DecisionTask) planItemDefinition;
        assertEquals("sid-F4BCA0C7-8737-4279-B50F-59272C7C65A2", decisionTask.getId());
        assertEquals("dmnTask", decisionTask.getName());

        assertThat(decisionTask.getFieldExtensions())
            .extracting(FieldExtension::getFieldName, FieldExtension::getStringValue)
            .as("fieldName, stringValue")
            .contains(
                tuple("fallbackToDefaultTenant", "true"),
                tuple("decisionTaskThrowErrorOnNoHits", "false")
            );
    }

}
