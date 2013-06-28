package com.mstsoft.frieda.server.animations;

import com.mstsoft.frieda.server.LightAnimation;
import com.mstsoft.frieda.server.MappedLEDPhidget;

/**
 * @author Mike
 */
public class TextAnimation implements LightAnimation {

    private final static int CHAR_WIDTH = 8;

    @Override
    public String getName() {
        return "text";
    }

    @Override
    public void run(MappedLEDPhidget ledPhidget) {
        // TODO Coonect to REST API
        String text = "HOLIDAYINSIDER";
        for (int i = CHAR_WIDTH / 2; i > -CHAR_WIDTH * text.length() + CHAR_WIDTH * 2; i--) {
            ledPhidget.clear();
            ledPhidget.getGraphics().drawString(text, i, 10);
            ledPhidget.update();
            try {
                Thread.currentThread().sleep(50);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        ledPhidget.clear();
    }
}
