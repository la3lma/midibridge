package no.rmz.midibridge;

import static com.google.common.base.Preconditions.checkNotNull;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;

public final class MidiOverUdpReceivingService {

    private final int port;
    private final MidiReceiver midiReceiver;

    public static class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        private ByteBuf buf;
        private final MidiReceiver midiReceiver;

        public UdpServerHandler(MidiReceiver midiReceiver) {
            super();
            this.midiReceiver = checkNotNull(midiReceiver);
        }


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
            final ByteBuf buf = i.content();
            final byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            midiReceiver.put(bytes);

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }

    public MidiOverUdpReceivingService(int port, MidiReceiver receiver) {
        this.port = port;
        this.midiReceiver = receiver;
    }

    public void start() throws MidibridgeException {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler(this.midiReceiver));

            b.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException ex) {
            throw new MidibridgeException(ex);
        } finally {
            group.shutdownGracefully();
        }
    }
}
