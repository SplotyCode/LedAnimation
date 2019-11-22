package team.gutterteam123.ledanimation.animation;

import io.github.splotycode.mosaik.domparsing.annotation.FileSystem;
import io.github.splotycode.mosaik.domparsing.annotation.parsing.SerialisedEntryParser;
import io.github.splotycode.mosaik.runtime.LinkBase;
import io.github.splotycode.mosaik.runtime.Links;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import team.gutterteam123.ledanimation.animation.keyframes.KeyFrame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
/* max playtime  27.3058333333 mins */
/* (2^15-1) * (1/20) / 60 */
public class Animation implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final FileSystem<Animation> FILE_SYSTEM = LinkBase.getInstance().getLink(Links.PARSING_FILEPROVIDER).provide("animation", new SerialisedEntryParser());

    private int end;

    @Getter private String name;
    @Getter private ArrayList<KeyFrame> keyFrames = new ArrayList<>();

    public Animation(String name) {
        this.name = name;
    }

    public int getEnd() {
        if (end == 0 && !keyFrames.isEmpty()) {
            computeEnd();
        }
        return end;
    }

    public void computeEnd() {
        end = 0;
        for (KeyFrame frame : keyFrames) {
            end = Math.max(end, frame.getEnd());
        }
    }
}
