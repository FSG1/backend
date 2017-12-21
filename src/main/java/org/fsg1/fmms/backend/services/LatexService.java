package org.fsg1.fmms.backend.services;

import org.fsg1.fmms.backend.database.Connection;

import javax.inject.Inject;

public class LatexService extends Service {

    /**
     * Constructor. Takes a connection object which it uses to query a database.
     *
     * @param connection The connection object.
     */
    @Inject
    LatexService(final Connection connection) {
        super(connection);
    }






}

