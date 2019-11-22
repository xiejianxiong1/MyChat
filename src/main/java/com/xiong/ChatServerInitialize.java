package com.xiong;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ChatServerInitialize extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        //当由客户端连接服务器时，Netty会自动调用这个初始化器的initChannel方法
        System.out.println("有客户端接入："+ channel.remoteAddress());

        ChannelPipeline pipeline = channel.pipeline();
        //管道中发送的数据最终都是0101的格式，无缝流动
        //所以在数据量大的时候，我们需要将数据分帧
        //1 定长，比如：每十个字是一帧
        //2 使用固定的分割符，比如：&
        //3 将消息分为消息头和消息体两部分，在消息头中用一个数据说明消息体的长度
        //4 或者其他复杂的协议

        //当前使用的是固定分割符，第一个参数是一帧最大是多少，第二个数是要用哪个分割符
        /**
         * 流经过的时候需要经过以下这么几关
         * 1、0101按照分割符组装整块数据
         * 2、然后在通过解码器解码
         * 如果发给客户端的话，就经过编码器进行编码
         */
        pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
        pipeline.addLast("decoder",new StringDecoder());
        pipeline.addLast("encoder",new StringEncoder());
        pipeline.addLast("handler",new ChatServerHandler());



    }

}
