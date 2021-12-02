# keycloak-api-extensions

Extension of the Keycloak 11.0 REST API. Following API calls have been added:
- Get a list of all the users in the realm
```
GET {baseURL}/auth/realms/{realmName}/users
```

- Get all members of a specific group
```
GET {baseURL}/auth/realms/{realmName}/groups/{groupId}/members
```

The difference of these API calls compared to the existing API calls in the admin resource, is that it also returns the realm roles and the groups of each user.

## Authorization

Authorization is done by a Bearer access token issued in the realm specified in the path of the request.

You can give permissions to certain realm roles to define who can access the API calls. This can be configured by adding following lines to the standalone.xml configuration file:

```
<spi name="realm-restapi-extension">
    <provider name="users" enabled="true">
        <properties>
            <property name="roles.canView" value="roleX,roleY"/>
        </properties>
    </provider>
    <provider name="groups" enabled="true">
        <properties>
            <property name="roles.canViewMembers" value="roleX,roleY"/>
            <property name="roles.canViewMembersOfOwnGroups" value="roleA,roleB"/>
        </properties>
    </provider>
</spi>
```

The values of the properties contain always a comma-separated list of the realm roles that will be mapped to the specific permission. If you don't add this in the configuration, there are no role restrictions.

### Users provider
- `roles.canView` : Users with one of these roles can view all users.

### Groups provider
- `roles.canViewMembers` : Users with one of these roles can view all members of all groups.
- `roles.canViewMembersOfOwnGroups` : Users with one of these roles can only view all members of the groups they belong to.

## Installation

- Create JAR
- Add JAR to `{$KEYCLOAK_HOME}/providers/`
- Add SPI to Keycloack configuration (optional, see Authorization section)  
- Restart Keycloak
