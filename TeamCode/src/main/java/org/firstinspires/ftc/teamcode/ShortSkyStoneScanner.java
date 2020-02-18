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

public class ShortSkyStoneScanner {
    private final int width;
    private final int height;
    private final short[][] pixels;
    public List<Integer> ys;

    public ShortSkyStoneScanner(Image image, int x, int y, int width, int height) {
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
        final int nWidth = width - x;
        for (int iY = 0; iY < height; iY++) {
            System.arraycopy(pixels[y + iY], x, this.pixels[y + iY], x, nWidth);
        }
    }

    public ShortSkyStoneScanner getLines(int maxBrightness, int length) {
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

    public ShortSkyStoneScanner saveWithLines(Context context, int rgb) {
        Bitmap bitmap = createBitmap();
        for (int y : ys) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                bitmap.setPixel(x, y, rgb);
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Skystone Image", "");
        return this;
    }

    public ShortSkyStoneScanner save(Context context) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), createBitmap(), "Skystone Image", "");
        return this;
    }

    private Bitmap createBitmap() {
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
