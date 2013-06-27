package com.mstsoft.frieda.server.animations;

import com.mstsoft.frieda.server.LightAnimation;
import com.mstsoft.frieda.server.MappedLEDPhidget;

/**
 * @author Mike
 */
public class CircleAnimation implements LightAnimation {

    @Override
    public String getName() {
        return "circle";
    }

    @Override
    public void run(MappedLEDPhidget ledPhidget) {
        ledPhidget.clear();
        for (int wink = 10; wink > 0; wink--) {
            for (int iteration = 0; iteration < 6; iteration++) {
                resizeStep(ledPhidget, iteration, wink*15);
            }
            for (int iteration = 5; iteration >= 0; iteration--) {
                resizeStep(ledPhidget, iteration, wink*10);
            }
        }
        ledPhidget.update();
        ledPhidget.clear();
    }

    private void resizeStep(MappedLEDPhidget ledPhidget, int iteration, int speed) {
        ledPhidget.clear();
        ledPhidget.getGraphics().drawOval(0, 5-iteration, 5, iteration*2);
        ledPhidget.update();
        try {
            Thread.currentThread().sleep(speed);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
