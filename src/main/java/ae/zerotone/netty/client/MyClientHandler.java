package ae.zerotone.netty.client;

import ae.zerotone.netty.domain.Constants;
import ae.zerotone.netty.domain.FileBurstData;
import ae.zerotone.netty.domain.FileBurstInstruct;
import ae.zerotone.netty.domain.FileTransferProtocol;
import ae.zerotone.netty.util.FileUtil;
import ae.zerotone.netty.util.MsgUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyClientHandler extends ChannelInboundHandlerAdapter {

  /** 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据 */
  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    SocketChannel channel = (SocketChannel) ctx.channel();
    System.out.println("链接报告开始");
    System.out.println("链接报告信息：本客户端链接到服务端。channelId：" + channel.id());
    System.out.println("链接报告IP:" + channel.localAddress().getHostString());
    System.out.println("链接报告Port:" + channel.localAddress().getPort());
    System.out.println("链接报告完毕");
  }

  /** 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据 */
  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("断开链接" + ctx.channel().localAddress().toString());
    super.channelInactive(ctx);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // 数据格式验证
    if (!(msg instanceof FileTransferProtocol)) return;

    FileTransferProtocol fileTransferProtocol = (FileTransferProtocol) msg;
    // 0传输文件'请求'、1文件传输'指令'、2文件传输'数据'
    switch (fileTransferProtocol.getTransferType()) {
      case 1: // 收到服务端文件可以传输指令
        FileBurstInstruct fileBurstInstruct =
            (FileBurstInstruct) fileTransferProtocol.getTransferObj();
        // Constants.FileStatus ｛0开始、1中间、2结尾、3完成｝
        if (Constants.FileStatus.COMPLETE == fileBurstInstruct.getStatus()) {
          ctx.flush();
          ctx.close();
          System.exit(-1);
          return;
        }
        FileBurstData fileBurstData =
            FileUtil.readFile(
                fileBurstInstruct.getClientFileUrl(), fileBurstInstruct.getReadPosition());
        ctx.writeAndFlush(MsgUtil.buildTransferData(fileBurstData));
        System.out.println(
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + "File manage 客户端传输文件信息。 FILE："
                + fileBurstData.getFileName()
                + " SIZE(byte)："
                + (fileBurstData.getEndPos() - fileBurstData.getBeginPos()));
        break;
      default:
        break;
    }

    /**
     * 模拟传输过程中断，场景测试可以注释掉
     *
     * <p>System.out.println(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + " File
     * manage客户端传输文件信息[主动断开链接，模拟断点续传]"); ctx.flush(); ctx.close(); System.exit(-1);
     */
  }

  /** 抓住异常，当发生异常的时候，可以做一些相应的处理，比如打印日志、关闭链接 */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.close();
    System.out.println("异常信息：\r\n" + cause.getMessage());
  }
}
