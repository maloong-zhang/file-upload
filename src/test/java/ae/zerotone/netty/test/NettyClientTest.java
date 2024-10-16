package ae.zerotone.netty.test;

import ae.zerotone.netty.client.NettyClient;
import ae.zerotone.netty.domain.FileTransferProtocol;
import ae.zerotone.netty.util.MsgUtil;
import io.netty.channel.ChannelFuture;
import java.io.File;

public class NettyClientTest {

  public static void main(String[] args) {

    // 启动客户端
    ChannelFuture channelFuture = new NettyClient().connect("127.0.0.1", 7397);

    // 文件信息{文件大于1024kb方便测试断点续传}
    File file =
        new File(
            "/Users/andying/github/maloong-zhang/netty/file-upload/src/test/java/ae/zerotone/netty/test/测试传输文件.zip");
    FileTransferProtocol fileTransferProtocol =
        MsgUtil.buildRequestTransferFile(file.getAbsolutePath(), file.getName(), file.length());

    // 发送信息；请求传输文件
    channelFuture.channel().writeAndFlush(fileTransferProtocol);
  }
}
