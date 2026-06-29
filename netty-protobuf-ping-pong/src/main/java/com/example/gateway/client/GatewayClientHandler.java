package com.example.gateway.client;

import com.example.gateway.proto.GatewayProto;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

public class GatewayClientHandler extends SimpleChannelInboundHandler<GatewayProto.Response> {

    private String sessionId;

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        GatewayProto.Request loginRequest = GatewayProto.Request.newBuilder()
                .setRequestId(UUID.randomUUID().toString())
                .setLogin(GatewayProto.LoginRequest.newBuilder()
                        .setUsername("dreamer")
                        .setPassword("password")
                        .build())
                .build();

        System.out.println("Sending login request: " + loginRequest);
        ctx.writeAndFlush(loginRequest);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayProto.Response response) {
        System.out.println("Received response: " + response);

        if (response.getSuccess() && !response.getSessionId().isBlank()) {
            this.sessionId = response.getSessionId();

            GatewayProto.Request pingRequest = GatewayProto.Request.newBuilder()
                    .setRequestId(UUID.randomUUID().toString())
                    .setPing(
                            GatewayProto.Ping.newBuilder()
                                    .setText("hello from client, sessionId: " + sessionId)
                                    .build()
                    )
                    .build();

            System.out.println("Sending ping request: " + pingRequest);

            ctx.writeAndFlush(pingRequest);
        } else {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}
