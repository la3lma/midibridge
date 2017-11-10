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
import java.util.HashSet;
import java.util.Set;
import javax.sound.midi.InvalidMidiDataException;

public final class MidiOverUdpReceivingService implements MidiEventProducer {

    private final int port;


    public static class UdpServerHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        private ByteBuf buf;


        public UdpServerHandler() {
            super();
        }

        private final Set<MidiReceiver> midiReceivers = new HashSet<>();

        private void sendBytesToMidiReceivers(final byte[] bytes) throws InvalidMidiDataException {
            for (final MidiReceiver r : midiReceivers) {
                r.put(bytes);
            }
        }

        public void addMidiReceiver(MidiReceiver receiver) {
            midiReceivers.add(receiver);
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

            sendBytesToMidiReceivers(bytes);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }

    private final UdpServerHandler handler = new UdpServerHandler();

    @Override
    public void addMidiReceiver(final MidiReceiver receiver) {
        checkNotNull(receiver);
        handler.addMidiReceiver(receiver);
    }

    public MidiOverUdpReceivingService(int port) {
        this.port = port;
    }

    public void start() throws MidibridgeException {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(handler);

            b.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException ex) {
            throw new MidibridgeException(ex);
        } finally {
            group.shutdownGracefully();
        }
    }
}
