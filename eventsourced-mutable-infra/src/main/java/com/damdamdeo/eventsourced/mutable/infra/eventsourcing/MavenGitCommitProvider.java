package com.damdamdeo.eventsourced.mutable.infra.eventsourcing;

import com.damdamdeo.eventsourced.mutable.api.eventsourcing.GitCommitProvider;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@ApplicationScoped
public class MavenGitCommitProvider implements GitCommitProvider {

    private final String gitCommitId;

    public MavenGitCommitProvider() {
        try (final InputStream gitProperties = getClass().getResourceAsStream("/git.properties");
             final JsonReader reader = Json.createReader(gitProperties)) {
            final javax.json.JsonObject gitPropertiesObject = reader.readObject();
            this.gitCommitId = Objects.requireNonNull(gitPropertiesObject.getString("git.commit.id"));
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String gitCommitId() {
        return gitCommitId;
    }

}
