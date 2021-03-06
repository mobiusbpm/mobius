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
package mobius.cmmn.model;

public class CaseElement extends CmmnElement {
    
    protected String name;
    protected PlanFragment parent;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PlanFragment getParent() {
        return parent;
    }

    public void setParent(PlanFragment parent) {
        this.parent = parent;
    }
    
    public Stage getParentStage() {
        if (parent instanceof Stage) {
            return (Stage) parent;
        } else if (parent != null){
            return parent.getParentStage();
        }
        return null;
    }
    
}
