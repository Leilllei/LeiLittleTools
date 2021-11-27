import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TestDemo0 {
    public static void main(String[] args) throws IOException {
//        //1.读取本地png图片or读取url图片
//        File input = new File("src/main/resources/011_009_.png");
//        BufferedImage bimg = ImageIO.read(input);//读取本地图片
//        //BufferedImage bimg = ImageIO.read(new URL("http://img.alicdn.com/tfs/TB1kbMoUOLaK1RjSZFxXXamPFXa.png"));//读取url图片
//
//        //2. 填充透明背景为白色
//        BufferedImage res = new BufferedImage(bimg.getWidth(), bimg.getHeight(), BufferedImage.TYPE_INT_RGB);
//        res.createGraphics().drawImage(bimg, 0, 0, Color.WHITE, null); //背景填充色设置为白色，也可以设置为其他颜色比如粉色Color.PINK
//
//        //3. 保存成jpg到本地
//        File output = new File("src/main/resources/011_009_.jpg");
//        ImageIO.write(res, "jpg", output);

//        ImageResize.scaleMaxToMaxLength("src/main/resources/011_009_.png", "src/main/resources/011_009_0.jpg", 2560);

        File input = new File("src/main/resources/011_009_.png");
        ImageResize.isImage(input);
    }
}
