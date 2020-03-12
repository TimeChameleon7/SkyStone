package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.MediaStore;

import com.vuforia.Image;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SkyStoneScanner {
    final int width;
    final int height;
    private final short[][] pixels;
    public List<Integer> ys;

    public SkyStoneScanner(Image image, int x, int y, int width, int height) {
        ByteBuffer buffer = image.getPixels();
        short[][] pixels = new short[image.getHeight()][image.getWidth()];
        for (int iY = 0; iY < image.getHeight(); iY++) {
            for (int iX = 0; iX < image.getWidth(); iX++) {
                pixels[iY][iX] = (short) (buffer.get() & 0xff);
            }
        }

        this.height = height;
        this.width = width;
        this.pixels = new short[height][width];
        for (int iY = 0; iY < height; iY++) {
            System.arraycopy(pixels[y + iY], x, this.pixels[iY], 0, width);
        }
    }

    public SkyStoneScanner getLines(int maxBrightness, int length) {
        List<Point> heightDarkCount = new ArrayList<>(height);
        for (int y = 0; y < height; y++) {
            int count = 0;
            for (int x = 0; x < width; x++) {
                if (pixels[y][x] < maxBrightness)
                    count++;
            }
            //just using Point as it's convenient, just need integers paired up
            //noinspection SuspiciousNameCombination
            heightDarkCount.add(new Point(y, count));
        }
        Collections.sort(heightDarkCount, new Comparator<Point>() {
            @Override
            public int compare(Point p1, Point p2) {
                return p2.y - p1.y;
            }
        });
        ys = new ArrayList<>();
        for (int i = 0; i < length; i++)
            ys.add(heightDarkCount.get(i).x);
        return this;
    }

    public boolean fitsBetween(int minY, int maxY, int allowedExceptions) {
        int outliers = 0;
        for (int y : ys) {
            if (y < minY || y > maxY) {
                outliers++;
                if (outliers > allowedExceptions)
                    return false;
            }
        }
        return true;
    }

    public int getAvgBrightness(int minX, int minY, int maxX, int maxY) {
        int avg = 0;
        int count = 0;
        for (int y = minY; y < maxY; y++) {
            for (int x = minX; x < maxX; x++) {
                avg += pixels[y][x];
                count++;
            }
        }
        return avg / count;
    }

    public SkyStoneScanner saveOnlyRectangles(Context context, int... numbers) {
        if (numbers.length % 4 != 0) throw new IllegalArgumentException("numbers length must be a multiple of 4");
        short[][] pixels = new short[height][width];
        //only copy what's in numbers[]
        for (int i = 0; i < numbers.length / 4; i++) {
            for (int x = numbers[0]; x < numbers[2]; x++) {
                for (int y = numbers[1]; y < numbers[3]; y++) {
                    pixels[y][x] = this.pixels[y][x];
                }
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), createBitmap(pixels), "Skystone Image", "");
        return this;
    }

    public SkyStoneScanner saveWithLines(Context context, int rgb) {
        Bitmap bitmap = createBitmap(pixels);
        for (int y : ys) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                bitmap.setPixel(x, y, rgb);
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Skystone Image", "");
        return this;
    }

    public SkyStoneScanner save(Context context) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), createBitmap(pixels), "Skystone Image", "");
        return this;
    }

    private Bitmap createBitmap(short[][] pixels) {
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = pixels[y][x];
                bitmap.setPixel(x, y, Color.rgb(rgb, rgb, rgb));
            }
        }
        return bitmap;
    }
}
