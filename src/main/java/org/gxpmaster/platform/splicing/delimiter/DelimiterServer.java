package org.gxpmaster.platform.splicing.delimiter;

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
import io.netty.handler.codec.LineBasedFrameDecoder;
import org.gxpmaster.platform.splicing.IBaseServer;
import org.gxpmaster.platform.splicing.PropertyMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Sun
 * 按指定特殊字符读取Client端报文，然后进行解析
 */
public class DelimiterServer implements IBaseServer {

    public static final int port = Integer.valueOf(PropertyMgr.get("serverport"));
    public static final String DELIMITER_SYMBOL = PropertyMgr.get("delimitersymbol");

    static private final Logger LOGGER = LoggerFactory.getLogger(DelimiterServer.class);

    public static void main(String[] args) {
        DelimiterServer delimiterServer = new DelimiterServer();
        LOGGER.info("服务器即将启动");
        delimiterServer.start();
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
            ByteBuf delimiter = Unpooled.copiedBuffer(DELIMITER_SYMBOL.getBytes());
            socketChannel.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
            socketChannel.pipeline().addLast(new DelimiterServerHandler());
        }
    }

}
