#!/bin/bash
sleep 5
ldapadd -h ldap -D "cn=admin,dc=example,dc=org" -w admin -f /container/service/slapd/assets/test/gauss.ldif
ldapadd -h ldap -D "cn=admin,dc=example,dc=org" -w admin -f /container/service/slapd/assets/test/newton.ldif
