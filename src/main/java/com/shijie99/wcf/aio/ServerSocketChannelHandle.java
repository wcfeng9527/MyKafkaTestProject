package com.shijie99.wcf.aio;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;


public class ServerSocketChannelHandle implements CompletionHandler<AsynchronousSocketChannel,Void>{
	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger.getLogger(ServerSocketChannelHandle.class);
	
	private AsynchronousServerSocketChannel serverSocketChannel;
	
	public ServerSocketChannelHandle(AsynchronousServerSocketChannel serverSocketChannel){
		this.serverSocketChannel = serverSocketChannel;
	}
	@Override
	public void completed(AsynchronousSocketChannel socketChannel, Void attachment) {
		LOGGER.info("completed(AsynchronousSocketChannel result, ByteBuffer attachment)");
		//每次都要重新注册监听（一次注册，一次响应），但是由于“文件状态标示符”是独享的，所以不需要担心有“漏掉的”事件
		this.serverSocketChannel.accept(attachment, this);
		ByteBuffer readBuffer = ByteBuffer.allocate(50);
		socketChannel.read(readBuffer, new StringBuffer(), new SocketChannelReadHandle(socketChannel,readBuffer));
	}

	@Override
	public void failed(Throwable exc, Void attachment) {
		LOGGER.info("failed(Throwable exc, ByteBuffer attachment)");
		
	}

}
