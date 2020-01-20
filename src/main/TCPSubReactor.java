package main;

import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

public class TCPSubReactor implements  Runnable {
    private final ServerSocketChannel ssc;
    private final Selector selector;
    private boolean restart = false;
    int num;
    public TCPSubReactor(Selector selector,ServerSocketChannel ssc,int num){
        this.ssc = ssc;
        this.selector = selector;
        this.num = num;
    }
    @Override
    public void run() {
        while(!Thread.interrupted()){//在线程被中断前持续运行
          //  System.out.println("waiting for restart");
            while(!Thread.interrupted() && !restart){//在线程被中断前以及被指定重启前持续运行
                try{
                    if(selector.select()==0){
                        continue;//若没有事件就绪则不往下执行
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();//取得所有已就绪事件的key集合
                Iterator<SelectionKey> it = selectionKeys.iterator();
                while(it.hasNext()){
                    dispatch((SelectionKey)(it.next()));//根据事件的key进行调度
                    it.remove();
                }

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



    public void setRestart(boolean restart) {
        this.restart = restart;
    }
}
