package sample;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.imageio.plugins.common.ImageUtil;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {

    @FXML
    public AnchorPane root;

    private Queue<Design> queueDesign = new LinkedList<>();

    private Double max = 0d;
    private Double count = 0d;

    @FXML
    public ProgressBar pgTask;

    @FXML

    File render;

    public void onOpenFolder(ActionEvent actionEvent) {
        max = 0d;
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("JavaFX Projects");
        File defaultDirectory = new File("c:/");
        chooser.setInitialDirectory(defaultDirectory);
        File selectedDirectory = chooser.showDialog(root.getScene().getWindow());
        if (selectedDirectory.exists() && selectedDirectory.isDirectory()) {
            File[] designFolders = selectedDirectory.listFiles();
            for (File dir : designFolders) {
                File[] design = dir.listFiles();
                if (design != null) {
                    if (design.length > 0) {
                        Design d = new Design();
                        d.setName(dir.getName());
                        d.setUrl(dir.getAbsolutePath());
                        queueDesign.add(d);
                        max += 1;

                    }
                }
            }
        }

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                render();
                return null;
            }
        };
        new Thread(task).start();


    }

    private void render() throws IOException {
        count = 0d;
        while (!queueDesign.isEmpty()) {
            ImageDAO imageDAO = new ImageDAO();
            Design design = queueDesign.remove();
            File[] designIMG = (new File(design.getUrl())).listFiles();

            File dir = new File(render, design.getName());
            if (!dir.exists()) dir.mkdirs();
            System.out.println(dir);
            for (File f : designIMG) {
                BufferedImage bufferedImage = null;
                try {
                    bufferedImage = ImageIO.read(f);
                } catch (IIOException e) {
                    bufferedImage = JPEGCodec.createJPEGDecoder(new FileInputStream(f)).decodeAsBufferedImage();
                }
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();

                /*bufferedImage = rotateImage90(bufferedImage);
                int w = bufferedImage.getWidth();
                int h = bufferedImage.getHeight();*/
                    
                if (w > h) {
                    bufferedImage = rotateImage90(bufferedImage);
                    w = bufferedImage.getWidth();
                    h = bufferedImage.getHeight();
                }

                BufferedImage resized = new BufferedImage(Constants.WIDTH_DESIGN, Constants.HEIGHT_DESIGN, bufferedImage.getType());
                Graphics2D g = resized.createGraphics();
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(bufferedImage, 0, 0, Constants.WIDTH_DESIGN, Constants.HEIGHT_DESIGN, 0, 0, bufferedImage.getWidth(),
                        bufferedImage.getHeight(), null);
                g.dispose();

                imageDAO.renderPicture(resized, f.getName(), dir.getAbsoluteFile());
            }
            count += 1;
            Platform.runLater(() -> {
                pgTask.setProgress(count / max);
            });
        }


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        render = new File("render");
        if (!render.exists()) {
            render.mkdirs();
        }
    }

    private BufferedImage rotateImage90(BufferedImage original) {
        int originH = original.getHeight();
        BufferedImage rotated = new BufferedImage(original.getHeight(), original.getWidth(), original.getType());
        int rotateWidth = rotated.getWidth();
        int rotateHeight = rotated.getHeight();
        for (int i = 0; i < rotateWidth; i++) {
            for (int j = 0; j < rotateHeight; j++) {
                rotated.setRGB(i, j, original.getRGB(j, originH - 1 - i));
            }
        }
        return rotated;
    }
}
