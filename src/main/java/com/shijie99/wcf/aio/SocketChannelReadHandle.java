package com.shijie99.wcf.aio;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.apache.log4j.Logger;

public class SocketChannelReadHandle implements
		CompletionHandler<Integer, StringBuffer> {
	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger	.getLogger(SocketChannelReadHandle.class);

	private AsynchronousSocketChannel socketChannel;
	/**
	 * 专门用于进行这个通道数据缓存操作的ByteBuffer<br>
	 * 当然，您也可以作为CompletionHandler的attachment形式传入。<br>
	 * 这是，在这段示例代码中，attachment被我们用来记录所有传送过来的Stringbuffer了。
	 */
	private ByteBuffer byteBuffer;

	public SocketChannelReadHandle(AsynchronousSocketChannel socketChannel,ByteBuffer byteBuffer) {
		this.socketChannel = socketChannel;
		this.byteBuffer = byteBuffer;
	}
	@Override
	public void completed(Integer result, StringBuffer historyContext) {
		
		LOGGER.info("result："+result);
		if(result ==-1){
			try{
				this.socketChannel.close();
			}catch (Exception e) {
				LOGGER.error(e);
			}
		}
		
		LOGGER.info("completed(Integer result, Void attachment) : 然后我们来取出通道中准备好的值");
		 /*
         * 实际上，由于我们从Integer result知道了本次channel从操作系统获取数据总长度
         * 所以实际上，我们不需要切换成“读模式”的，但是为了保证编码的规范性，还是建议进行切换。
         * 
         * 另外，无论是JAVA AIO框架还是JAVA NIO框架，都会出现“buffer的总容量”小于“当前从操作系统获取到的总数据量”，
         * 但区别是，JAVA AIO框架中，我们不需要专门考虑处理这样的情况，因为JAVA AIO框架已经帮我们做了处理（做成了多次通知）
         * */
        this.byteBuffer.flip();
        byte[] contexts = new byte[1024];
        this.byteBuffer.get(contexts, 0, result);
        this.byteBuffer.clear();
        try{
        	String nowContent = new String(contexts,0,result,"utf-8");
        	historyContext.append(nowContent);
        }catch (Exception e) {
        	LOGGER.error(e);
		}
        
        if(historyContext.indexOf("over") ==-1){
        	//还要继续监听（一次监听一次通知）
            this.socketChannel.read(this.byteBuffer, historyContext, this);
        	return;
        }
   	 	
        //=========================================================================
        //          和上篇文章的代码相同，我们以“over”符号作为客户端完整信息的标记
        //=========================================================================
        LOGGER.info("=======收到完整信息，开始处理业务=========");
        try {
        	LOGGER.info(("收到的信息："+URLDecoder.decode(historyContext.toString(),"utf-8")));
        	ByteBuffer sendBuffer = ByteBuffer.wrap(URLEncoder.encode("回发处理结果", "UTF-8").getBytes());
			this.socketChannel.write(sendBuffer);
        	this.socketChannel.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
       
        historyContext = new StringBuffer();

       
        //还要继续监听（一次监听一次通知）
//        this.socketChannel.read(this.byteBuffer, historyContext, this);
	}

	@Override
	public void failed(Throwable exc, StringBuffer attachment) {
		LOGGER.info("=====发现客户端异常关闭，服务器将关闭TCP通道");
		try {
			this.socketChannel.close();
		} catch (IOException e) {
			SocketChannelReadHandle.LOGGER.error(e);
		}
	}

}
