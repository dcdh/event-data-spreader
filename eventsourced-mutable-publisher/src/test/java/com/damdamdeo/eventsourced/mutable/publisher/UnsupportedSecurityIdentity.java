package com.damdamdeo.eventsourced.mutable.publisher;

import io.quarkus.security.credential.Credential;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.security.Permission;
import java.security.Principal;
import java.util.Map;
import java.util.Set;

@ApplicationScoped
public class UnsupportedSecurityIdentity implements SecurityIdentity {
    @Override
    public Principal getPrincipal() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public Set<String> getRoles() {
        return null;
    }

    @Override
    public boolean hasRole(String role) {
        return false;
    }

    @Override
    public <T extends Credential> T getCredential(Class<T> credentialType) {
        return null;
    }

    @Override
    public Set<Credential> getCredentials() {
        return null;
    }

    @Override
    public <T> T getAttribute(String name) {
        return null;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Uni<Boolean> checkPermission(Permission permission) {
        return null;
    }
}
