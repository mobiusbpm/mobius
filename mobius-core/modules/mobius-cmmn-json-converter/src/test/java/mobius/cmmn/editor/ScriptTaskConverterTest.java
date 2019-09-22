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
import static org.junit.Assert.assertNull;

import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemControl;
import mobius.cmmn.model.Stage;

/**
 * @author Joram Barrez
 */
public class ScriptTaskConverterTest extends AbstractConverterTest {

    @Override
    protected String getResource() {
        return "test.scriptTaskModel.json";
    }
    
    @Override
    protected void validateModel(CmmnModel cmmnModel) {
        Stage planModel = cmmnModel.getPrimaryCase().getPlanModel();
        assertEquals(2, planModel.getPlanItemDefinitionMap().size());
        
        PlanItem planItemA = planModel.getPlanItems().stream().filter(p -> p.getName().equals("A")).findFirst().get();
        assertEquals("A", planItemA.getName());
        assertNull(planItemA.getItemControl());
        
        PlanItem planItemB = planModel.getPlanItems().stream().filter(p -> p.getName().equals("B")).findFirst().get();
        assertEquals("B", planItemB.getName());
        PlanItemControl planItemControlB = planItemB.getItemControl();
        assertNotNull(planItemControlB.getRequiredRule());
        assertNotNull(planItemControlB.getRepetitionRule());
        assertNotNull(planItemControlB.getManualActivationRule());
    }

}