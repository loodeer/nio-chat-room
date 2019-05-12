import java.io.IOException;

/**
 * @author luzuheng
 * @date 2019-05-13 00:36
 */
public class AClient {

    public static void main(String[] args) {
        try {
            new NioClient().start("AClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
