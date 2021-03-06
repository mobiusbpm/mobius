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
package mobius.dmn.model;

/**
 * @author Yvo Swillens
 */
public class InputClause extends DmnElement {

    protected LiteralExpression inputExpression;

    protected UnaryTests inputValues;

    protected int inputNumber;

    public LiteralExpression getInputExpression() {
        return inputExpression;
    }

    public void setInputExpression(LiteralExpression inputExpression) {
        this.inputExpression = inputExpression;
    }

    public UnaryTests getInputValues() {
        return inputValues;
    }

    public void setInputValues(UnaryTests inputValues) {
        this.inputValues = inputValues;
    }

    public int getInputNumber() {
        return inputNumber;
    }

    public void setInputNumber(int inputNumber) {
        this.inputNumber = inputNumber;
    }
}
