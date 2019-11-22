package com.xiong;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ChatRoomServer {

    private int port = 8888;

    public ChatRoomServer(int port) {
        this.port = port;
    }

    public void start() {
        //创建两个线程组
        //接收客户端的连接
        EventLoopGroup boss = new NioEventLoopGroup();
        //负责与已连接的客户端通讯
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            //配置服务器
            ServerBootstrap boot = new ServerBootstrap();
            //让两个线程组关联起来
            boot.group(boss, worker)
                    //组册一下服务端的通道，处理类
                    .channel(NioServerSocketChannel.class)
                    //设定子处理器，当有某一些网络通讯事件发生之后，由谁来处理这些事件
                    //处理客户端请求的回调类
                    .childHandler(new ChatServerInitialize())
                    //设置通道数，官方建议是128
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //保持连接
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture future = boot.bind(this.port).sync();
            System.out.println("服务器启动了");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        new ChatRoomServer(8888).start();
    }

}
