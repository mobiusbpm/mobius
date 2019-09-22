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

import mobius.cmmn.model.CmmnModel;
import mobius.cmmn.model.GenericEventListener;
import mobius.cmmn.model.HumanTask;
import org.junit.Test;

/**
 * @author Tijs Rademakers
 */
public class GenericEventListenerCmmnXmlConverterTest extends AbstractConverterTest {

    private static final String CMMN_RESOURCE = "mobius/test/cmmn/converter/generic-event-listener.cmmn";

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

        List<HumanTask> humanTasks = cmmnModel.getPrimaryCase().getPlanModel().findPlanItemDefinitionsOfType(HumanTask.class, true);
        assertEquals(2, humanTasks.size());

        List<GenericEventListener> genericEventListeners = cmmnModel.getPrimaryCase().getPlanModel().findPlanItemDefinitionsOfType(GenericEventListener.class, true);
        assertEquals(1, genericEventListeners.size());

        GenericEventListener genericEventListener = genericEventListeners.get(0);
        assertEquals("myGenericEventListener", genericEventListener.getName());
        assertEquals("genericActionListener",genericEventListener.getId());
        assertEquals("GenericEventListener documentation", genericEventListener.getDocumentation());
    }
}
