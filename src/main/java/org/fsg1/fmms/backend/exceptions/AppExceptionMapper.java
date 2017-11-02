package org.fsg1.fmms.backend.exceptions;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class AppExceptionMapper implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception ex) {
        if (ex instanceof AppException) {
            return Response.status(((AppException) (ex)).getStatus())
                    .entity(ex)
                    .build();
        }
        return Response.status(500)
                .entity(ex)
                .build();
    }
}

