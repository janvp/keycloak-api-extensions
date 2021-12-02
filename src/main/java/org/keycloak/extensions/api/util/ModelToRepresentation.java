package org.keycloak.extensions.api.util;

import org.keycloak.models.*;
import org.keycloak.models.credential.OTPCredentialModel;
import org.keycloak.representations.idm.*;
import org.keycloak.storage.StorageId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ModelToRepresentation {
    
    public static UserRepresentation toRepresentation(KeycloakSession session, RealmModel realm, UserModel user) {
        UserRepresentation rep = new UserRepresentation();
        rep.setId(user.getId());
        String providerId = StorageId.resolveProviderId(user);
        rep.setOrigin(providerId);
        rep.setUsername(user.getUsername());
        rep.setCreatedTimestamp(user.getCreatedTimestamp());
        rep.setLastName(user.getLastName());
        rep.setFirstName(user.getFirstName());
        rep.setEmail(user.getEmail());
        rep.setEnabled(user.isEnabled());
        rep.setEmailVerified(user.isEmailVerified());
        rep.setTotp(session.userCredentialManager().isConfiguredFor(realm, user, OTPCredentialModel.TYPE));
        rep.setDisableableCredentialTypes(session.userCredentialManager().getDisableableCredentialTypes(realm, user));
        rep.setFederationLink(user.getFederationLink());

        rep.setNotBefore(session.users().getNotBeforeOfUser(realm, user));

        Set<String> requiredActions = user.getRequiredActions();
        List<String> reqActions = new ArrayList<>(requiredActions);

        rep.setRequiredActions(reqActions);

        Map<String, List<String>> attributes = user.getAttributes();
        Map<String, List<String>> copy = null;

        if (attributes != null) {
            copy = new HashMap<>(attributes);
            copy.remove(UserModel.LAST_NAME);
            copy.remove(UserModel.FIRST_NAME);
            copy.remove(UserModel.EMAIL);
            copy.remove(UserModel.USERNAME);
        }
        if (attributes != null && !copy.isEmpty()) {
            Map<String, List<String>> attrs = new HashMap<>(copy);
            rep.setAttributes(attrs);
        }

        Set<RoleModel> realmRoleModels = user.getRealmRoleMappings();
        List<String> realmRoles = new ArrayList<String>();
        for (RoleModel role : realmRoleModels) {
            realmRoles.add(role.getName());
        }
        rep.setRealmRoles(realmRoles);

        Set<GroupModel> groupModels = user.getGroups();
        List<String> groups = new ArrayList<String>();
        for (GroupModel group : groupModels) {
            groups.add(group.getName());
        }
        rep.setGroups(groups);

        return rep;
    }

}
