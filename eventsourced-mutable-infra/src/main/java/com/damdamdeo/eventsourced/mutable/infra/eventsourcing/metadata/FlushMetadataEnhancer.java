package com.damdamdeo.eventsourced.mutable.infra.eventsourcing.metadata;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Provider
public class FlushMetadataEnhancer implements ContainerResponseFilter {

    @Override
    public void filter(final ContainerRequestContext requestContext, final ContainerResponseContext responseContext) throws IOException {
        MetadataEnhancerContextHolder.cleanupThread();
    }

}
