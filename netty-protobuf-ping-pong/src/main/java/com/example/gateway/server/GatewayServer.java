package com.example.gateway.server;

import com.example.gateway.session.RedisSessionService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.MultiThreadIoEventLoopGroup;
import io.netty.channel.nio.NioIoHandler;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class GatewayServer {

    private final int port;

    public GatewayServer(int port) {
        this.port = port;
    }

    private void start() throws Exception {

        EventLoopGroup bossGroup = new MultiThreadIoEventLoopGroup(1, NioIoHandler.newFactory());
        EventLoopGroup workerGroup = new MultiThreadIoEventLoopGroup(NioIoHandler.newFactory());
        RedisSessionService sessionService = new RedisSessionService("redis://localhost:6379");

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new GatewayServerInitializer(sessionService));

            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            System.out.println("Gateway server started on port: " + port);

            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();

        } finally {
            sessionService.close();
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new GatewayServer(8083).start();
    }
}
