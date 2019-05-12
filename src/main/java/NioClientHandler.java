import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author luzuheng
 * @date 2019-05-13 00:20
 * 客户端线程类，专门接受服务器响应信息
 */
public class NioClientHandler implements Runnable{

    private Selector selector;

    public NioClientHandler(Selector selector) {
        this.selector = selector;
    }

    @Override public void run() {
        try {
            for (;;) {
                int readyChannels = selector.select();
                if (readyChannels == 0) {
                    continue;
                }
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    iterator.remove();
                    if (selectionKey.isReadable()) {
                        readHandler(selectionKey, selector);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 从 selectionKey 中获取到已经就绪的 channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建 buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 循环读取服务端请求信息
        StringBuilder response = new StringBuilder();
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换 buffer 为读模式
            byteBuffer.flip();
            // 读取 buffer 中的内容
            response.append(Charset.forName("UTF-8").decode(byteBuffer));
        }
        // 将 channel 注册到 selector 上，监听其他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 输出将服务器端发送的信息
        if (response.length() > 0) {
            System.out.println(":: " + response);
        }
    }
}
