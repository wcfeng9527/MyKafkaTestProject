package com.shijie99.wcf.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;

public class SocketServer1 {
	static {
		BasicConfigurator.configure();
	}

	/**
	 * 日志
	 */
	private static final Log LOGGER = LogFactory.getLog(SocketServer1.class);

	public static void main(String[] args) throws IOException {
		ServerSocketChannel serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);
		ServerSocket serverSocket = serverChannel.socket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress(83));
		
		Selector selector = Selector.open();
		
		serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		
		try{
			while(true){
				if(selector.select(100)==0){
					continue;
				}
				
				Iterator<SelectionKey> selectionKey = selector.selectedKeys().iterator();
				while(selectionKey.hasNext()){
					SelectionKey readyKey = selectionKey.next();
					selectionKey.remove();
					
					SelectableChannel selectableChannel = readyKey.channel();
					
					if(readyKey.isValid() && readyKey.isConnectable()){
						SocketServer1.LOGGER.info("======socket channel 建立连接=======");
					}else if(readyKey.isValid() && readyKey.isAcceptable()){
						SocketServer1.LOGGER.info("======channel通道已经准备好=======");
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectableChannel;
						SocketChannel socketChannel = serverSocketChannel.accept();
						socketChannel.configureBlocking(false);
						socketChannel.register(selector, SelectionKey.OP_READ,ByteBuffer.allocate(1024));
					}else if(readyKey.isValid() && readyKey.isReadable()){
						SocketServer1.LOGGER.info("======socket channel 数据准备完成，可以去读==读取=======");
						SocketChannel clientSocketChannel = (SocketChannel) readyKey.channel();
						SocketServer1.LOGGER.info("clientSocketChannel 的hashCode："+clientSocketChannel.hashCode());
						InetSocketAddress sourceSocketAddress=(InetSocketAddress) clientSocketChannel.getRemoteAddress();
						Integer resoucePort = sourceSocketAddress.getPort();
						ByteBuffer contextBytes =(ByteBuffer) readyKey.attachment();
						int realLen = -1;
						try{
							realLen = clientSocketChannel.read(contextBytes);
						}catch (Exception e) {
							 SocketServer1.LOGGER.error(e.getMessage());
							 clientSocketChannel.close();
							 return;
						}
						if(realLen==-1){
							 SocketServer1.LOGGER.warn("====缓存区没有数据？====");
							 return ;
						}
						contextBytes.flip();
						byte[] messageBytes = contextBytes.array();
						String messageEncode = new String(messageBytes,"utf-8");
						String message = URLDecoder.decode(messageEncode,"utf-8");
						if(message.indexOf("over") !=-1){
							contextBytes.clear();
							SocketServer1.LOGGER.info("端口:" + resoucePort + "客户端发来的信息======message : " + message);
							ByteBuffer sendBuffer = ByteBuffer.wrap(URLEncoder.encode("回发处理结果", "utf-8").getBytes());
							clientSocketChannel.write(sendBuffer);
							clientSocketChannel.close();
						}else{
							SocketServer1.LOGGER.info("端口:" + resoucePort + "客户端信息还未接受完，继续接受======message : " + message);
							contextBytes.position(realLen);
					        contextBytes.limit(contextBytes.capacity());
						}
					}
					
					
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			
		}
		
	}
}
