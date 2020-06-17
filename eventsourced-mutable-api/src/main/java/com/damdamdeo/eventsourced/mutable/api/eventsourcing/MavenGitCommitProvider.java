package com.damdamdeo.eventsourced.mutable.api.eventsourcing;

import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

// TODO infra
@ApplicationScoped
public class MavenGitCommitProvider implements GitCommitProvider {

    private final String gitCommitId;

    public MavenGitCommitProvider() {
        try (final InputStream gitProperties = getClass().getClassLoader().getResourceAsStream("git.properties");
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
