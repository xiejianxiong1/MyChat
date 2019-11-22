package com.xiong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class ChatServerHandler extends SimpleChannelInboundHandler<String> {

    //指定泛型为字符串类型，表明传输的是字符串类型

    public static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 该方法是当有客户端消息进行写入的时候，会自动调用
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel cilent = ctx.channel();

        for (Channel ch : channels) {
            if (ch != cilent) {
                ch.writeAndFlush("[用户：" + cilent.remoteAddress() +"说：]"+ msg + "\n");
            }else {
                ch.writeAndFlush("[我说：]"+ msg + "\n");
            }
        }
    }


    /**
     * 当监听到客户端活动时
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //相当于心跳包
        Channel incoming = ctx.channel();
        System.out.println("[" + incoming.remoteAddress() + "]:在线中" );
    }

    /**
     * 客户端没活动，离线了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        //相当于心跳包
        Channel incoming = ctx.channel();
        System.out.println("[" + incoming.remoteAddress() + "]:离线了" );
    }

    /**
     * 异常
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    /**
     * 当有客户端连接时执行
     * 表示有客户端连接了
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //获取客户端通道
        Channel incoming = ctx.channel();
        //新加入的客户端放入队列中
        channels.add(incoming);
        for (Channel ch : channels) {
            if (ch != incoming) {
                ch.writeAndFlush("[欢迎：]" + incoming.remoteAddress() + "进入聊天室\n");
            }
        }

    }

    /**
     * 断开连接时执行
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        //获取客户端通道
        Channel incoming = ctx.channel();
        for (Channel ch : channels) {
            if (ch != incoming) {
                ch.writeAndFlush("[再见：]" + incoming.remoteAddress() + "离开聊天室");
            }
        }
        //将离开的客户端队列中删除
        channels.remove(incoming);
    }
}
