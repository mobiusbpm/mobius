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
import mobius.cmmn.model.CaseElement;
import mobius.cmmn.model.Stage;
import mobius.common.engine.api.FlowableException;

/**
 *
 */
public abstract class CaseElementXmlConverter extends BaseCmmnXmlConverter {
    
    @Override
    public abstract String getXMLElementName();
    
    @Override
    public BaseElement convertToCmmnModel(XMLStreamReader xtr, ConversionHelper conversionHelper) {
        BaseElement baseElement = super.convertToCmmnModel(xtr, conversionHelper);
        if (baseElement != null && !(baseElement instanceof CaseElement)) {
            throw new FlowableException(this.getClass() + " error: element is not a CaseElement : " + baseElement.getClass());
        }
        CaseElement caseElement = (CaseElement) baseElement;
        if (caseElement != null) {
            conversionHelper.addCaseElement(caseElement);
            if (!(caseElement instanceof Stage)) {
                caseElement.setParent(conversionHelper.getCurrentPlanFragment());
            } 
        }
        return caseElement;
    }
    
}
