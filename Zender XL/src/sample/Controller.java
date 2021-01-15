package sample;

import com.google.zxing.WriterException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class Controller implements Initializable {

    FileWriter f0 = null;

    @FXML
    public AnchorPane root;

    private Queue<Design> queueDesign = new LinkedList<>();

    private Double max = 0d;
    private Double count = 0d;

    @FXML
    public ProgressBar pgTask;

    @FXML

    File render;
    public static String name;

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
                if (design.length > 0) {
                    File dirDesign = new File(render, dir.getName());
//                    if (!dirDesign.exists()) {
//                        dirDesign.mkdirs();
//                    }
                    for (File f : design) {
                        Design d = new Design();
                        d.setName(dir.getName());
                        d.setUrl(f.getAbsolutePath());
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

    private void render() throws IOException, WriterException {
        count = 0d;
        Color color;
        while (!queueDesign.isEmpty()) {
            BufferedImage bi = new BufferedImage(Constants.WIDTH_RENDER, Constants.HEIGHT_RENDER + Constants.PADDING_HEIGHT, BufferedImage.TYPE_INT_RGB);
            Graphics2D    graphics = bi.createGraphics();

            graphics.setPaint ( Color.white );
            graphics.fillRect ( 0, 0, bi.getWidth(), bi.getHeight() );
            /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
            String name = simpleDateFormat.format(Calendar.getInstance().getTime());*/
            /*Design design = queueDesign.remove();
            String name = design.getName();
            File file = new File(render ,name + ".txt");
            file.createNewFile();
            f0 = new FileWriter(file);*/
            for (int i = 0; i < 1; i++) {
                for (int j = 0; j < 1; j++) {
                    if (!queueDesign.isEmpty()) {

                        /*design = queueDesign.remove();*/
                        Design design = queueDesign.remove();
                        name = design.getName();
                        File file = new File(render ,name + ".txt");
                        file.createNewFile();
                        f0 = new FileWriter(file);
                        try {
                            System.out.println(design.getName());
                            f0.write(design.getName() + "\n");
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                        InputStream is = new FileInputStream(new File(design.getUrl()));
                        BufferedImage bufferedImage = ImageIO.read(is);
                        int w = bufferedImage.getWidth();
                        int h = bufferedImage.getHeight();

                        BufferedImage newDesign = ImageIO.read(new File("model.png"));
                        for (int x = 0; x < w ; x++) {
                            for (int y = 0; y < h; y++) {

                                    newDesign.setRGB(x + 59, y+ 354, bufferedImage.getRGB(x, y));

                            }
                        }

                        BufferedImage label = LabelUtils.makeLabel(design.getName());
                        /*label = rotateImage90(label);*/
                        LabelUtils.addlabelTopLeft(newDesign, label);

                        BufferedImage qrCode = QrCode.createQRImage(file, design.getName().replaceAll("X-Large_", ""), 280, "png");
                        QrCode.addlabelTopLeft(newDesign, qrCode);


                        for (int x = 0; x < Constants.WIDTH_DESIGN; x++) {
                            for (int y = 0; y < Constants.HEIGHT_DESIGN + Constants.PADDING_HEIGHT; y++) {
                                try {
                                    color = new Color(newDesign.getRGB(x,y),true);
                                    if(color.getAlpha() > 0) {
                                        bi.setRGB(x + (j * (Constants.WIDTH_DESIGN + Constants.PADDING_WIDTH)), y + (i * (Constants.HEIGHT_DESIGN + Constants.PADDING_HEIGHT)), newDesign.getRGB(x, y));
                                    }
                                    /*bi.setRGB(x + (j * (Constants.WIDTH_DESIGN + Constants.PADDING_WIDTH)), y + (i * (Constants.HEIGHT_DESIGN + Constants.PADDING_HEIGHT)), newDesign.getRGB(x, y));*/
                                } catch (IndexOutOfBoundsException e) {

                                }

                            }
                        }

                        count += 1;
                        Platform.runLater(() -> {
                            pgTask.setProgress(count / max);
                        });
                    } else {
                        continue;
                    }

                }
            }

            ImageDAO imageDAO = new ImageDAO();
            imageDAO.renderPicture(bi, name, render);
            System.out.println(name);
            System.out.println("ok");
            f0.close();
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
