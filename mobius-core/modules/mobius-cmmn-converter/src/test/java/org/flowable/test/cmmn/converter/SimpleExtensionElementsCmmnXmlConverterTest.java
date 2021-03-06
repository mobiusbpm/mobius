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
package mobius.test.cmmn.converter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import mobius.cmmn.model.Case;
import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.ExtensionElement;
import mobius.cmmn.model.Milestone;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.Sentry;
import mobius.cmmn.model.Stage;
import mobius.cmmn.model.Task;
import org.junit.Test;

/**
 *
 */
public class SimpleExtensionElementsCmmnXmlConverterTest extends AbstractConverterTest {

    private static final String CMMN_RESOURCE = "mobius/test/cmmn/converter/simple_extensionelements.cmmn";

    @Test
    public void convertXMLToModel() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        validateModel(cmmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        CmmnModel parsedModel = exportAndReadXMLFile(cmmnModel);
        validateModel(parsedModel);
    }

    public void validateModel(CmmnModel cmmnModel) {
        assertNotNull(cmmnModel);
        assertEquals(1, cmmnModel.getCases().size());

        // Case
        Case caze = cmmnModel.getCases().get(0);
        assertEquals("myCase", caze.getId());

        // Plan model
        Stage planModel = caze.getPlanModel();
        assertNotNull(planModel);
        assertEquals("myPlanModel", planModel.getId());
        assertEquals("My CasePlanModel", planModel.getName());
        assertEquals("formKey", planModel.getFormKey());
        assertEquals("formFieldValidationValue", planModel.getValidateFormFields());

        Task task = (Task) planModel.findPlanItemDefinitionInStageOrUpwards("taskA");
        assertEquals(1, task.getExtensionElements().size());
        List<ExtensionElement> extensionElements = task.getExtensionElements().get("taskTest");
        assertEquals(1, extensionElements.size());
        ExtensionElement extensionElement = extensionElements.get(0);
        assertEquals("taskTest", extensionElement.getName());
        assertEquals("hello", extensionElement.getElementText());
        
        Milestone milestone = (Milestone) planModel.findPlanItemDefinitionInStageOrUpwards("mileStoneOne");
        assertEquals(1, milestone.getExtensionElements().size());
        extensionElements = milestone.getExtensionElements().get("milestoneTest");
        assertEquals(1, extensionElements.size());
        extensionElement = extensionElements.get(0);
        assertEquals("milestoneTest", extensionElement.getName());
        assertEquals("hello2", extensionElement.getElementText());
        
        PlanItem planItem = planModel.findPlanItemInPlanFragmentOrDownwards("planItemTaskA");
        assertEquals(1, planItem.getExtensionElements().size());
        extensionElements = planItem.getExtensionElements().get("test");
        assertEquals(1, extensionElements.size());
        extensionElement = extensionElements.get(0);
        assertEquals("test", extensionElement.getName());
        assertEquals("test", extensionElement.getElementText());

        List<Sentry> sentries = planModel.getSentries();
        assertEquals(3, sentries.size());
        Sentry sentry = sentries.get(0);
        assertEquals(1, sentry.getExtensionElements().size());
        extensionElements = sentry.getExtensionElements().get("test2");
        assertEquals(1, extensionElements.size());
        extensionElement = extensionElements.get(0);
        assertEquals("test2", extensionElement.getName());
        assertEquals("test2", extensionElement.getElementText());
    }

}
