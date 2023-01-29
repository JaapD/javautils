#!/bin/bash

docker start ldap-service

if [[ ${?} -ne 0 ]]; then
  echo "Start failed, create a new container"
  mkdir -p ${HOME}/slapd
  echo "Remove previous configuration, in case of error, delete dir ${HOME}/slapd"
  rm -rf ${HOME}/slapd/*
  mkdir -p ${HOME}/slapd/database
  mkdir -p ${HOME}/slapd/config
  echo "Running container"
  docker run -p 389:389 --name ldap-service --hostname ldap-service \
    --env LDAP_ORGANISATION="test"\
    --env LDAP_DOMAIN="test.nl" \
    --env LDAP_ADMIN_PASSWORD="adminPassword"\
    --env LDAP_BASE_DN="dc=test,dc=nl"\
    --volume ${HOME}/slapd/database:/var/lib/ldap \
    --volume ${HOME}/slapd/config:/etc/ldap/slapd.d\
    --detach osixia/openldap:1.3.0
fi
