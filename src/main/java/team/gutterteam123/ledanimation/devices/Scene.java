package team.gutterteam123.ledanimation.devices;

import io.github.splotycode.mosaik.domparsing.annotation.FileSystem;
import io.github.splotycode.mosaik.domparsing.annotation.parsing.SerialisedEntryParser;
import io.github.splotycode.mosaik.runtime.LinkBase;
import io.github.splotycode.mosaik.runtime.Links;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

@Getter
public class Scene implements Serializable  {

    private static final long serialVersionUID = 1L;

    public static final FileSystem<Scene> FILE_SYSTEM = LinkBase.getInstance().getLink(Links.PARSING_FILEPROVIDER).provide("scenes", new SerialisedEntryParser());

    private String name;
    private Map<String, Map<ChannelType, Short>> values;

    public Scene(String name, Map<String, Map<ChannelType, Short>> values) {
        this.name = name;
        this.values = values;
    }

    @Getter @Setter private boolean visible;

    public void load() {
        for (Map.Entry<String, Map<ChannelType, Short>> device : values.entrySet()) {
            Controllable dev = Controllable.FILE_SYSTEM.getEntry(device.getKey());
            for (Map.Entry<ChannelType, Short> channel : device.getValue().entrySet()) {
                dev.setChannel(null, channel.getKey(), channel.getValue());
            }
        }
    }


}
