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

import mobius.cmmn.model.CmmnElement;
import mobius.cmmn.model.ProcessTask;

/**
 *
 */
public class ProcessTaskXmlConverter extends TaskXmlConverter {
    
    @Override
    public String getXMLElementName() {
        return CmmnXmlConstants.ELEMENT_PROCESS_TASK;
    }

    @Override
    protected CmmnElement convert(XMLStreamReader xtr, ConversionHelper conversionHelper) {
        ProcessTask processTask = new ProcessTask();
        convertCommonTaskAttributes(xtr, processTask);
        processTask.setProcessRef(xtr.getAttributeValue(null, CmmnXmlConstants.ATTRIBUTE_PROCESS_REF));
        
        String fallbackToDefaultTenantValue = xtr.getAttributeValue(CmmnXmlConstants.FLOWABLE_EXTENSIONS_NAMESPACE, CmmnXmlConstants.ATTRIBUTE_FALLBACK_TO_DEFAULT_TENANT);
        if (fallbackToDefaultTenantValue != null) {
            processTask.setFallbackToDefaultTenant(Boolean.parseBoolean(fallbackToDefaultTenantValue));
        }
        
        return processTask;
    }
    
}