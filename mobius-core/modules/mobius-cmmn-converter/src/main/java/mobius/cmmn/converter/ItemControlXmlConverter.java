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
package mobius.cmmn.converter;

import mobius.cmmn.model.CmmnElement;
import mobius.cmmn.model.PlanItem;
import mobius.cmmn.model.PlanItemControl;

import javax.xml.stream.XMLStreamReader;

/**
 *
 */
public class ItemControlXmlConverter extends CaseElementXmlConverter {
    
    @Override
    public String getXMLElementName() {
        return CmmnXmlConstants.ELEMENT_ITEM_CONTROL;
    }
    
    @Override
    public boolean hasChildElements() {
        return true;
    }

    @Override
    protected CmmnElement convert(XMLStreamReader xtr, ConversionHelper conversionHelper) {
        PlanItemControl planItemControl = new PlanItemControl();
        CmmnElement currentCmmnElement = conversionHelper.getCurrentCmmnElement();
        if (currentCmmnElement instanceof PlanItem) {
            ((PlanItem) currentCmmnElement).setItemControl(planItemControl);
        }
        return planItemControl;
    }
    
}