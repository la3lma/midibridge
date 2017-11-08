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

    private final int port;

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
            final ByteBuf buf = i.content();
            final byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);

            final int firstByte = bytes[0] & 0xFF;

            final MidiCmd cmd = MidiCmd.findByMidiCmd(firstByte);
            if (cmd == null) {
                return;
            }
            if (bytes.length != (cmd.getNoOfArgs() + 1)) {
                return;
            }

            System.out.print("Received MIDI cmd: " + cmd.name());

        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }
    }

    public UdpServer(int port) {
        this.port = port;
    }

    public void start() throws MidibridgeException {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .handler(new UdpServerHandler());

            b.bind(port).sync().channel().closeFuture().await();
        } catch (InterruptedException ex) {
            throw new MidibridgeException(ex);
        } finally {
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        final UdpServer udpServer = new UdpServer(6565);
        udpServer.start();

    }
}
