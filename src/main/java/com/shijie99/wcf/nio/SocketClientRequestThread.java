package com.shijie99.wcf.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public class SocketClientRequestThread implements Runnable {
	/**
	 * 日志
	 */
	private static final Logger LOGGER = Logger.getLogger(SocketClientRequestThread.class);

    private CountDownLatch countDownLatch;
    private static Random random = new Random();

    /**
     * 这个线层的编号
     * @param countDownLatch
     */
    private Integer clientIndex;

    /**
     * countDownLatch是java提供的同步计数器。
     * 当计数器数值减为0时，所有受其影响而等待的线程将会被激活。这样保证模拟并发请求的真实性
     * @param countDownLatch
     */
    public SocketClientRequestThread(CountDownLatch countDownLatch , Integer clientIndex) {
        this.countDownLatch = countDownLatch;
        this.clientIndex = clientIndex;
    }

    @Override
    public void run() {
        Socket socket = null;
        OutputStream clientRequest = null;
        InputStream clientResponse = null;

        try {
           

            //等待，直到SocketClientDaemon完成所有线程的启动，然后所有线程一起发送请求
            this.countDownLatch.await();
            int s = random.nextInt(10);
            SocketClientRequestThread.LOGGER.info("线程："+this.clientIndex+"将在："+s+"秒后开始建立通道并开始发送数据");
            TimeUnit.SECONDS.sleep(s);
            
            socket = new Socket("localhost",83);
            clientRequest = socket.getOutputStream();
            clientResponse = socket.getInputStream();
            //发送请求信息
            clientRequest.write(URLEncoder.encode("这是第" + this.clientIndex + " 个客户端的请求,请求还没有发送完成,请等待。", "utf-8").getBytes());
            clientRequest.flush();
            TimeUnit.SECONDS.sleep(3+random.nextInt(10));
            clientRequest.write(URLEncoder.encode("这是第" + this.clientIndex + " 个客户端的请求,完成over", "utf-8").getBytes());
            clientRequest.flush();
            //在这里等待，直到服务器返回信息
            SocketClientRequestThread.LOGGER.info("第" + this.clientIndex + "个客户端的请求发送完成，等待服务器返回信息");
            int maxLen = 1024;
            byte[] contextBytes = new byte[maxLen];
            int realLen;
            String message = "";
            //程序执行到这里，会一直等待服务器返回信息（注意，前提是in和out都不能close，如果close了就收不到服务器的反馈了）
            while((realLen = clientResponse.read(contextBytes, 0, maxLen)) != -1) {
                message += new String(contextBytes , 0 , realLen);
            }
            SocketClientRequestThread.LOGGER.info("接收到来自服务器的信息:" + URLDecoder.decode(message,"utf-8"));
        } catch (Exception e) {
            SocketClientRequestThread.LOGGER.error(e.getMessage(), e);
        } finally {
            try {
                if(clientRequest != null) {
                    clientRequest.close();
                }
                if(clientResponse != null) {
                    clientResponse.close();
                }
            } catch (IOException e) {
                SocketClientRequestThread.LOGGER.error(e.getMessage(), e);
            }
        }
    }
}
