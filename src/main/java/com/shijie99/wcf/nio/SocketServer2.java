package com.shijie99.wcf.nio;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

public class SocketServer2 {
	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger.getLogger(SocketServer2.class);
	private static final ConcurrentMap<Integer,StringBuffer> MESSAGEHASHCONTEXT = new ConcurrentHashMap<Integer,StringBuffer>();
	
	public static void main(String[] args) throws Exception{
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
//					SocketServer1.LOGGER.info("======正在等待数据来到=======");
					continue;
				}
				 //这里就是本次询问操作系统，所获取到的“所关心的事件”的事件类型（每一个通道都是独立的）
				Iterator<SelectionKey> selectionKey =selector.selectedKeys().iterator();
				while(selectionKey.hasNext()){
					SelectionKey readyKey = selectionKey.next();
					//这个已经处理的readyKey一定要移除。如果不移除，就会一直存在在selector.selectedKeys集合中
                    //待到下一次selector.select() > 0时，这个readyKey又会被处理一次
					selectionKey.remove();
					
//					SelectableChannel selectableChannel = readyKey.channel();
					
					if(readyKey.isValid() && readyKey.isAcceptable()){
						  SocketServer2.LOGGER.info("======channel通道已经准备好=======");
						/*
						 * 当server socket channel通道已经准备好，就可以从server socket
						 * channel中获取socketchannel了 拿到socket
						 * channel后，要做的事情就是马上到selector注册这个socket channel感兴趣的事情。
						 * 否则无法监听到这个socket channel到达的数据
						 */
						ServerSocketChannel serverSocketChannel = (ServerSocketChannel) readyKey.channel();
						SocketChannel socketChannel = serverSocketChannel.accept();
						// 设置成非阻塞模式
						socketChannel.configureBlocking(false);
						//socket通道可以且只可以注册三种事件SelectionKey.OP_READ | SelectionKey.OP_WRITE | SelectionKey.OP_CONNECT
				        //最后一个参数视为 为这个socketchanne分配的缓存区
						socketChannel.register(selector, SelectionKey.OP_READ,ByteBuffer.allocate(50));
					}else if(readyKey.isValid() && readyKey.isReadable()){
						SocketChannel clientSocketChannel = (SocketChannel)readyKey.channel();
						  //获取客户端使用的端口
				        InetSocketAddress sourceSocketAddress = (InetSocketAddress)clientSocketChannel.getRemoteAddress();
				        Integer resoucePort = sourceSocketAddress.getPort();
				        //拿到这个socket channel使用的缓存区，准备读取数据
				        //在后文，将详细讲解缓存区的用法概念，实际上重要的就是三个元素capacity,position和limit。
				        ByteBuffer contextBytes = (ByteBuffer)readyKey.attachment();
				        //将通道的数据写入到缓存区，注意是写入到缓存区。
				        //这次，为了演示buff的使用方式，我们故意缩小了buff的容量大小到50byte，
				        //以便演示channel对buff的多次读写操作
				        int realLen = 0;
				        StringBuffer message = new StringBuffer();
				        //这句话的意思是，将目前通道中的数据写入到缓存区
				        //最大可写入的数据量就是buff的容量
				        while((realLen=clientSocketChannel.read(contextBytes))!=0){
				        	contextBytes.flip();
				        	int position = contextBytes.position();
				        	int capacity = contextBytes.capacity();
				        	byte[] messageBytes = new byte[capacity];
				        	contextBytes.get(messageBytes, position, realLen);
				        	//这种方式也是可以读取数据的，而且不用关心position的位置。
				            //因为是目前contextBytes所有的数据全部转出为一个byte数组。
				            //使用这种方式时，一定要自己控制好读取的最终位置（realLen很重要）
				            //byte[] messageBytes = contextBytes.array();
				        	//注意中文乱码的问题，我个人喜好是使用URLDecoder/URLEncoder，进行解编码。
				            //当然java nio框架本身也提供编解码方式，看个人咯
				            String messageEncode = new String(messageBytes , 0 , realLen , "UTF-8");
				            SocketServer2.LOGGER.info("端口："+resoucePort+"客户端方来的消息======messageEncode:"+messageEncode);
				            message.append(messageEncode);

				            //再切换成“写”模式，直接清空缓存的方式，最快捷
				            contextBytes.clear();
				        }
				        
				        if(URLDecoder.decode(message.toString(), "utf-8").indexOf("over")!=-1){
				        	Integer channelUUID = clientSocketChannel.hashCode();
				        	 SocketServer2.LOGGER.info("端口："+resoucePort+"客户端方来的消息======message:"+message);
				        	 StringBuffer completeMessage;
				        	 StringBuffer historyMessage = MESSAGEHASHCONTEXT.get(channelUUID);
				        	 if(historyMessage==null){
				        		 completeMessage = historyMessage;
				        	 }else{
				        		 completeMessage = historyMessage.append(message);
				        	 }
				        	 MESSAGEHASHCONTEXT.put(channelUUID, completeMessage);
				        	 SocketServer2.LOGGER.info("端口:" + resoucePort + "客户端发来的完整信息======completeMessage : " + URLDecoder.decode(completeMessage.toString(), "UTF-8"));
				        	 Iterator<Integer> it = MESSAGEHASHCONTEXT.keySet().iterator();
				        	 SocketServer2.LOGGER.info("当前接收的信息：");
				        	 while(it.hasNext()){
				        		 Integer key = it.next();
				        		 SocketServer2.LOGGER.info("channelUUID:"+key+",message:"+MESSAGEHASHCONTEXT.get(key));
				        	 }

				             //======================================================
				             //          当然接受完成后，可以在这里正式处理业务了        
				             //======================================================

				             //回发数据，并关闭channel
				        	 ByteBuffer sendBuffer = ByteBuffer.wrap(URLEncoder.encode("回发处理结果", "UTF-8").getBytes());
				             clientSocketChannel.write(sendBuffer);
				             clientSocketChannel.close();
				        }else{
				        	//每一个channel对象都是独立的，所以可以使用对象的hash值，作为唯一标示
				            Integer channelUUID = clientSocketChannel.hashCode();
				        	//如果没有发现有“over”关键字，说明还没有接受完，则将本次接受到的信息存入messageHashContext
				            SocketServer2.LOGGER.info("端口:" + resoucePort +",channelUUID:"+channelUUID+",客户端信息还未接受完，继续接受======message : " + URLDecoder.decode(message.toString(), "UTF-8"));
				           

				            //然后获取这个channel下以前已经达到的message信息
				            StringBuffer historyMessage = MESSAGEHASHCONTEXT.get(channelUUID);
				            if(historyMessage == null) {
				                historyMessage = new StringBuffer();
				                MESSAGEHASHCONTEXT.put(channelUUID, historyMessage.append(message));
				            }
				        }
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
