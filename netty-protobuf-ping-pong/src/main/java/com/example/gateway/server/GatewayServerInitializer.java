package com.example.gateway.server;

import com.example.gateway.proto.GatewayProto;
import com.example.gateway.session.RedisSessionService;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

public class GatewayServerInitializer extends ChannelInitializer<SocketChannel> {

    private final RedisSessionService sessionService;

    public GatewayServerInitializer(RedisSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    protected void initChannel (SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();

        // inbound: ByteBuf -> Request
        pipeline.addLast(new ProtobufVarint32FrameDecoder());
        pipeline.addLast(new ProtobufDecoder(GatewayProto.Request.getDefaultInstance()));

        // outbound: Response -> ByteBuf
        pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
        pipeline.addLast(new ProtobufEncoder());

        // business logic
        pipeline.addLast(new GatewayServerHandler(sessionService));
    }
}
