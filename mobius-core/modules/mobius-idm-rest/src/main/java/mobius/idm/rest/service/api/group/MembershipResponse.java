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

package mobius.idm.rest.service.api.group;

import io.swagger.annotations.ApiModelProperty;

/**
 *
 */
public class MembershipResponse extends MembershipRequest {

    protected String url;
    protected String groupId;

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    @ApiModelProperty(example = "sales")
    public String getGroupId() {
        return groupId;
    }

    @ApiModelProperty(example = "http://localhost:8182/groups/sales/members/userId")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
