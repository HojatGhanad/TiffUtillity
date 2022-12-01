package com.nanolyze.tiff;

import com.nanolyze.tiff.service.GrayScaleTiff;
import com.nanolyze.tiff.service.RGBTiff;
import mil.nga.tiff.FileDirectory;
import mil.nga.tiff.TIFFImage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.imageio.ImageReader;
import java.awt.*;
import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class TiffApplication {


    public static void main(String[] args) {
        SpringApplication.run(TiffApplication.class, args);
        try {
            Scanner scanner = new Scanner(System.in);
            System.out.println("Please Enter File name with extension: ");
            String fileName = scanner.nextLine();
            TIFFImage tiffImage = GrayScaleTiff.readTiffFile(fileName);
            List<FileDirectory> directories = tiffImage.getFileDirectories();
            FileDirectory directory = directories.get(0);
            int bytesPerSample = directory.getBitsPerSample().size();
            int samplePerPixel = directory.getSamplesPerPixel();
            int numberOfFrames = directories.size();
            System.out.println("File has " + numberOfFrames + " frames.");
            int imageHeight = directory.getImageHeight().intValue();
            int imageWidth = directory.getImageWidth().intValue();
            if (bytesPerSample == 1) {
                short[][][] loadedTiff = GrayScaleTiff.loadGrayScaleTiff(tiffImage, imageWidth, imageHeight, numberOfFrames);
                short[][] finalImage = GrayScaleTiff.computeAverageOnTiff(loadedTiff, imageWidth, imageHeight, numberOfFrames);
                GrayScaleTiff.writeTiffFile(finalImage, imageWidth, imageHeight, samplePerPixel, fileName);
            } else {
                //TODO handle RGB Tiffs
                ImageReader imageReader = RGBTiff.readTiffFile(fileName);
                int[][][] loadRGBTiff = RGBTiff.loadRGBTiff(imageReader, imageWidth, imageHeight, numberOfFrames);
                Color[][] finalImage = RGBTiff.computeAverageOnTiff(loadRGBTiff, imageWidth, imageHeight, numberOfFrames);
                RGBTiff.writeTiffFile(finalImage, imageWidth, imageHeight, imageReader, fileName);
            }
        } catch (Exception e) {
            System.out.println("Something goes wrong!");
        } finally {
            System.out.println("Shutting down...");
            System.exit(-1);
        }

    }

}
