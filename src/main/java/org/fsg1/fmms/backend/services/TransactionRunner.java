package org.fsg1.fmms.backend.services;

import java.sql.Connection;

/**
 * Functional interface for functions to perform inside a database transaction.
 */
@FunctionalInterface
public interface TransactionRunner {

    /**
     * Method to perform.
     *
     * @param conn Connection to use.
     * @throws Exception If a database access error occurs or anything else goes wrong interacting with the database.
     */
    void run(Connection conn) throws Exception;
}
