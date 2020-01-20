package main;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public class WorkState implements  HandlerState{
    private SelectionKey sk;
    private TcpHandler tcpHandler;
    private String str;
    public WorkState(TcpHandler tcpHandler,String str){
        this.tcpHandler = tcpHandler;
        this.str = str;
    }

    @Override
    public void handler(TcpHandler tcpHandler, SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
       //todao auto-generated method stub
        //nothing to do

    }

    @Override
    public void changeState(TcpHandler h, SelectionKey key, SocketChannel sc, ThreadPoolExecutor pool) throws IOException {
        //TODO Auto-generated mehtod stub

    }
}
