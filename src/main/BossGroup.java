package main;

import jdk.nashorn.internal.ir.CatchNode;

import javax.swing.text.html.HTMLDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class BossGroup implements Runnable{

    private ServerSocketChannel ssc;

    private Selector sc;

    public BossGroup(int port){

        try {
            ssc = ServerSocketChannel.open();
            sc = Selector.open();
            ssc.configureBlocking(false);//设置为非阻塞
            SelectionKey sk = ssc.register(sc,SelectionKey.OP_ACCEPT);
            sk.attach(new Acceptor(ssc));//指定附加对象
            InetSocketAddress addr = new InetSocketAddress(port);
            ssc.socket().bind(addr);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    @Override
    public void run() {
        while(!Thread.interrupted()){
            System.out.println("waiting for new event on port:"+ssc.socket().getLocalPort());
            try{
                if(sc.select() == 0){//没有连接进来，则持续等待
                    continue;
                }
            } catch(IOException e){
                e.printStackTrace();
            }
            Set<SelectionKey> selectedKeys = sc.selectedKeys();
            Iterator<SelectionKey> it = selectedKeys.iterator();
            while(it.hasNext()){
                dispatch((SelectionKey)(it.next()));
                it.remove();//调用完后移除
            }
        }

    }

    public void dispatch(SelectionKey s){
        //调用run方法
        Runnable r = (Runnable)s.attachment();
        if(r!=null){
            r.run();
        }
    }
}
