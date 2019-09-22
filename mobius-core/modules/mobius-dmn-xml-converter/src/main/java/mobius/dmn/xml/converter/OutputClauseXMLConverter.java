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

import mobius.dmn.model.DecisionTable;
import mobius.dmn.model.DmnDefinition;
import mobius.dmn.model.DmnElement;
import mobius.dmn.model.InputClause;
import mobius.dmn.model.OutputClause;

import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

/**
 * @author Yvo Swillens
 */
public class OutputClauseXMLConverter extends BaseDmnXMLConverter {

    @Override
    public Class<? extends DmnElement> getDmnElementType() {
        return InputClause.class;
    }

    @Override
    protected String getXMLElementName() {
        return ELEMENT_OUTPUT_CLAUSE;
    }

    @Override
    protected DmnElement convertXMLToElement(XMLStreamReader xtr, DmnDefinition model, DecisionTable decisionTable) throws Exception {
        OutputClause clause = new OutputClause();
        clause.setId(xtr.getAttributeValue(null, ATTRIBUTE_ID));
        clause.setLabel(xtr.getAttributeValue(null, ATTRIBUTE_LABEL));
        clause.setName(xtr.getAttributeValue(null, ATTRIBUTE_NAME));
        clause.setTypeRef(xtr.getAttributeValue(null, ATTRIBUTE_TYPE_REF));
        parseChildElements(getXMLElementName(), clause, decisionTable, xtr);
        return clause;
    }

    @Override
    protected void writeAdditionalAttributes(DmnElement element, DmnDefinition model, XMLStreamWriter xtw) throws Exception {

    }

    @Override
    protected void writeAdditionalChildElements(DmnElement element, DmnDefinition model, XMLStreamWriter xtw) throws Exception {

    }

}
