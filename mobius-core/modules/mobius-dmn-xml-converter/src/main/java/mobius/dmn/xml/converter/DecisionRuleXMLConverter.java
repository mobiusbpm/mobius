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
package mobius.dmn.xml.converter;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import mobius.dmn.model.DecisionRule;
import mobius.dmn.model.DecisionTable;
import mobius.dmn.model.DmnDefinition;
import mobius.dmn.model.DmnElement;

/**
 *
 * @author Yvo Swillens
 */
public class DecisionRuleXMLConverter extends BaseDmnXMLConverter {

    @Override
    public Class<? extends DmnElement> getDmnElementType() {
        return DecisionRule.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_RULE;
    }

    @Override
    protected DmnElement convertXMLToElement(XMLStreamReader xtr, DmnDefinition model, DecisionTable decisionTable) throws Exception {
        DecisionRule rule = new DecisionRule();
        parseChildElements(getXMLElementName(), rule, decisionTable, xtr);
        return rule;
    }

    @Override
    protected void writeAdditionalAttributes(DmnElement element, DmnDefinition model, XMLStreamWriter xtw) throws Exception {

    }

    @Override
    protected void writeAdditionalChildElements(DmnElement element, DmnDefinition model, XMLStreamWriter xtw) throws Exception {

    }

}
