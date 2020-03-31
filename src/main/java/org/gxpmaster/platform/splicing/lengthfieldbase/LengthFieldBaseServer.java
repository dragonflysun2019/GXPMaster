package org.gxpmaster.platform.splicing.lengthfieldbase;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.gxpmaster.platform.splicing.IBaseServer;
import org.gxpmaster.platform.splicing.PropertyMgr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Sun
 * 按报文头指定长度读取Client发送报文，进行解析
 */
public class LengthFieldBaseServer implements IBaseServer {

    public static final int port = Integer.valueOf(PropertyMgr.get("serverport"));

    static private final Logger LOGGER = LoggerFactory.getLogger(LengthFieldBaseServer.class);

    public static void main(String[] args) {
        LengthFieldBaseServer lengthFieldBaseServer = new LengthFieldBaseServer();
        LOGGER.info("服务器即将启动");
        lengthFieldBaseServer.start();
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
            int maxFrameLength = 0;
            int lengthFieldLength = 0;
            int lengthFieldOffset = 0;
            int lengthAdjustment = 0;
            int initialBytesToStrip = 0;
            if (null != PropertyMgr.get("lengthfieldlength")){
                lengthFieldLength = Integer.valueOf(PropertyMgr.get("lengthfieldlength"));
            }else{
                lengthFieldLength = 2;
            }
            if (null != PropertyMgr.get("maxframelength")){
                maxFrameLength = Integer.valueOf(PropertyMgr.get("maxframelength"));
            }else{
                maxFrameLength = 2;
            }
            if (null != PropertyMgr.get("lengthfieldoffset")){
                lengthFieldOffset = Integer.valueOf(PropertyMgr.get("lengthfieldoffset"));
            }else{
                lengthFieldOffset = 0;
            }
            if (null != PropertyMgr.get("lengthadjustment")){
                lengthAdjustment = Integer.valueOf(PropertyMgr.get("lengthadjustment"));
            }else{
                lengthAdjustment = 0;
            }
            if (null != PropertyMgr.get("initialbytestostrip")){
                initialBytesToStrip = Integer.valueOf(PropertyMgr.get("initialbytestostrip"));
            }else{
                initialBytesToStrip = 2;
            }
            socketChannel.pipeline().addLast(new LengthFieldPrepender(lengthFieldLength));
            socketChannel.pipeline().addLast(
                    new LengthFieldBasedFrameDecoder(maxFrameLength,lengthFieldOffset,
                            lengthFieldLength,lengthAdjustment,initialBytesToStrip));
            socketChannel.pipeline().addLast(new LengthFieldBaseServerHandler());
        }
    }

}
