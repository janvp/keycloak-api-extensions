package org.keycloak.extensions.api.groups;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.services.resource.RealmResourceProvider;
import org.keycloak.services.resource.RealmResourceProviderFactory;

import java.util.*;

public class GroupsResourceProviderFactory implements RealmResourceProviderFactory {

    // This will result in a new sub path under an existing realm
    // eg. http://localhost:8080/auth/realms/realm-name/groups
    public static final String ID = "groups";

    private Map<String,List<String>> roles = new HashMap<String,List<String>>();

    @Override
    public RealmResourceProvider create(KeycloakSession keycloakSession) {
        return new GroupsResourceProvider(keycloakSession, roles);
    }

    @Override
    public void init(Config.Scope config) {
        String viewMembersConfig = config.get("roles.canViewMembers");
        List<String> viewMembersRoles = Collections.<String>emptyList();
        if (viewMembersConfig != null) {
            viewMembersRoles = Arrays.asList(viewMembersConfig.split(","));
        }
        roles.put("viewMembers", viewMembersRoles);

        String viewMembersOwnGroupsConfig = config.get("roles.canViewMembersOfOwnGroups");
        List<String> viewMembersOwnGroupsRoles = Collections.<String>emptyList();
        if (viewMembersOwnGroupsConfig != null) {
            viewMembersOwnGroupsRoles = Arrays.asList(viewMembersOwnGroupsConfig.split(","));
        }
        roles.put("viewMembersOfOwnGroups", viewMembersOwnGroupsRoles);
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
