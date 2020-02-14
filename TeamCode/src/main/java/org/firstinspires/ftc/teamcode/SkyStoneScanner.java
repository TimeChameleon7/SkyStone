package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("WeakerAccess")
public class SkyStoneScanner {
    private final Bitmap bitmap;
    public List<Integer> ys;

    public SkyStoneScanner(Bitmap bitmap, int x, int y, int width, int height) {
        this.bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    public SkyStoneScanner getLines(int maxBrightness, int length) {
        List<Point> heightDarkCount = new ArrayList<>(bitmap.getHeight());
        for (int y = 0; y < bitmap.getHeight(); y++) {
            int count = 0;
            for (int x = 0; x < bitmap.getWidth(); x++) {
                if (Color.red(bitmap.getPixel(x, y)) < maxBrightness)
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

    public SkyStoneScanner saveWithLines(Context context, int rgb) {
        Bitmap bitmap = Bitmap.createBitmap(this.bitmap);
        for (int y : ys) {
            for (int x = 0; x < bitmap.getWidth(); x++) {
                bitmap.setPixel(x, y, rgb);
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Skystone Image", "");
        return this;
    }

    public SkyStoneScanner save(Context context) {
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Skystone Image", "");
        return this;
    }
}
