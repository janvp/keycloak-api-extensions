package org.keycloak.extensions.api.users;

import org.keycloak.extensions.api.util.ModelToRepresentation;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.services.managers.AppAuthManager;
import org.keycloak.services.managers.AuthenticationManager;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class UsersResource {

    private final KeycloakSession session;

    private final AuthenticationManager.AuthResult auth;

    private Map<String,List<String>> roles;

    public UsersResource(KeycloakSession session, Map<String,List<String>> roles) {
        this.session = session;
        this.roles = roles;
        this.auth = resolveAuthentication();
    }

    private AuthenticationManager.AuthResult resolveAuthentication() {
        AppAuthManager appAuthManager = new AppAuthManager();
        RealmModel realm = session.getContext().getRealm();
        AuthenticationManager.AuthResult authResult = appAuthManager.authenticateBearerToken(session, realm);

        if (authResult != null) {
            return authResult;
        }

        return null;
    }

    private boolean canViewUsers(UserModel user) {
        List<String> viewRoles = roles.get("view");
        
        if (viewRoles.isEmpty()) {
            return true;
        }

        for (String viewRole : viewRoles) {
            RoleModel role = session.getContext().getRealm().getRole(viewRole);
            if (user.hasRole(role)) {
                return true;
            }
        }

        return false;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUsers() {

        if (auth == null || !canViewUsers(auth.getUser())) {
            return Response.status(Response.Status.FORBIDDEN).build();
        }

        final List<UserModel> users = session
                .userStorageManager()
                .getUsers(session.getContext().getRealm());

        // Transform our model to representations that can be serialized.
        List<UserRepresentation> representations = new ArrayList<>(users.size());
        for (UserModel user : users) {
            representations.add(ModelToRepresentation.toRepresentation(session, session.getContext().getRealm(), user));
        }

        return Response.status(Response.Status.OK).entity(representations).build();
    }
}
