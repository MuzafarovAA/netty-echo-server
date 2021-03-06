package ru.gb.netty.echo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

public class ClientApp {

    public static final String HOST = "localhost";
    public static final int PORT = 9000;

    public static void main(String[] args) {

        new ClientApp().start();

    }

    private void start() {


        EventLoopGroup workerGroup = new NioEventLoopGroup(1);

        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                            nioSocketChannel.pipeline().addLast(
                                    new LengthFieldBasedFrameDecoder(1024,0,2,0,2),
                                    new LengthFieldPrepender(2),
                                    new StringDecoder(),
                                    new StringEncoder(),
                                    new ClientHandler()
                            );
                        }
                    })
                    .option(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture futureChannel = bootstrap.connect(HOST, PORT).sync();

            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.next();
                futureChannel.channel().writeAndFlush(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }


    }

}
