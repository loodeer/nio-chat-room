import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * @author luzuheng
 * @date 2019-05-12 23:25
 */
public class NioClient {
    public void start(String nickname) throws IOException {
        // 连接服务器端
        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9876));
        System.out.println("客户端启动成功");

        // 新开线程，专门负责来接受服务器端的响应数据
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();

        // 向服务器端发送数据
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String request = scanner.nextLine();
            if (!StringUtils.isEmpty(request)) {
                socketChannel.write(Charset.forName("UTF-8").encode(nickname + ":" + request));
            }
        }
        // 接受服务器响应
    }
}
