package com.nanolyze.tiff.service;

import mil.nga.tiff.*;
import mil.nga.tiff.util.TiffConstants;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
public class GrayScaleTiff {
    public static TIFFImage readTiffFile(String fileName) {
        TIFFImage tiffImage = null;
        try {
            File input = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + fileName);
            tiffImage = TiffReader.readTiff(input);
        } catch (FileNotFoundException e) {
            System.out.println("There is NO file with the name : " + fileName);
        } catch (IOException e) {
            System.out.println("Some problem happened in reading the file!");
        }
        return tiffImage;
    }
    public static short[][][] loadGrayScaleTiff(TIFFImage tiffImage, int imageWidth, int imageHeight, int numberOfFrames) {
        List<FileDirectory> directories = tiffImage.getFileDirectories();
        System.out.println("Reading file...");
        short[][][] loadedImage = new short[imageWidth][imageHeight][numberOfFrames];
        for (int frame = 0; frame < numberOfFrames; frame++) {
            FileDirectory directory = directories.get(frame);
            Rasters raster = directory.readRasters();
            for (int col = 0; col < imageWidth; col++) {
                for (int row = 0; row < imageHeight; row++) {
                    loadedImage[col][row][frame] = raster.getFirstPixelSample(col, row).shortValue();
                }
            }
            System.out.println("frame = " + frame + " loaded.");
        }
        System.out.println("File has been loaded.");
        return loadedImage;
    }
    public static short[][] computeAverageOnTiff(short[][][] loadedTiff, int imageWidth, int imageHeight, int numberOfFrames) {
        System.out.println("Getting average...");
        short[][] finalImage = new short[imageWidth][imageHeight];
        for (int i = 0; i < imageWidth; i++) {
            for (int j = 0; j < imageHeight; j++) {
                long pixel = 0L;
                for (int f = 0; f < numberOfFrames; f++) {
                    pixel += loadedTiff[i][j][f];
                }
                finalImage[i][j] = ((Long) (pixel / numberOfFrames)).shortValue();
            }
        }
        System.out.println("Average has been calculated.");
        return finalImage;

    }
    public static void writeTiffFile(short[][] finalImage, int imageWidth, int imageHeight, int samplePerPixel, String fileName) {
        FieldType fieldType = FieldType.FLOAT;
        int bitsPerSample = fieldType.getBits();
        Rasters raster = new Rasters(Math.toIntExact(imageWidth), Math.toIntExact(imageHeight), samplePerPixel, fieldType);
        int rowsPerStrip = raster.calculateRowsPerStrip(TiffConstants.PLANAR_CONFIGURATION_PLANAR);
        FileDirectory directory = new FileDirectory();
        directory.setImageWidth(imageWidth);
        directory.setImageHeight(imageHeight);
        directory.setBitsPerSample(bitsPerSample);
        directory.setCompression(TiffConstants.COMPRESSION_NO);
        directory.setPhotometricInterpretation(TiffConstants.PHOTOMETRIC_INTERPRETATION_WHITE_IS_ZERO);
        directory.setSamplesPerPixel(samplePerPixel);
        directory.setRowsPerStrip(rowsPerStrip);
        directory.setPlanarConfiguration(TiffConstants.PLANAR_CONFIGURATION_CHUNKY);
        directory.setSampleFormat(TiffConstants.SAMPLE_FORMAT_SIGNED_INT);
        directory.setWriteRasters(raster);
        System.out.println("Making Output File...");
        for (int y = 0; y < imageHeight; y++) {
            for (int x = 0; x < imageWidth; x++) {
                Short pixelValue = finalImage[x][y]; // any pixel value
                raster.setFirstPixelSample(x, y, pixelValue);
            }
        }
        TIFFImage newTiffImage = new TIFFImage();
        newTiffImage.add(directory);
        File newFile = new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "avg-" + fileName);
        try {
            TiffWriter.writeTiff(newFile, newTiffImage);
        } catch (IOException e) {
            //TODO extract and make meaningful exceptions
            System.out.println("Some problem happened in saving the file!");
        }
        System.out.println("Output file has been saved.");
    }
}
