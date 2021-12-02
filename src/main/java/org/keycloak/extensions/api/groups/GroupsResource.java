package org.keycloak.extensions.api.groups;

import org.keycloak.extensions.api.util.ModelToRepresentation;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class GroupsResource {

    protected KeycloakSession session;

    protected AuthenticationManager.AuthResult auth;

    protected RealmModel realm;

    protected Map<String,List<String>> roles;

    public GroupsResource(KeycloakSession session, Map<String,List<String>> roles) {
        this.session = session;
        this.roles = roles;
        this.realm = session.getContext().getRealm();
        this.auth = resolveAuthentication();
    }

    protected AuthenticationManager.AuthResult resolveAuthentication() {
        AppAuthManager appAuthManager = new AppAuthManager();
        AuthenticationManager.AuthResult authResult = appAuthManager.authenticateBearerToken(session, realm);

        if (authResult != null) {
            return authResult;
        }

        return null;
    }

    protected boolean canViewUsers(UserModel user, GroupModel group) {
        List<String> viewRoles = roles.get("viewMembers");
        
        if (viewRoles.isEmpty()) {
            return true;
        }

        for (String roleName : viewRoles) {
            RoleModel role = realm.getRole(roleName);
            if (user.hasRole(role)) {
                return true;
            }
        }

        List<String> viewOwnGroupsRoles = roles.get("viewMembersOfOwnGroups");
        
        for (String roleName : viewOwnGroupsRoles) {
            RoleModel role = realm.getRole(roleName);
            if (user.hasRole(role) && user.getGroups().contains(group)) {
                return true;
            }
        }

        return false;
    }

    @GET
    @Path("{id}/members")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getGroupMembers(@PathParam("id") String id) {

        GroupModel group = realm.getGroupById(id);

        if (auth == null || !canViewUsers(auth.getUser(), group)) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        List<UserRepresentation> results = new ArrayList<UserRepresentation>();
        List<UserModel> userModels = session.users().getGroupMembers(realm, group);

        for (UserModel user : userModels) {
            UserRepresentation userRep = ModelToRepresentation.toRepresentation(session, realm, user);

            results.add(userRep);
        }

        return Response.status(Response.Status.OK).entity(results).build();
    }
}
