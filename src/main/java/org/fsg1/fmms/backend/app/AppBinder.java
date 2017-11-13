package org.fsg1.fmms.backend.app;

import org.apache.commons.dbcp2.BasicDataSource;
import org.fsg1.fmms.backend.database.Connection;
import org.fsg1.fmms.backend.services.*;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

/**
 * The AppBinder. This resolves dependencies using Jersey's HK2 DI.
 */
public final class AppBinder extends AbstractBinder {

    /**
     * Configures dependencies. Dependencies can be shared in a Singleton, recreated for every request
     * and more. Read HK2's DI documentation.
     */
    @Override
    protected void configure() {
        bind(BasicDataSource.class).to(BasicDataSource.class).in(Singleton.class);
        bind(Connection.class).to(Connection.class).in(Singleton.class);
        bind(CurriculaService.class).to(CurriculaService.class).in(Singleton.class);
        bind(LayerActivityService.class).to(LayerActivityService.class).in(Singleton.class);
        bind(ModulesService.class).to(ModulesService.class).in(Singleton.class);
        bind(SemestersService.class).to(SemestersService.class).in(Singleton.class);
        bind(QualificationsService.class).to(QualificationsService.class).in(Singleton.class);
    }
}
