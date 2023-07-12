package ru.combuddy.backend.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageConverter {
    /**
     * @return null if can not convert
     */
    public static ImageData convertImage(byte[] inputImageBytes, String convertImageFormat) throws IOException {
        ByteArrayInputStream inputImageStream = new ByteArrayInputStream(inputImageBytes);
        BufferedImage inputImage = ImageIO.read(inputImageStream);
        if (inputImage == null) {
            return null;
        }
        return new ImageData(inputImage.getWidth(), inputImage.getHeight(), convert(inputImage, convertImageFormat));
    }

    /**
     * @return null if can not convert
     */
    public static byte[] resizeImage(byte[] inputImageBytes, int targetWidth, int targetHeight, String imageFormat) throws IOException {
        ByteArrayInputStream inputImageStream = new ByteArrayInputStream(inputImageBytes);
        BufferedImage inputImage = ImageIO.read(inputImageStream);
        Image resultingImage = inputImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return convert(outputImage, imageFormat);
    }

    /**
     * @return null if can not convert
     */
    private static byte[] convert(BufferedImage inputImage, String targetFormat) throws IOException {
        ByteArrayOutputStream convertedImageStream = new ByteArrayOutputStream();
        boolean result = ImageIO.write(inputImage, targetFormat, convertedImageStream);
        if (!result) {
            return null;
        }
        return convertedImageStream.toByteArray();

    }

    @Data
    @AllArgsConstructor
    public static class ImageData {
        private int width;
        private int height;
        private byte[] bytes;
    }
}
