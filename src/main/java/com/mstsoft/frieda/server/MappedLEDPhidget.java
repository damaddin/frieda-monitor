package com.mstsoft.frieda.server;

import com.phidgets.LEDPhidget;
import com.phidgets.PhidgetException;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Allows for mapping where its physically not possible to connect all leds in series.
 * In my case i snapped some cables too short, so had to resolve this in software ;-)
 * <p/>
 * User: mst
 * Date: 24.06.13
 */
public class MappedLEDPhidget {

    public final static int ROWS = 10;
    public final static int COLUMNS = 6;
    private int[] map;
    private int[][] matrix;
    private LEDPhidget ledPhidget;
    private BufferedImage deviceImage;
    private Graphics2D graphics;

    public MappedLEDPhidget(
            LEDPhidget ledPhidget, int[] map
    ) {
        this.ledPhidget = ledPhidget;
        this.map = map;
        this.matrix = new int[COLUMNS][ROWS];
        for (int columnBlock = 0; columnBlock < ROWS * COLUMNS; columnBlock += ROWS * 2) {
            // GREEN
            for (int i = 0; i < ROWS * 2; i += 2) {
                this.matrix[columnBlock / ROWS][i / 2] = this.map[columnBlock + i];
            }
            // RED
            for (int i = 1; i < ROWS * 2 + 1; i += 2) {
                this.matrix[columnBlock / ROWS + 1][(i - 1) / 2] = this.map[columnBlock + i];
            }
        }
        this.deviceImage = new BufferedImage(COLUMNS, ROWS, BufferedImage.TYPE_INT_ARGB);
        this.graphics = this.deviceImage.createGraphics();
    }


    public int getDiscreteLED(int i) {
        try {
            return ledPhidget.getDiscreteLED(map[i]);
        } catch (PhidgetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDiscreteLED(int i, int i1) {
        try {
            ledPhidget.setDiscreteLED(map[i], i1);
        } catch (PhidgetException e) {
            throw new RuntimeException(e);
        }
    }

    public double getBrightness(int i) {
        try {
            return ledPhidget.getBrightness(map[i]);
        } catch (PhidgetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBrightness(int i, double v) {
        try {
            ledPhidget.setBrightness(map[i], v);
        } catch (PhidgetException e) {
            throw new RuntimeException(e);
        }
    }

    public void setBrightness(int x, int y, double v) {
        try {
            ledPhidget.setBrightness(this.matrix[x][y], v);
        } catch (PhidgetException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                deviceImage.setRGB(x, y, 0x00000000);
                update(x, y);
            }
        }
    }

    public void update() {
        for (int x = 0; x < COLUMNS; x++) {
            for (int y = 0; y < ROWS; y++) {
                update(x, y);
            }
        }
    }

    public void update(int x, int y) {
        int rgb = this.deviceImage.getRGB(x, y);
        int alpha = (rgb >> 24) & 0xff;
        if (alpha > 100) {
            alpha = 100;
        }
        setBrightness(x, y, alpha);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }
}
