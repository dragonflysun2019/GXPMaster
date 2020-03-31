package org.gxpmaster.platform.splicing.fixedlength;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author David Sun
 */
public class FixedLengthClientHandler extends SimpleChannelInboundHandler<ByteBuf> {
    static private final Logger LOGGER = LoggerFactory.getLogger(FixedLengthClientHandler.class);
    private AtomicInteger counter = new AtomicInteger(0);
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        LOGGER.info("Client Accept: " + byteBuf.toString(CharsetUtil.UTF_8)
                + " Count:[" + counter.incrementAndGet() + "]");
    }

    /**
     *
     * Client被激活后开始发送数据，这个Client是模拟测试作用
     *
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ByteBuf msg = null;
        for (int i = 0; i < 20; ++i){
            //Thread.sleep(500);
            LOGGER.info(System.currentTimeMillis()+":即将发送数据："
                    + FixedLengthClient.REQUEST);
            msg = Unpooled.buffer(FixedLengthClient.REQUEST.length());
            msg.writeBytes(FixedLengthClient.REQUEST.getBytes());
            ctx.writeAndFlush(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
