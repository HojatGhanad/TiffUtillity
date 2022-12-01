package com.nanolyze.tiff.service;

import mil.nga.tiff.TIFFImage;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Service
public class RGBTiff {
    public static ImageReader readTiffFile(String fileName) throws IOException {
        File input = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName);
        ImageInputStream inputStream = null;
        Iterator<ImageReader> iterator;
        try {
            inputStream = ImageIO.createImageInputStream(input);
            if (inputStream == null || inputStream.length() == 0) {
                //TODO handle error
            }
            iterator = ImageIO.getImageReaders(inputStream);
            if (iterator == null || !iterator.hasNext()) {
                throw new IOException("Image file format not supported by ImageIO: ");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImageReader reader = (ImageReader) iterator.next();
        reader.setInput(inputStream);
        int numberOfFrames = reader.getNumImages(true);
        return reader;
    }

    public static int[][][] loadRGBTiff(ImageReader reader, int imageWidth, int imageHeight, int numberOfFrames) throws IOException {
        int[][][] loadedImage = new int[imageWidth][imageHeight][numberOfFrames];
        System.out.println("Reading file...");
        for (int frame = 0; frame < numberOfFrames; frame++) {
            BufferedImage image = reader.read(frame);
            for (int col = 0; col < imageWidth; col++) {
                for (int row = 0; row < imageHeight; row++) {
                    loadedImage[col][row][frame] = image.getRGB(col, row);
                }
            }
            System.out.println("frame = " + frame + " loaded.");
        }
        System.out.println("File has been loaded.");
        return loadedImage;
    }

    public static Color[][] computeAverageOnTiff(int[][][] loadedTiff, int imageWidth, int imageHeight, int numberOfFrames) {
        System.out.println("Getting average...");
        Color[][] finalImages = new Color[Math.toIntExact(imageWidth)][Math.toIntExact(imageHeight)];
        for (int col = 0; col < imageWidth; col++) {
            for (int row = 0; row < imageHeight; row++) {
                Color pixel;
                long red = 0;
                long green = 0;
                long blue = 0;
                for (int frame = 0; frame < numberOfFrames; frame++) {
                    pixel = new Color(loadedTiff[col][row][frame]);
                    red += pixel.getRed();
                    green += pixel.getGreen();
                    blue += pixel.getBlue();
                }
                finalImages[col][row] = new Color((int) (red / numberOfFrames), (int) (green / numberOfFrames), (int) (blue / numberOfFrames));
            }
        }
        System.out.println("Average has been calculated.");
        return finalImages;
    }

    public static void writeTiffFile(Color[][] finalImage, int imageWidth, int imageHeight, ImageReader reader, String fileName) throws IOException {
        int colorType = reader.read(0).getColorModel().getColorSpace().getType();
        BufferedImage image = new BufferedImage(imageWidth, imageHeight, colorType);
        for (int row = 0; row < imageHeight; row++) {
            for (int col = 0; col < imageWidth; col++) {
                int Pixel = finalImage[col][row].getRGB();
                image.setRGB(col, row, Pixel);
            }
        }
        ImageIO.write(image, "tiff", new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "avg-" + fileName));
        System.out.println("Output file has been saved.");
    }
}
