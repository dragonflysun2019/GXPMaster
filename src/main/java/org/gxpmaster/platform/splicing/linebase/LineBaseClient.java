package org.gxpmaster.platform.splicing.linebase;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.gxpmaster.platform.splicing.PropertyMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;


/**
 * @author David Sun
 * 发送带回车换行符的报文给Server
 */
public class LineBaseClient {
    private final String host = PropertyMgr.get("serverip");
    private final int port = Integer.valueOf(PropertyMgr.get("serverport"));
    static private final Logger LOGGER = LoggerFactory.getLogger(LineBaseClient.class);

    public static void main(String[] args) {
        new LineBaseClient().start();
    }

    private void start() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.group(group)
                .channel(NioSocketChannel.class)
                .remoteAddress(new InetSocketAddress(host,port))
                .handler(new ChannelInitializerImp() {
                });
        try {
            ChannelFuture f = b.connect().sync();
            LOGGER.info("已连接到服务器.....");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            group.shutdownGracefully();
        }


    }

    private static class ChannelInitializerImp extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new LineBasedFrameDecoder(1024));
            socketChannel.pipeline().addLast(new LineBaseClientHandler());
        }
    }
}
