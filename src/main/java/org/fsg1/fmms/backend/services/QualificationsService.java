package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

public class QualificationsService extends Service {

    @Inject
    QualificationsService(Connection connection) {
        super(connection);
    }

    public String getQualificationsQuery() {
        return
                "";
    }
}
