package team.gutterteam123.ledanimation.animation;

import io.github.splotycode.mosaik.util.ThreadUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.gutterteam123.ledanimation.animation.keyframes.KeyFrame;
import team.gutterteam123.ledanimation.devices.Scene;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AnimationExecutor {

    @Getter private static AnimationExecutor instance = new AnimationExecutor();

    private ExecutorService executor = Executors.newFixedThreadPool(8);

    public void execute(Animation animation) {
        ExecutionContext context = new ExecutionContext(animation);
        executor.execute(() -> {
            context.start = Scene.saveCurrent("Start");
            for (short i = 0; i < context.getAnimation().getEnd(); i++) {
                long start = System.currentTimeMillis();
                int frameID = 0;
                for (KeyFrame frame : animation.getKeyFrames()) {
                    context.nextKeyFrame(++frameID);
                    if (frame.getStart() == i) {
                        frame.executeAction(context);
                        frame.step(context);
                    } else if (i > frame.getStart() && i < frame.getEnd()) {
                        frame.step(context);
                    }
                }
                context.next();
                long toSleep = 1000 / 20;
                long delay = System.currentTimeMillis() - start;
                ThreadUtil.sleep(Math.max(0, toSleep - delay));
            }
        });
    }

}
