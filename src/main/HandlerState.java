package main;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ThreadPoolExecutor;

public interface HandlerState {

     void handler(TcpHandler tcpHandler , SelectionKey sk, SocketChannel sc, ThreadPoolExecutor pool) throws IOException;
     void changeState(TcpHandler h,SelectionKey key,SocketChannel sc,ThreadPoolExecutor pool) throws IOException;
}
