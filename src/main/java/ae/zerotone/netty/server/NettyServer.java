package ae.zerotone.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class NettyServer {

  // 配置服务端NIO线程组
  private final EventLoopGroup parentGroup =
      new NioEventLoopGroup(); // NioEventLoopGroup extends MultithreadEventLoopGroup Math.max(1,
  // SystemPropertyUtil.getInt("io.netty.eventLoopThreads",
  // NettyRuntime.availableProcessors() * 2));
  private final EventLoopGroup childGroup = new NioEventLoopGroup();
  private Channel channel;

  public ChannelFuture bing(int port) {
    ChannelFuture channelFuture = null;
    try {
      ServerBootstrap b = new ServerBootstrap();
      b.group(parentGroup, childGroup)
          .channel(NioServerSocketChannel.class) // 非阻塞模式
          .option(ChannelOption.SO_BACKLOG, 128)
          .childHandler(new MyChannelInitializer());
      channelFuture = b.bind(port).syncUninterruptibly();
      this.channel = channelFuture.channel();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (null != channelFuture && channelFuture.isSuccess()) {
        System.out.println("File manage server start done.");
      } else {
        System.out.println("File manage server start error.");
      }
    }
    return channelFuture;
  }

  public void destroy() {
    if (null == channel) return;
    channel.close();
    parentGroup.shutdownGracefully();
    childGroup.shutdownGracefully();
  }

  public Channel getChannel() {
    return channel;
  }
}
