package org.gxpmaster.platform.splicing.fixedlength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import org.gxpmaster.platform.splicing.IBaseServer;
import org.gxpmaster.platform.splicing.PropertyMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Sun
 * 按定长格式接收Client端报文，然后进行解析
 * 这种情况用在定长包通信的情况下
 */
public class FixedLengthServer implements IBaseServer {

    public static final String RESPONSE = "Server has received";
    public static final int port = Integer.valueOf(PropertyMgr.get("serverport"));

    static private final Logger LOGGER = LoggerFactory.getLogger(FixedLengthServer.class);

    public static void main(String[] args) {
        FixedLengthServer fixedLengthServer = new FixedLengthServer();
        LOGGER.info("服务器即将启动");
        fixedLengthServer.start();
    }

    @Override
    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(port)
                .childHandler(new ChannelInitializerImp());

        try {
            //异步绑定到服务器，sync()会阻塞直到完成
            ChannelFuture f = b.bind().sync();
            LOGGER.info("服务器启动完成，等待客户端的连接和数据.....");
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private static class ChannelInitializerImp extends ChannelInitializer<SocketChannel>{

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            socketChannel.pipeline().addLast(new FixedLengthFrameDecoder(FixedLengthClient.REQUEST.length()));
            socketChannel.pipeline().addLast(new FixedLengthServerHandler());
        }
    }

}
