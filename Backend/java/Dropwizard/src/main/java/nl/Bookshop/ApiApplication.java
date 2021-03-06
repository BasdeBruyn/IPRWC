package nl.Bookshop;

import io.dropwizard.Application;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.auth.basic.BasicCredentialAuthFilter;
import io.dropwizard.db.PooledDataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.hibernate.UnitOfWorkAwareProxyFactory;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import nl.Bookshop.exceptions.ClientExceptionHandler;
import nl.Bookshop.models.CartItem;
import nl.Bookshop.models.Order;
import nl.Bookshop.models.User;
import nl.Bookshop.persistance.CartDao;
import nl.Bookshop.persistance.OrderDao;
import nl.Bookshop.resources.CartResource;
import nl.Bookshop.resources.OrderResource;
import nl.Bookshop.models.Item;
import nl.Bookshop.persistance.ItemDao;
import nl.Bookshop.persistance.UserDao;
import nl.Bookshop.resources.ItemResource;
import nl.Bookshop.resources.UserResource;
import nl.Bookshop.services.AuthService;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.hibernate.SessionFactory;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.EnumSet;

public class ApiApplication extends Application<APiConfiguration> {

    public static void main(String[] args) throws Exception {
        new ApiApplication().run(args);
    }

    private final HibernateBundle<APiConfiguration> hibernate
            = new HibernateBundle<APiConfiguration>(Item.class, User.class, Order.class, CartItem.class) {
        @Override
        public PooledDataSourceFactory getDataSourceFactory(APiConfiguration configuration) {
            return configuration.getDataSourceFactory();
        }
    };

    @Override
    public void initialize(Bootstrap<APiConfiguration> bootstrap) {
        bootstrap.addBundle(hibernate);
    }

    @Override
    public void run(APiConfiguration config, Environment environment){
        setupCORS(environment);

        initDaos(hibernate.getSessionFactory());

        registerResources(environment);

        environment.jersey().register(new ClientExceptionHandler());

        setupAuthentication(environment);
    }

    private void initDaos(SessionFactory sessionFactory) {
        CartDao.initInstance(sessionFactory);
        ItemDao.initInstance(sessionFactory);
        OrderDao.initInstance(sessionFactory);
        UserDao.initInstance(sessionFactory);
    }

    private void registerResources(Environment environment) {
        environment.jersey().register(new ItemResource());
        environment.jersey().register(new UserResource());
        environment.jersey().register(new OrderResource());
        environment.jersey().register(new CartResource());
    }

    private void setupAuthentication(Environment environment){
        AuthService authService =
                new UnitOfWorkAwareProxyFactory(hibernate)
                .create(AuthService.class);
        environment.jersey().register(new AuthDynamicFeature(
                new BasicCredentialAuthFilter.Builder<User>()
                        .setAuthenticator(authService)
                        .setAuthorizer(authService)
                        .setRealm("Basic")
                        .buildAuthFilter())
        );
        environment.jersey().register(RolesAllowedDynamicFeature.class);
        environment.jersey().register(new AuthValueFactoryProvider.Binder<>(User.class));
    }

    private void setupCORS(Environment environment) {
        final FilterRegistration.Dynamic cors =
                environment.servlets().addFilter("CORS", CrossOriginFilter.class);

        // Configure CORS parameters
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");

        // Add URL mapping
        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
    }
}
