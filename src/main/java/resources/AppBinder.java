package resources;

import database.Connection;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import services.CurriculumService;

import javax.inject.Singleton;

public class AppBinder extends AbstractBinder {

    @Override
    protected void configure() {
        //Connection conn = new Connection();
        bind(Connection.class).to(Connection.class).in(Singleton.class);
        bind(CurriculumService.class).to(CurriculumService.class).in(Singleton.class);
    }
}
