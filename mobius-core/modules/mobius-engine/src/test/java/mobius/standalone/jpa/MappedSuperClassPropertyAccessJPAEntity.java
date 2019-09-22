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

package mobius.standalone.jpa;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Mapped superclass containing an
 * 
 * @author Frederik Heremans
 */

@MappedSuperclass
public class MappedSuperClassPropertyAccessJPAEntity {

    private Long id;

    @Id
    @Column(name = "ID_")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
