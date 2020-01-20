package main;

import javafx.concurrent.WorkerStateEvent;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class ReadState implements  HandlerState{
    private SelectionKey sk;
    @Override
    public void handler(TcpHandler h, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
        this.sk =sk;
        //non-blocking 下不可用Readers 因为Readers不支持non-blocking
        byte[] arr = new byte[1024];
        ByteBuffer buf = ByteBuffer.wrap(arr);

        int numBytes = sc.read(buf);//读取字符串
        if(numBytes == -1){
            System.out.println("[Warning!] A client has been closed");
            h.closeChannel();
            return ;
        }
        String str = new String(arr);//将读取到的byte内容转为字符串
        System.out.println(h.getSc().getLocalAddress().toString()+"说："+str);
        if((str!=null) && !str.equals("")){
            h.setState(new WorkState(h,str));//改变状态
            pool.execute(new WorkerThread(h,str));//do process in worker thread
        }
    }

    @Override
    public void changeState(TcpHandler h, SelectionKey key, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {

    }

    synchronized  void process(TcpHandler h,String str){

        //处理业务逻辑
        //do process(decode,logically process, encode)..
        h.setState(new WriteState(h,str));//改变状态（Working -> Sending）
        this.sk.interestOps(SelectionKey.OP_WRITE);//通过key改变通道注册事件
        this.sk.selector().wakeup();//使一个阻塞住的selector操作立即返回
    }

    /**
     * 工作者线程
     */
    class WorkerThread implements   Runnable{

        TcpHandler h;
        String str;

        public WorkerThread(TcpHandler h,String str){
            this.h = h;
            this.str = str;
        }

        @Override
        public void run() {
            process(h,str);
        }
    }
}
