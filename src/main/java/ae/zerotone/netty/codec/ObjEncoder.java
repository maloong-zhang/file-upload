package ae.zerotone.netty.codec;

import ae.zerotone.netty.util.SerializationUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class ObjEncoder extends MessageToByteEncoder {

  private final Class<?> genericClass;

  public ObjEncoder(Class<?> genericClass) {
    this.genericClass = genericClass;
  }

  @Override
  protected void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) {
    if (genericClass.isInstance(in)) {
      byte[] data = SerializationUtil.serialize(in);
      out.writeInt(data.length);
      out.writeBytes(data);
    }
  }
}
