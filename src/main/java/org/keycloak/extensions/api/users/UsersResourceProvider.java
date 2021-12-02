package org.keycloak.extensions.api.users;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.List;
import java.util.Map;


public class UsersResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    
    private Map<String,List<String>> roles;

    public UsersResourceProvider(KeycloakSession session, Map<String,List<String>> roles) {
        this.session = session;
        this.roles = roles;
    }

    @Override
    public Object getResource() {
        return new UsersResource(session, roles);
    }

    @Override
    public void close() {
    }
}
