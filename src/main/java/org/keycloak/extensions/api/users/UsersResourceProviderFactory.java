package org.keycloak.extensions.api.users;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import java.util.*;

public class UsersResourceProviderFactory implements RealmResourceProviderFactory {

    // This will result in a new sub path under an existing realm
    // eg. http://localhost:8080/auth/realms/realm-name/users
    public static final String ID = "users";

    private Map<String,List<String>> roles = new HashMap<String,List<String>>();

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new UsersResourceProvider(keycloakSession, roles);
    }

    @Override
    public void init(Config.Scope config) {
        String viewRolesConfig = config.get("roles.canView");
        List<String> viewRoles = Collections.<String>emptyList();
        if (viewRolesConfig != null) {
            viewRoles = Arrays.asList(viewRolesConfig.split(","));
        }
        roles.put("view", viewRoles);
    }

    @Override
    public void postInit(KeycloakSessionFactory keycloakSessionFactory) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return ID;
    }
}
