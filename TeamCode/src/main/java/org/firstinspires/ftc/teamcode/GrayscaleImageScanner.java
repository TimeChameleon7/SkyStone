package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.provider.MediaStore;

import org.firstinspires.ftc.robotcore.external.Predicate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class GrayscaleImageScanner {
    private final Bitmap bitmap;
    protected List<Point> points;
    protected List<Rectangle> rectangles;

    public GrayscaleImageScanner(Bitmap bitmap, int x, int y, int width, int height) {
        this.bitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
    }

    /**
     * Returns an array of points on the image that are darker than the specified maxBrightness.
     *
     * @param maxBrightness No point returned will have a corresponding pixel brighter than this.
     */
    public GrayscaleImageScanner getDarkPoints(int maxBrightness) {
        final int width = bitmap.getWidth();
        final int height = bitmap.getHeight();
        points = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (getBrightness(x, y) <= maxBrightness) {
                    points.add(new Point(x, y));
                }
            }
        }
        return this;
    }

    public GrayscaleImageScanner getRectangles(int minDimension) {
        if (points == null) throw new UnsupportedOperationException("getDarkPoints must be run before getRectangles");
        rectangles = new ArrayList<>();
        for (Point point : points) {
            Rectangle rectangle = findContaining(point);
            Rectangle fit = new Rectangle(point.x - minDimension, point.y - minDimension, 2 * minDimension, 2 * minDimension);
            if (rectangle == null) {
                rectangles.add(fit);
                Collections.sort(rectangles, new Comparator<Rectangle>() {
                    @Override
                    public int compare(Rectangle r1, Rectangle r2) {
                        return r1.width * r1.height - r2.width * r2.height;
                    }
                });
            } else {
                int x = min(rectangle.x, fit.x);
                int y = min(rectangle.y, fit.y);
                int x2 = max(rectangle.getMaxX(), fit.getMaxX());
                int y2 = max(rectangle.getMaxY(), fit.getMaxY());
                rectangle.setBounds(x, y, x2 - x, y2 - y);
            }
        }
        return this;
    }

    /**
     * Removes rectangles that do not fit within the specified bounds.
     *
     */
    public GrayscaleImageScanner removeSize(final int minWidth, final int minHeight, final int maxWidth, final int maxHeight) {
        return removeIf(new Predicate<Rectangle>() {
            @Override
            public boolean test(Rectangle rectangle) {
                return rectangle.width < minWidth ||
                        rectangle.height < minHeight ||
                        rectangle.width > maxWidth ||
                        rectangle.height > maxHeight;
            }
        });
    }

    public GrayscaleImageScanner removeMinPoints(final int minPoints) {
        return removeIf(new Predicate<Rectangle>() {
            @Override
            public boolean test(Rectangle rectangle) {
                int count = 0;
                for (Point point : points) {
                    if (rectangle.contains(point)) {
                        count++;
                        if (count == minPoints) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    /**
     * Removes rectangles that are contained within another rectangle in the list.
     */
    public GrayscaleImageScanner removeContained() {
        //rectangle names within the loops are as if the conditions are met for removal
        return removeIf(new Predicate<Rectangle>() {
            @Override
            public boolean test(Rectangle smaller) {
                for (Rectangle bigger : rectangles) {
                    if (bigger.contains(smaller) && !bigger.equals(smaller))
                        return true;
                }
                return false;
            }
        });
    }

    /**
     * Removes rectangles that contain so many points as to exceed the specified max concentration.
     * Concentration is calculated by {@code pointCount / rectangle.width * rectangle.height}
     *
     * @param maxConcentration Rectangles over this threshold will be removed
     */
    public GrayscaleImageScanner removeMaxConcentration(final double maxConcentration) {
        return removeIf(new Predicate<Rectangle>() {
            @Override
            public boolean test(Rectangle rectangle) {
                int count = 0;
                for (Point point : points) {
                    if (rectangle.contains(point))
                        count++;
                }
                double size = rectangle.width * rectangle.height;
                return count / size > maxConcentration;
            }
        });
    }

    public GrayscaleImageScanner saveWithRectangles(Context context, int rgb) {
        Bitmap bitmap = Bitmap.createBitmap(this.bitmap);
        for (Rectangle rectangle : rectangles) {
            final int
                minX = Math.max(rectangle.x, 0),
                minY = Math.max(rectangle.y, 0),
                maxX = Math.min(rectangle.getMaxX(), bitmap.getWidth()),
                maxY = Math.min(rectangle.getMaxY(), bitmap.getHeight());
            for (int x = minX; x < maxX; x++) {
                bitmap.setPixel(x, minY - 1, rgb);
                bitmap.setPixel(x, maxY - 1, rgb);
            }
            for (int y = minY; y < maxY; y++) {
                bitmap.setPixel(minX - 1, y, rgb);
                bitmap.setPixel(maxX - 1, y, rgb);
            }
        }
        MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Skystone Image", "");
        return this;
    }

    private GrayscaleImageScanner removeIf(Predicate<Rectangle> predicate) {
        if (rectangles == null) throw new UnsupportedOperationException("getRectangles must be run before any of the remove methods.");
        List<Rectangle> remove = new ArrayList<>();
        for (Rectangle rectangle : rectangles) {
            if (predicate.test(rectangle))
                remove.add(rectangle);
        }
        rectangles.removeAll(remove);
        return this;
    }

    private Rectangle findContaining(Point point) {
        for (Rectangle rectangle : rectangles) {
            if (rectangle.contains(point)) return rectangle;
        }
        return null;
    }

    private int getBrightness(int x, int y) {
        //the image is grayscale, so we can just get any of the colors.
        return Color.red(bitmap.getPixel(x, y));
    }
}
