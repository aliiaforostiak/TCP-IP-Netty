package com.example.gateway.server;

import com.example.gateway.proto.GatewayProto;
import com.example.gateway.session.RedisSessionService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class GatewayServerHandler extends SimpleChannelInboundHandler<GatewayProto.Request> {

    private final RedisSessionService sessionService;

    public GatewayServerHandler(RedisSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GatewayProto.Request request) {
        System.out.println("Received request: " + request);

        GatewayProto.Response response;

        if (request.hasLogin()) {
            String username = request.getLogin().getUsername();
            String sessionId = sessionService.createSession(username);

            response = GatewayProto.Response.newBuilder()
                    .setRequestId(request.getRequestId())
                    .setSuccess(true)
                    .setMessage("Login successful")
                    .setSessionId(sessionId)
                    .build();

        } else if (request.hasPing()) {
            response = GatewayProto.Response.newBuilder()
                    .setRequestId(request.getRequestId())
                    .setSuccess(true)
                    .setMessage("PONG: " + request.getPing().getText())
                    .build();

        } else {
            response = GatewayProto.Response.newBuilder()
                    .setRequestId(request.getRequestId())
                    .setSuccess(false)
                    .setMessage("Unsupported request type")
                    .build();
        }

        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
