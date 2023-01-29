# How to use

## Web.xml

No need to add a filter to the webxml. But probably modify the root element so it contains:

```` xml
         metadata-complete="false"
````

## WEB-INF 

Create a file `/WEB-INF/auth.properties` with something like:
````properties
login=/index.jsf
logout=/logout.jsf
allowed.all=/javax.faces.resource.+;/styles/.+;/js/.+;/errors/.+
page.after.login=/pages/main/index.xhtml
````
## beans.xml

Add the following to `beans.xml`
````xml
  <interceptors>
    <class>nl.tsbd.alias.auth.AuthenticatedInterceptor</class>
  </interceptors>

````

## Application

Extend the AuthenticationProperties class and override `getPropertyFilenameKey()` to use the right
name for the property `authentication-properties`.

## Configure Ldap

Create a property `authentication-properties` in the application server.
That has the name of the authentication properties file. An example:

```properties
ldap.root=dc=test,dc=nl
ldap.admindn=cn=admin,dc=test,dc=nl
ldap.adminpw=adminPassword
ldap.ldaphost=localhost
ldap.ldapport=389
ldap.is.tls=false
should.have.group=cn=alias,ou=groups,dc=test,dc=nl
admin.group=cn=teacher,ou=groups,dc=test,dc=nl
```

## Login and Logout pages

Create the login and logout pages.
