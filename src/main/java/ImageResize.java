import org.apache.tika.Tika;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ImageResize {

    public static void main(String[] args) throws IOException {

        long startTime = System.currentTimeMillis();

        int THREAD_NUM = 8;
        int MAX_LENGTH = 2560;
        if (args.length == 1) {
            System.out.println("Set MAX_LENGTH to 2560");
        } else if (args[1].equals("4k")) {
            MAX_LENGTH = 3840;
            System.out.println("Set MAX_LENGTH to 3840");
        } else if (args[1].equals("default")) {
            MAX_LENGTH = 114514;
            System.out.println("Set MAX_LENGTH to the origin");
        }
        final int MAX_LENGTH_INNER = MAX_LENGTH;

        final String srcPath = args[0];
        final String targetPath = srcPath + "_resized";

        List<String> srcPathList = getFileList(srcPath, new ArrayList<>());
        List<List<String>> filesTaskList = averageAssign(srcPathList, THREAD_NUM);
        CountDownLatch latch = new CountDownLatch(THREAD_NUM);

        AtomicInteger doneCount = new AtomicInteger(0);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_NUM);
        for (List<String> filesToScale : filesTaskList
        ) {
            executor.submit(() -> {
                for (String fileToScale : filesToScale
                ) {
                    try {
                        scaleMaxToMaxLength(fileToScale, fileToScale.replace(srcPath, targetPath), MAX_LENGTH_INNER);
                        doneCount.incrementAndGet();
                        System.out.print("\r" + doneCount.get() + " / " +
                                srcPathList.size() + " have been done, for now, it spends: " + (System.currentTimeMillis() - startTime) / 1000.0 + " sec.");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        executor.shutdown();
    }

    public static void scaleMaxToMaxLength(String srcPath, String targetPath, int MAX_LENGTH) throws IOException {

        BufferedImage srcImage = ImageIO.read(new File(srcPath));

        int srcHeight = srcImage.getHeight();
        int srcWidth = srcImage.getWidth();

        int targetWidth, targetHeight;
        BufferedImage scaledImage = null;
        if (Math.max(srcHeight, srcWidth) > MAX_LENGTH) {
            if (srcWidth > srcHeight) {
                targetWidth = MAX_LENGTH;
                targetHeight = MAX_LENGTH * srcHeight / srcWidth;
            } else {
                targetWidth = MAX_LENGTH * srcWidth / srcHeight;
                targetHeight = MAX_LENGTH;
            }

            Image scaledImageTemp = srcImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_REPLICATE);

            scaledImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics2D = scaledImage.createGraphics();
            graphics2D.drawImage(scaledImageTemp, 0, 0, Color.WHITE, null);
            graphics2D.dispose();
        } else if (srcPath.toLowerCase().endsWith("png")) {
            scaledImage = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
            scaledImage.createGraphics().drawImage(srcImage, 0, 0, Color.WHITE, null); //背景填充色设置为白色，也可以设置为其他颜色比如粉色Color.PINK
        } else {
            scaledImage = srcImage;
        }

        File targetImageFile = new File(targetPath);
        if (!targetImageFile.exists()) {
            targetImageFile.getParentFile().mkdirs();
        }

        ImageIO.write(scaledImage, "jpg", targetImageFile);
    }

    public static List<String> getFileList(String srcPath, List<String> filePathList) throws IOException {
        File dir = new File(srcPath);
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files
            ) {
                if (file.isDirectory()) {
                    getFileList(file.getAbsolutePath(), filePathList);
                } else if (isImage(file)) {
                    String filePath = file.getAbsolutePath();
                    filePathList.add(filePath);
                }
            }
        }
        return filePathList;
    }

    public static boolean isImage(File file) throws IOException {
        Tika tika = new Tika();
        String mimeType = tika.detect(file);
        return mimeType.startsWith("image");
    }

    private static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;
        int number = source.size() / n;

        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value = null;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }
}
