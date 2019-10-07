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

import javax.xml.stream.XMLStreamReader;

import mobius.cmmn.model.BaseElement;
import mobius.cmmn.model.PlanItemDefinition;
import mobius.cmmn.model.Stage;

/**
 *
 */
public abstract class PlanItemDefinitionXmlConverter extends CaseElementXmlConverter {
    
    @Override
    public abstract String getXMLElementName();
    
    @Override
    public BaseElement convertToCmmnModel(XMLStreamReader xtr, ConversionHelper conversionHelper) {
        PlanItemDefinition planItemDefinition = (PlanItemDefinition) super.convertToCmmnModel(xtr, conversionHelper);
        conversionHelper.addPlanItemDefinition(planItemDefinition);
        if (planItemDefinition.getId() != null) {
            Stage parentStage = planItemDefinition.getParentStage();
            if (parentStage != null) {
                parentStage.addPlanItemDefinition(planItemDefinition);
            }
        }   
        return planItemDefinition;
    }
    
}
