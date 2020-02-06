package org.firstinspires.ftc.teamcode;

import android.content.Context;
import android.graphics.Bitmap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class VerboseGrayscaleImageScanner extends GrayscaleImageScanner {
    private final Telemetry telemetry;

    public VerboseGrayscaleImageScanner(Bitmap bitmap, int x, int y, int width, int height, Telemetry telemetry) {
        super(bitmap, x, y, width, height);
        this.telemetry = telemetry;
    }

    @Override
    public GrayscaleImageScanner getDarkPoints(int maxBrightness) {
        super.getDarkPoints(maxBrightness);
        log("Dark Point count: %d", points.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner getRectangles(int minDimension) {
        super.getRectangles(minDimension);
        log("Initial Rectangle size: %d", rectangles.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner removeSize(int minWidth, int minHeight, int maxWidth, int maxHeight) {
        int initial = rectangles.size();
        super.removeSize(minWidth, minHeight, maxWidth, maxHeight);
        log("Removed by removeSize: %d", initial - rectangles.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner removeMinPoints(int minPoints) {
        int initial = rectangles.size();
        super.removeMinPoints(minPoints);
        log("Removed by removeMinPoints: %d", initial - rectangles.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner removeContained() {
        int initial = rectangles.size();
        super.removeContained();
        log("Removed by removeContained: %d", initial - rectangles.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner removeMinConcentration(double minConcentration) {
        int initial = rectangles.size();
        super.removeMinConcentration(minConcentration);
        log("Removed by removeMinConcentration: %d", initial - rectangles.size());
        return this;
    }

    @Override
    public GrayscaleImageScanner saveWithRectangles(Context context, int rgb) {
        super.saveWithRectangles(context, rgb);
        log("Rectangles saved on image: %d", rectangles.size());
        return this;
    }

    private void log(String format, Object... args) {
        telemetry.log().add(format, args);
    }
}
