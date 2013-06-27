package com.mstsoft.frieda.server;

import com.mstsoft.frieda.server.animations.*;
import com.mstsoft.frieda.server.lightstrategies.*;
import com.mstsoft.frieda.server.lightstrategies.FadeStrategy;
import com.mstsoft.frieda.server.lightstrategies.BlinkStrategy;
import com.phidgets.LEDPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * User: martins
 * Date: 03.06.2008
 * Time: 17:41:13
 */
public class LightManager {

    private static Class[] strategyClasses = new Class[]{
            FadeStrategy.class,
            OnStrategy.class,
            OffStrategy.class,
            BlinkStrategy.class
    };

    private static Class[] animationClasses = new Class[] {
            AlertTopAnimation.class,
            HarlemShakeAnimation.class,
            IdentifyAnimation.class,
            KnightRiderAnimation.class,
            SnakeAnimation.class,
            SlotMachineAnimation.class,
            CircleAnimation.class,
            ColumnsCycleAnimation.class
    };

    private LEDPhidget led;
    private MappedLEDPhidget ledMapper;

    private List<LightAnimation> animations = new ArrayList<LightAnimation>();
    private List<LightStrategy> strategies = new ArrayList<LightStrategy>();

    private int[] currentStatus = new int[64];
    private LightStrategy[] currentStrategies = new LightStrategy[64];

    private LightAnimation currentAnimation = null;

    private int[] map = null;

    public LightManager() {

        for (Class c : strategyClasses) {
            try {
                strategies.add((LightStrategy) c.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (Class c : animationClasses) {
            try {
                animations.add((LightAnimation) c.newInstance());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        for (int i = 0; i < 64; i++) {
            currentStrategies[i] = lookupStrategy("off");
        }
        for (int i = 0; i < 64; i++) {
            currentStatus[i] = -1;
        }

        initPhidget();

        new Thread() {
            public void run() {
                while (true) {
                    try {
                        if (currentAnimation != null) {
                            LightAnimation local  = currentAnimation;
                            currentAnimation = null;
                            local.run(ledMapper);
                            for (int i = 0; i < 64; i++) {
                                currentStatus[i] = 0;
                            }
                        }
                        tick();
                        sleep(100);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }.start();

    }

    /**
     * Execute one tick
     */
    private void tick() throws PhidgetException {

        for(LightStrategy strategy : strategies) {
            strategy.tick();
        }

        // collect change set
        for (int i = 0; i < 64; i++) {
            int value = currentStrategies[i].val();
            if (value != currentStatus[i]) {
                currentStatus[i] = value;
                ledMapper.setBrightness(i, value);
            }
        }
    }

    private void initPhidget() {
        try {
            led = new LEDPhidget();
            led.addAttachListener(new AttachListener() {
                public void attached(AttachEvent ae) {
                    System.out.println("attachment of " + ae);
                }
            });
            led.addDetachListener(new DetachListener() {
                public void detached(DetachEvent ae) {
                    System.out.println("detachment of " + ae);
                }
            });
            led.addErrorListener(new ErrorListener() {
                public void error(ErrorEvent ee) {
                    System.out.println("error event for " + ee);
                }
            });

            led.openAny();
            System.out.println("waiting for LED attachment...");
            led.waitForAttachment();

            System.out.println("Serial: " + led.getSerialNumber());
            System.out.println("LEDs: " + led.getLEDCount());

            Properties props = new Properties();
            try {
                props.load(getClass().getClassLoader().getResourceAsStream("lightmap.properties"));
                map = new int[64];
                for (int i = 0; i < 64; i++) {
                    String val = props.getProperty("" + i);
                    if (val != null) {
                        map[i] = Integer.parseInt(val);
                    } else {
                        map[i] = i;
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            this.ledMapper = new MappedLEDPhidget(led, map);

        } catch (PhidgetException pe) {
            throw new RuntimeException(pe);
        }
    }

    public void setStrategy(int light, String name) {
       currentStrategies[light] = lookupStrategy(name);
    }

    public void setAnimation(String name) {
        currentAnimation = lookupAnimation(name);
    }

    private LightStrategy lookupStrategy(String name) {
        for (LightStrategy strategy : strategies) {
            if (strategy.getName().equals(name)) {
                return strategy;
            }
        }
        throw new RuntimeException("strategy not found: " + name);
    }

    private LightAnimation lookupAnimation(String name) {
        for (LightAnimation ani : animations) {
            if (ani.getName().equals(name)) {
                return ani;
            }
        }
        throw new RuntimeException("strategy not found: " + name);
    }

}