package com.example.gateway.session;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;

import java.time.Duration;
import java.util.UUID;

public class RedisSessionService implements AutoCloseable {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> connection;

    public RedisSessionService(String redisUri) {
        this.redisClient = RedisClient.create(redisUri);
        this.connection = redisClient.connect();
    }

    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        String key = "session:" + sessionId;

        connection.sync().setex(
                key,
                Duration.ofHours(1).toSeconds(),
                username
        );

        return sessionId;
    }


    public String getUsername(String sessionId) {
        return connection.sync().get("session:" + sessionId);
    }

    @Override
    public void close() throws Exception {
        connection.close();
        redisClient.shutdown();
    }
}
