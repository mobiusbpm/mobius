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
package mobius.variable.service.impl.types;

import mobius.variable.api.types.ValueFields;
import mobius.variable.api.types.VariableType;
import org.joda.time.LocalDate;

/**
 * @author Tijs Rademakers
 */
public class JodaDateType implements VariableType {

    public static final String TYPE_NAME = "jodadate";

    @Override
    public String getTypeName() {
        return TYPE_NAME;
    }

    @Override
    public boolean isCachable() {
        return true;
    }

    @Override
    public boolean isAbleToStore(Object value) {
        if (value == null) {
            return true;
        }
        return LocalDate.class.isAssignableFrom(value.getClass());
    }

    @Override
    public Object getValue(ValueFields valueFields) {
        Long longValue = valueFields.getLongValue();
        if (longValue != null) {
            return new LocalDate(longValue);
        }
        return null;
    }

    @Override
    public void setValue(Object value, ValueFields valueFields) {
        if (value != null) {
            valueFields.setLongValue(((LocalDate) value).toDateTimeAtStartOfDay().getMillis());
        } else {
            valueFields.setLongValue(null);
        }
    }
}