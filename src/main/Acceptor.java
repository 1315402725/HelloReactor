package main;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Acceptor implements  Runnable{

    private final ServerSocketChannel ssc;
   // private final int cores = Runtime.getRuntime().availableProcessors();
    private final int cores = 8;
    private final Selector[] selectors = new Selector[cores];
    private int selIndex=0;
    private TCPSubReactor r[] = new TCPSubReactor[cores];
    private Thread[]  t = new Thread[cores];
    public Acceptor(ServerSocketChannel ssc){
        this.ssc = ssc;
        try{
            for(int i=0;i<cores;i++){
                selectors[i] = Selector.open();
                r[i] = new TCPSubReactor(selectors[i],ssc,i);
                t[i] = new Thread(r[i]);
                t[i].start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
       try{
           SocketChannel sc = ssc.accept();//接受client连接请求

           if(sc!=null){
               System.out.println("client has connected,ip is" + sc.getLocalAddress());
               sc.configureBlocking(false);
               r[selIndex].setRestart(true);
               selectors[selIndex].wakeup();//使一个阻塞住的selector操作立即返回
               SelectionKey sk = sc.register(selectors[selIndex],SelectionKey.OP_READ);
               selectors[selIndex].wakeup();//r[selIdx].
               r[selIndex].setRestart(false);//重放线程
               sk.attach(new TcpHandler(sk,sc));
               //给定key一个附加的TcpHandler
               if(++selIndex == selectors.length){
                   selIndex++;
               }
           }
       }catch (IOException e){
           e.printStackTrace();
       }
    }
}
