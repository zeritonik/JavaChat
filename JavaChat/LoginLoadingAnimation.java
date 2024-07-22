package JavaChat;

import javafx.animation.AnimationTimer;
import javafx.scene.shape.Arc;

public class LoginLoadingAnimation extends AnimationTimer {
    Arc arc;
    long ts;


    public LoginLoadingAnimation(Arc arc) {
        this.arc = arc;
    }

    @Override
    public void start() {
        ts = -1;
        arc.setVisible(true);
        super.start();
    }
    public void stop() {
        arc.setVisible(false);
        super.stop();
    }

    @Override
    public void handle(long t) {
        if (ts == -1)
            ts = t;
        double time = (t - ts) / 1e9 % 1;
        arc.setRotate(time / 0.5 * 360 + 90);
        arc.setLength(time * 360);
    }
}
