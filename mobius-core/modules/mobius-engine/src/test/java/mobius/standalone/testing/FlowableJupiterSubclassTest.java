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
package mobius.standalone.testing;

/**
 * This test is explicitly empty.
 * <p>
 * The purpose of this test is to make sure that FlowableExtensions works with tests methods that are defined in a parent class.
 * <p>
 * When running the test suite over this class, JUnit will execute the test from {@link FlowableJupiterTest}.
 * This means that the code in {@link mobius.engine.impl.test.TestHelper} needs to be able to find the test method even when it's not declared on the test class itself.
 * <p>
 * Specifically, {@link mobius.engine.impl.test.TestHelper} needs to call getMethod() rather than getDeclaredMethod() since the method is declared in a parent of the actual test class.
 *
 *
 */
class FlowableJupiterSubclassTest extends FlowableJupiterTest {

}
