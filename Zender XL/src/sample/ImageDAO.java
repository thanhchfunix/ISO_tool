package sample;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class ImageDAO {


    private static final int DPI = 150;
    private static final double INCH_2_CM = 2.54;
    private BufferedImage gridImage;

    public static BufferedImage loadBF(File fileModel) {
        BufferedImage in = null;
        try {
            in = ImageIO.read(fileModel);
        } catch (IOException e) {
            e.printStackTrace();
        }

        BufferedImage newImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g = newImage.createGraphics();
        g.drawImage(in, 0, 0, null);
        g.dispose();
        return in;
    }

    public void renderPicture(BufferedImage buffMain, String name, File selectedDirectory) {
        File file = new File(selectedDirectory, name + ".jpg");
        try {
            saveBufferedImageAsJPEG(buffMain, 150, file);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void saveBufferedImageAsJPEG(BufferedImage bi, int dpi, File fileName) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileName);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bi);
            param.setQuality(0.8f, true);
            param.setDensityUnit(JPEGEncodeParam.DENSITY_UNIT_DOTS_INCH);
            param.setXDensity(dpi);
            param.setYDensity(dpi);
            encoder.setJPEGEncodeParam(param);
            try {
                encoder.encode(bi);
                out.close();
            } catch (IOException io) {
                System.out.println(io);
            }
        } catch (FileNotFoundException fnf) {
            System.out.println("File Not Found");
        }
    }

    public void saveGridImage(File output, BufferedImage buffOutput) throws IOException {
        output.delete();
        gridImage = buffOutput;
        final String formatName = "png";

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName(formatName); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB_PRE);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata);

            final ImageOutputStream stream = ImageIO.createImageOutputStream(output);
            try {
                writer.setOutput(stream);
                writer.write(metadata, new IIOImage(gridImage, null, metadata), writeParam);
            } finally {
                stream.close();
            }
            break;
        }
        gridImage = null;
    }

    private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {

        // for PMG, it's dots per millimeter
        //double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;
        double dotsPerMilli = 1.0 * DPI / 10 / INCH_2_CM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    public void makePreview(File preview, BufferedImage buffPreview) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(preview);
            ImageIO.write(buffPreview, "PNG", fileOutputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
