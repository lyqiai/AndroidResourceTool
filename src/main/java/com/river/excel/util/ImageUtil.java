package com.river.excel.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.File;
import java.io.IOException;

public class ImageUtil {
    public static boolean compareImage(File source, File target) {
        try {
            BufferedImage sourceBufferedImage = ImageIO.read(source);
            DataBuffer sourceDataBuffer = sourceBufferedImage.getData().getDataBuffer();
            int sourceDataBufferSize = sourceDataBuffer.getSize();

            BufferedImage targetBufferedImage = ImageIO.read(target);
            DataBuffer targetDataBuffer = targetBufferedImage.getData().getDataBuffer();
            int targetDataBufferSize = targetDataBuffer.getSize();

            if (sourceDataBufferSize == targetDataBufferSize) {
                for (int i = 0; i < sourceDataBufferSize; i++) {
                    if (sourceDataBuffer.getElem(i) != targetDataBuffer.getElem(i)) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
