package netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.nio.charset.StandardCharsets;

public class ServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        String message = byteBuf.toString(StandardCharsets.UTF_8);

        System.out.println("Server received from client: " + message);

        ByteBuf response = ctx.alloc().buffer();
        response.writeCharSequence(message, StandardCharsets.UTF_8);

        ctx.writeAndFlush(response);
        byteBuf.release();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("Server error: " + cause.getMessage());
        ctx.close();
    }
}
