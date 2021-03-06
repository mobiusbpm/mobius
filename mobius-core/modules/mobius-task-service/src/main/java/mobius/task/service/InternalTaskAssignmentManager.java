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

package mobius.task.service;

import java.util.List;

import mobius.identitylink.api.IdentityLink;
import mobius.task.api.Task;

/**
 *
 */
public interface InternalTaskAssignmentManager {
    
    void changeAssignee(Task task, String assignee);
    
    void changeOwner(Task task, String owner);
    
    void addCandidateUser(Task task, IdentityLink identityLink);

    void addCandidateUsers(Task task, List<IdentityLink> candidateUsers);
    
    void addCandidateGroup(Task task, IdentityLink identityLink);

    void addCandidateGroups(Task task, List<IdentityLink> candidateGroups);
    
    void addUserIdentityLink(Task task, IdentityLink identityLink);

    void addGroupIdentityLink(Task task, IdentityLink identityLink);

    void deleteUserIdentityLink(Task task, IdentityLink identityLink);
    
    void deleteGroupIdentityLink(Task task, IdentityLink identityLink);
}
