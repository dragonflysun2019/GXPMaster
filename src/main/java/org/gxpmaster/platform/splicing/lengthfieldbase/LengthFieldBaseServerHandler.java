package org.gxpmaster.platform.splicing.lengthfieldbase;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author David Sun
 * Server 端读取后的业务处理框架
 */
public class LengthFieldBaseServerHandler extends ChannelInboundHandlerAdapter {

    static private final Logger LOGGER = LoggerFactory.getLogger(LengthFieldBaseServerHandler.class);
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端：" + ctx.channel().remoteAddress() + " 已连接......");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.info("客户端：" + ctx.channel().remoteAddress() + " 即将关闭......");
    }
    /**
     *
     * 按照回车换行符读取完毕调用该函数
     *
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf)msg;
        String request = in.toString(CharsetUtil.UTF_8);
        LOGGER.info("Server Accepted: " + request + " Count:[" + counter.incrementAndGet() + "]");
        ctx.writeAndFlush(Unpooled.copiedBuffer(Unpooled.copiedBuffer(request.getBytes())));
        ctx.fireChannelRead(in);
    }

    /**
     *
     * 将缓冲区读取完毕后调用该函数
     * 一般不用该函数，系统缓冲区的读取不好控制
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
