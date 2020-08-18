package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata;

import io.quarkus.security.identity.SecurityIdentity;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;

@Provider
public class ConnectedUserMetadataEnhancer implements ContainerRequestFilter {

    public static final String USER_ANONYMOUS = "user.anonymous";
    public static final String USER_NAME = "user.name";

    @Inject
    SecurityIdentity securityIdentity;

    @Override
    public void filter(final ContainerRequestContext requestContext) throws IOException {
        MetadataEnhancerContextHolder.put(USER_ANONYMOUS, securityIdentity.isAnonymous());
        MetadataEnhancerContextHolder.put(USER_NAME, Optional.ofNullable(securityIdentity)
                .map(SecurityIdentity::getPrincipal)
                .map(Principal::getName)
                .orElse(""));
    }

}
