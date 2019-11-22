package team.gutterteam123.ledanimation.animation.keyframes;

import io.github.splotycode.mosaik.util.datafactory.DataKey;
import lombok.Getter;
import team.gutterteam123.ledanimation.animation.ExecutionContext;
import team.gutterteam123.ledanimation.devices.ChannelType;
import team.gutterteam123.ledanimation.devices.Controllable;
import team.gutterteam123.ledanimation.devices.Scene;

import java.util.Map;

public class PlaySceneKeyFrame extends KeyFrame {


    private static final DataKey<Scene> START_SCENE = new DataKey<>("start_scene");
    private static final DataKey<Scene> END_SCENE = new DataKey<>("end_scene");

    @Getter private String scene;

    public PlaySceneKeyFrame(String name, short start, short end, int layer, String scene) {
        super(name, start, end, layer);
        this.scene = scene;
    }

    @Override
    public void executeAction(ExecutionContext context) {
        context.putData(START_SCENE, Scene.saveCurrent("Start"));
        context.putData(END_SCENE, Scene.FILE_SYSTEM.getEntry(scene));
    }

    @Override
    public void step(ExecutionContext context) {
        float percent = (context.position() - start) / (float) (end - start);
        System.out.println(percent);
        Map<String, Map<ChannelType, Short>> start = context.getData(START_SCENE).getValues();
        Map<String, Map<ChannelType, Short>> end = context.getData(END_SCENE).getValues();

        for (Map.Entry<String, Map<ChannelType, Short>> endEntry : end.entrySet()) {
            Map<ChannelType, Short> startValues = start.get(endEntry.getKey());
            Controllable controllable = Controllable.FILE_SYSTEM.getEntry(endEntry.getKey());
            if (controllable == null || startValues == null) {
                System.out.println("Animation: No device named " + endEntry.getValue());
                continue;
            }
            for (ChannelType type : endEntry.getValue().keySet()) {
                short startValue = startValues.getOrDefault(type, (short) 0);
                short endValue = endEntry.getValue().getOrDefault(type, (short) 0);
                float value = (endValue - startValue) * percent  + startValue;
                controllable.setChannel(null, type, (short) Math.round(value));
            }
        }

    }
}
