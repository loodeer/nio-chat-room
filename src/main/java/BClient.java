import java.io.IOException;

/**
 * @author luzuheng
 * @date 2019-05-13 00:36
 */
public class BClient {

    public static void main(String[] args) {
        try {
            new NioClient().start("BClient");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
