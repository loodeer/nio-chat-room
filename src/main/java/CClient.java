import java.io.IOException;

/**
 * @author luzuheng
 * @date 2019-05-13 00:36
 */
public class CClient {

    public static void main(String[] args) {
        try {
            new NioClient().start("CClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
