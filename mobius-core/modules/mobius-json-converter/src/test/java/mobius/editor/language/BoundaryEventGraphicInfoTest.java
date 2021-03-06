/*
 *  Licensed under the Apache License, Version 2.0 (the "License");
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
package mobius.editor.language;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import mobius.bpmn.model.BoundaryEvent;
import mobius.bpmn.model.BpmnModel;
import mobius.bpmn.model.GraphicInfo;
import mobius.editor.json.converter.BpmnJsonConverter;
import org.junit.Test;

import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Created by David Pardo
 */
public class BoundaryEventGraphicInfoTest extends AbstractConverterTest {
    final String TIMER_BOUNDERY_ID = "sid-4F284555-6D97-4F67-A926-C96F552A4404";
    final String USER_TASK_ID = "sid-6A39AD39-C7BB-4D92-896B-CBF37D5D449B";

    @Test
    public void graphicInfoOfBoundaryEventShouldRemainTheSame() throws Exception {
        BpmnModel model = readJsonFile();
        validate(model);
        model = convertToJsonAndBack(model);
        validate(model);
    }

    @Test
    public void dockerInfoShouldRemainIntact() throws  Exception{
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(getResource());
        JsonNode model = new ObjectMapper().readTree(stream);
        BpmnModel bpmnModel = new BpmnJsonConverter().convertToBpmnModel(model);
        model = new BpmnJsonConverter().convertToJson(bpmnModel);
        validate(model);
    }

    protected void validate(JsonNode model){
        ArrayNode node = (ArrayNode) model.path("childShapes");
        JsonNode boundaryEventNode = null;

        for(JsonNode shape: node){
            String resourceId = shape.path("resourceId").asText();
            if(TIMER_BOUNDERY_ID.equals(resourceId)){
                boundaryEventNode = shape;
            }
        }

        //validate docker nodes
        Double x = boundaryEventNode.path("dockers").get(0).path("x").asDouble();
        Double y = boundaryEventNode.path("dockers").get(0).path("y").asDouble();

        //the modeler does not store a mathematical correct docker point.
        assertThat(x,is(50.0));
        assertThat(y,is(80.0));
    }

    protected void validate(BpmnModel model) {
        BoundaryEvent event = (BoundaryEvent) model.getFlowElement(TIMER_BOUNDERY_ID);
        assertThat(event.getAttachedToRefId(), is(USER_TASK_ID));

        //check graphicinfo boundary
        GraphicInfo giBoundary = model.getGraphicInfo(TIMER_BOUNDERY_ID);
        assertThat(giBoundary.getX(), is(334.2201675394047));
        assertThat(giBoundary.getY(), is(199.79587432571776));
        assertThat(giBoundary.getHeight(), is(31.0));
        assertThat(giBoundary.getWidth(), is(31.0));

        //check graphicinfo task
        GraphicInfo giTaskOne = model.getGraphicInfo(USER_TASK_ID);
        assertThat(giTaskOne.getX(),is(300.0));
        assertThat(giTaskOne.getY(),is(135.0));
        assertThat(giTaskOne.getWidth(),is(100.0));
        assertThat(giTaskOne.getHeight(),is(80.0));
    }

    @Override
    protected String getResource() {
        return "test.boundaryeventgraphicinfo.json";
    }
}
