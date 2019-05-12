
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * @author luzuheng
 * @date 2019-05-12 23:25
 */
public class NioServer {
    public static void main(String[] args) {
        try {
            new NioServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        // 1. 创建 Selector
        Selector selector = Selector.open();
        // 2. 通过 ServerSocketChannel 创建 channel 通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 3. 为 channel 通道绑定监听窗口
        serverSocketChannel.bind(new InetSocketAddress(9876));
        // 4. 设置 channel 为非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 5. 将 channle 注册到 Selector 上，并监听连接时间
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务端启动成功");
        // 6. 循环等待新接入的连接
        for (;;) {
            // 获取可用的 channel 数量
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                continue;
            }
            // 可用的事件
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                iterator.remove();
                // 7. 根据就绪状态调用相应的处理逻辑
                // ① 如果是 接入事件
                if (selectionKey.isAcceptable()) {
                    acceptHandler(serverSocketChannel, selector);
                }
                // ② 如果是 可读事件
                if (selectionKey.isReadable()) {
                    readHandler(selectionKey, selector);
                }

            }
        }

    }

    /**
     * 接入事件处理器
     * @param serverSocketChannel
     * @param selector
     * @throws IOException
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector) throws IOException {
        // 创建 socketChannel
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 将 socketChannle 设置为非阻塞模式
        socketChannel.configureBlocking(false);
        // 将 channel 注册到 selector 上，监听 可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 回复客户端
        socketChannel.write(Charset.forName("UTF-8").encode("你与聊天室其他人都不是好友，请注意隐私安全"));
    }

    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {
        // 从 selectionKey 中获取到已经就绪的 channel
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 创建 buffer
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        // 循环读取客户端请求信息
        StringBuilder request = new StringBuilder();
        while (socketChannel.read(byteBuffer) > 0) {
            // 切换 buffer 为读模式
            byteBuffer.flip();
            // 读取 buffer 中的内容
            request.append(Charset.forName("UTF-8").decode(byteBuffer));
        }
        // 将 channel 注册到 selector 上，监听其他的可读事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        // 将客户端发送的信息广播给其他客户端
        if (request.length() > 0) {
            broadCast(selector, socketChannel, request.toString());
            System.out.println(":: " + request);
        }
    }

    private void broadCast(Selector selector, SocketChannel sourceChannel, String request) {
        // 获取所有已接入的客户端 channel
        Set<SelectionKey> selectionKeySet = selector.keys();
        selectionKeySet.forEach(selectionKey -> {
            SelectableChannel targetChannel = selectionKey.channel();
            // 剔除发消息的channel
            if (targetChannel instanceof SocketChannel && sourceChannel != targetChannel) {
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
