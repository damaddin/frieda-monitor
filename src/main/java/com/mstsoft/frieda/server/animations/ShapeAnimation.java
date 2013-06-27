package com.mstsoft.frieda.server.animations;

import com.mstsoft.frieda.server.LightAnimation;
import com.mstsoft.frieda.server.MappedLEDPhidget;

import java.awt.*;

/**
 * @author Mike
 */
public class ShapeAnimation implements LightAnimation {

    @Override
    public String getName() {
        return "shape";
    }

    @Override
    public void run(MappedLEDPhidget ledPhidget) {
        ledPhidget.clear();
        int[] xs = {0, 0, 2, 3, 5, 5, 3, 2};
        int[] ys = {9, 0, 3, 3, 0, 9, 6, 6};
        Shape shape = new Polygon(xs,ys,8);
        ledPhidget.getGraphics().draw(shape);
        ledPhidget.update();
    }
}
