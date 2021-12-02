package org.keycloak.extensions.api.groups;

import org.keycloak.models.KeycloakSession;
import org.keycloak.services.resource.RealmResourceProvider;

import java.util.List;
import java.util.Map;


public class GroupsResourceProvider implements RealmResourceProvider {

    private final KeycloakSession session;
    
    private Map<String,List<String>> roles;

    public GroupsResourceProvider(KeycloakSession session, Map<String,List<String>> roles) {
        this.session = session;
        this.roles = roles;
    }

    @Override
    public Object getResource() {
        return new GroupsResource(session, roles);
    }

    @Override
    public void close() {
    }
}
