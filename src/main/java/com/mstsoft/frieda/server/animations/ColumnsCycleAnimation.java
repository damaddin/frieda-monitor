package com.mstsoft.frieda.server.animations;

import com.mstsoft.frieda.server.LightAnimation;
import com.mstsoft.frieda.server.MappedLEDPhidget;

/**
 * @author Mike
 */
public class ColumnsCycleAnimation implements LightAnimation {

    @Override
    public String getName() {
        return "columnsCycle";
    }

    @Override
    public void run(MappedLEDPhidget ledPhidget) {
        ledPhidget.clear();
        for (int iterations = 0; iterations < 4; iterations++) {
            for (int column = 0; column < MappedLEDPhidget.COLUMNS; column++) {
                int previousColumn = column - 1;
                if (previousColumn == -1) {
                    previousColumn = MappedLEDPhidget.COLUMNS - 1;
                } else if (previousColumn == MappedLEDPhidget.COLUMNS) {
                    previousColumn = 0;
                }
                ledPhidget.clear();
                ledPhidget.getGraphics().drawLine(column, 0, column, MappedLEDPhidget.ROWS - 1);
                ledPhidget.update();
                try {
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        ledPhidget.clear();
    }
}
