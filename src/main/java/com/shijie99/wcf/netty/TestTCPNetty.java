package com.shijie99.wcf.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.net.InetSocketAddress;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.ThreadFactory;

public class TestTCPNetty {
//	private static final Logger LOGGER = Logger.getLogger(TestTCPNetty.class);

	public static void main(String[] args) {
		// 这就是主要的服务启动器
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		// =======================下面我们设置线程池
		// BOSS线程池
		EventLoopGroup bossLoopGroup = new NioEventLoopGroup(1);
		// WORK线程池：这样的申明方式，主要是为了向读者说明Netty的线程组是怎样工作的
		ThreadFactory threadFactory = new DefaultThreadFactory("work thread pool");
		int processorsNumber = Runtime.getRuntime().availableProcessors();
		EventLoopGroup workLoopGroup = new NioEventLoopGroup(processorsNumber*2, threadFactory, SelectorProvider.provider());
		//指定Netty的Boss线程和work线程
		serverBootstrap.group(bossLoopGroup, workLoopGroup);
		// 如果是以下的申明方式，说明BOSS线程和WORK线程共享一个线程池
		// （实际上一般的情况环境下，这种共享线程池的方式已经够了）
		// serverBootstrap.group(workLoogGroup);
		
		 //========================下面我们设置我们服务的通道类型
        //只能是实现了ServerChannel接口的“服务器”通道类
		serverBootstrap.channel(NioServerSocketChannel.class);
		//当然也可以这样创建（那个SelectorProvider是不是感觉很熟悉？）
        //serverBootstrap.channelFactory(new ChannelFactory<NioServerSocketChannel>() {
        //  @Override
        //  public NioServerSocketChannel newChannel() {
        //      return new NioServerSocketChannel(SelectorProvider.provider());
        //  }
        //});
		
		//========================设置处理器
        //为了演示，这里我们设置了一组简单的ByteArrayDecoder和ByteArrayEncoder
        //Netty的特色就在这一连串“通道水管”中的“处理器”
		serverBootstrap.childHandler(new ChannelInitializer<NioSocketChannel>() {

			@Override
			protected void initChannel(NioSocketChannel ch) throws Exception {
				ch.pipeline().addLast(new ByteArrayEncoder());
				ch.pipeline().addLast(new TCPServerHandler());
				ch.pipeline().addLast(new ByteArrayDecoder());
			}
		});
		
		serverBootstrap.option(ChannelOption.SO_BACKLOG,128);
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
		serverBootstrap.bind(new InetSocketAddress("0.0.0.0", 83));
		// 还可以监控多个端口
		// serverBootstrap.bind(new InetSocketAddress("0.0.0.0", 84));
	}

}
