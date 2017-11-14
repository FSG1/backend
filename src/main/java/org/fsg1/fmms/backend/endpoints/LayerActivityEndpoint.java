package org.fsg1.fmms.backend.endpoints;

import com.fasterxml.jackson.databind.JsonNode;
import org.fsg1.fmms.backend.services.LayerActivityService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The class containing the architecturallayer/activity endpoints.
 */
@Path("curriculum/{curriculum_id}/architecturallayer/{layer_id}/activity/{activity_id}")
public class LayerActivityEndpoint extends Endpoint {
    /**
     * Constructor which receives the service as dependency. In subclasses this dependency is automatically
     * injected by Jersey's DPI system.
     *
     * @param service Service object.
     */
    @Inject
    LayerActivityEndpoint(final LayerActivityService service) {
        super(service);
    }

    /**
     * Get the complete overview of a specific qualification which is the combination of an architectural layer
     * and lifecycle activity as defined by the HBO-I matrix.
     *
     * @param curriculumId Identifier of the curriculum.
     * @param layerId      The number of the architectural layer.
     * @param activityId   The number of the lifecycle activity.
     * @return A JSON Object of the whole qualifications overview.
     * @throws Exception In case the querying goes wrong.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getQualificationsOverview(@PathParam("curriculum_id") final int curriculumId,
                                              @PathParam("layer_id") final int layerId,
                                              @PathParam("activity_id") final int activityId) throws Exception {
        final LayerActivityService service = (LayerActivityService) getService();
        final JsonNode result = service.get(service.getQueryQualificationsOverview(), "qualifications_overview", layerId, activityId, curriculumId);
        final String jsonString = result.toString();
        return Response.status(Response.Status.OK).entity(jsonString).build();
    }
}
