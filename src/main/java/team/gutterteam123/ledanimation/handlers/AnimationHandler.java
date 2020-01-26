package team.gutterteam123.ledanimation.handlers;

import io.github.splotycode.mosaik.webapi.handler.anotation.check.Mapping;
import io.github.splotycode.mosaik.webapi.handler.anotation.check.NeedPermission;
import io.github.splotycode.mosaik.webapi.handler.anotation.handle.RequiredGet;
import io.github.splotycode.mosaik.webapi.request.Request;
import io.github.splotycode.mosaik.webapi.response.Response;
import io.github.splotycode.mosaik.webapi.response.content.ResponseContent;
import io.github.splotycode.mosaik.webapi.response.content.file.FileResponseContent;
import io.github.splotycode.mosaik.webapi.response.content.manipulate.ManipulateableContent;
import org.json.JSONArray;
import org.json.JSONObject;
import team.gutterteam123.ledanimation.LedAnimation;
import team.gutterteam123.ledanimation.animation.Animation;
import team.gutterteam123.ledanimation.animation.AnimationExecutor;
import team.gutterteam123.ledanimation.animation.keyframes.KeyFrame;
import team.gutterteam123.ledanimation.animation.keyframes.PlaySceneKeyFrame;
import team.gutterteam123.ledanimation.devices.Scene;

import java.io.File;

@NeedPermission
public class AnimationHandler {

    @Mapping("views/animation")
    public ResponseContent view() {
        FileResponseContent content = new FileResponseContent(new File(LedAnimation.WEB_PATH, "views/animation.html"));
        content.manipulate().patternList(Animation.FILE_SYSTEM.getEntries());
        return content;
    }


    @Mapping("animations/create")
    public void create(Response response, @RequiredGet("name") String name) {
        Animation.FILE_SYSTEM.putEntry(name, new Animation(name));
        response.redirect("/animations/edit?name=" + name, false);
    }

    @Mapping("animations/play")
    public void play(Response response, @RequiredGet("name") String name) {
        AnimationExecutor.getInstance().execute(Animation.FILE_SYSTEM.getEntry(name));
        response.redirect("/animation", false);
    }

    @Mapping("animations/edit")
    public ResponseContent edit(@RequiredGet("name") String name) {
        ManipulateableContent content = new FileResponseContent(new File(LedAnimation.WEB_PATH, "editanimation.html"));
        content.manipulate().variable("name", name);
        content.manipulate().patternListName("scenes", Scene.FILE_SYSTEM.getEntries());
        Animation animation = Animation.FILE_SYSTEM.getEntry(name);
        JSONArray animations = new JSONArray();
        for (KeyFrame frame : animation.getKeyFrames()) {
            JSONObject frameObject = new JSONObject();
            animations.put(frameObject);

            frameObject.put("name", frame.getName());
            frameObject.put("start", frame.getStart() / 20f);
            frameObject.put("end", frame.getEnd() / 20f);
            frameObject.put("layer", frame.getLayer());

            //TODO
            frameObject.put("effect", "PlayScene");
            frameObject.put("playSceneScene", ((PlaySceneKeyFrame) frame).getScene());
        }
        content.manipulate().variable("content", animations.toString());
        return content;
    }

    @Mapping("animations/save")
    public void save(Request request, @RequiredGet("name") String name) {
        Animation animation = Animation.FILE_SYSTEM.getEntry(name);
        animation.getKeyFrames().clear();

        JSONArray animations = new JSONArray(new String(request.getBody()));
        for (int i = 0; i < animations.length(); i++) {
            JSONObject animationObject = animations.getJSONObject(i);
            short start = (short) Math.round(animationObject.getFloat("start") * 20f);
            short end = (short) Math.round(animationObject.getFloat("end") * 20f);
            animation.getKeyFrames().add(new PlaySceneKeyFrame(
                    animationObject.getString("name"),
                    start, end, animationObject.getInt("layer"),
                    animationObject.getString("playSceneScene")
            ));
        }
        animation.computeEnd();
        Animation.FILE_SYSTEM.putEntry(name, animation);
    }

    @Mapping("animations/delete")
    public void delete(Response response, @RequiredGet("name") String name) {
        Animation.FILE_SYSTEM.deleteEntry(name);
        response.redirect("/animation", false);
    }
}