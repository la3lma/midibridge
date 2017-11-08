package no.rmz.midibridge;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

/**
 * Discards any incoming data.
 */
public class UdpServer {

    public static class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        private ByteBuf buf;

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) {
            buf = ctx.alloc().buffer(64);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) {
            buf.release();
            buf = null;
        }

        @Override
        protected void messageReceived(ChannelHandlerContext chc, DatagramPacket i) throws Exception {
            System.out.println("packet  = " + i);
            ByteBuf content = i.content();
            System.out.println("content = " + content);
            final byte[] array = i.content().array();
            System.out.println("lenght = " + array.length);
            System.out.println("content = " + array);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }

    private static final int PORT = Integer.parseInt(System.getProperty("port", "6565"));

    public static void main(String[] args) throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler());

            b.bind(PORT).sync().channel().closeFuture().await();
        } finally {
            group.shutdownGracefully();
        }
    }
}
