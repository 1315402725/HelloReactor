package main;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TcpHandler implements  Runnable{
    private  static ThreadPoolExecutor pool =
            new ThreadPoolExecutor(11,11,10, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());//线程池
     HandlerState state;//以状态模式实现handler
    private SelectionKey sk;
    private SocketChannel sc;
    public TcpHandler(SelectionKey sk, SocketChannel sc){
        this.sk = sk;
        this.sc = sc;
        state = new ReadState();//初始状态设定为READING
        pool.setMaximumPoolSize(32);//设定线程池最大线程数
    }

    @Override
    public void run() {
        try{
            state.handler(this,sk,sc,pool);
        }catch(IOException e){
            System.out.println("warning A client has been closed");
            closeChannel();
        }
    }
    public void closeChannel(){
        try{
            sk.cancel();
            sc.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public void setState(HandlerState state){
        this.state = state;
    }

    public static ThreadPoolExecutor getPool() {
        return pool;
    }

    public static void setPool(ThreadPoolExecutor pool) {
        TcpHandler.pool = pool;
    }

    public HandlerState getState() {
        return state;
    }

    public SelectionKey getSk() {
        return sk;
    }

    public void setSk(SelectionKey sk) {
        this.sk = sk;
    }

    public SocketChannel getSc() {
        return sc;
    }

    public void setSc(SocketChannel sc) {
        this.sc = sc;
    }
}
