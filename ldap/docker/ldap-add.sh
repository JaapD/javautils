#!/bin/bash
echo "--------------------------"
echo "-- Add entries in ldap  --"
echo "--------------------------"

echo "-- Generate password (example)"
perl -e 'print("userPassword: {CRYPT}".crypt("secret","salt")."\n");'

echo "-- Cleanup"
ldapdelete -w adminPassword -r -x -D "cn=admin,dc=test,dc=nl" "ou=users,dc=test,dc=nl"
ldapdelete -w adminPassword -r -x -D "cn=admin,dc=test,dc=nl" "ou=groups,dc=test,dc=nl"

echo "-- Add"
ldapadd -w adminPassword -x -D "cn=admin,dc=test,dc=nl" -f ldap-add.ldif

