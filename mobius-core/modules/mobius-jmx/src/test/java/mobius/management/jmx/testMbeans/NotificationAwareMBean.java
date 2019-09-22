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

package mobius.management.jmx.testMbeans;

import mobius.management.jmx.NotificationSender;
import mobius.management.jmx.annotations.ManagedAttribute;
import mobius.management.jmx.annotations.ManagedOperation;
import mobius.management.jmx.annotations.ManagedResource;
import mobius.management.jmx.annotations.NotificationSenderAware;

/**
 * @author Saeid Mirzaei
 */

@ManagedResource(description = "test description")
public class NotificationAwareMBean implements NotificationSenderAware {

    NotificationSender capturedSender;

    @ManagedAttribute(description = "test attribute String description")
    public String getTestAttributeString() {
        return null;

    }

    @ManagedAttribute(description = "test attribute Boolean description")
    public Boolean isTestAttributeBoolean() {
        return null;
    }

    @ManagedOperation(description = "test operation description")
    public void getTestOperation() {

    }

    @Override
    public void setNotificationSender(NotificationSender sender) {
        capturedSender = sender;
    }

}
