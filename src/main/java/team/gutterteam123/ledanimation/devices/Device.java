package team.gutterteam123.ledanimation.devices;

import io.github.splotycode.mosaik.util.prettyprint.PrettyPrint;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import team.gutterteam123.ledanimation.LedAnimation;
import team.gutterteam123.ledanimation.server.WebSocketHandler;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Device implements Controllable {

    private static final long serialVersionUID = 1L;

    private Map<ChannelType, Integer> channels = new HashMap<>();
    private String name;
    @Getter @Setter
    private boolean visible;
    @Setter
    private int priority;

    public Map<ChannelType, Integer> getChannelMap() {
        return channels;
    }

    public Device(String name) {
        this.name = name;
    }

    @Override
    public void setChannel(ChannelHandlerContext ctx, ChannelType channel, short value) {
        LedHandler.getInstance().setChannel(channels.get(channel), value, channel == ChannelType.BRIGHTNESS);
        WebSocketHandler.getInstance().updateValue(ctx, this, channel);
        if (ctx != null) {
            for (Controllable controllable : Controllable.FILE_SYSTEM.getEntries()) {
                if (controllable instanceof DeviceGroup) {
                    if (((DeviceGroup) controllable).getRawDevices().contains(name)) {
                        ChannelValue cValue = controllable.getValue(channel);
                        ctx.writeAndFlush(new TextWebSocketFrame(controllable.displayName() + ":" + channel.displayName() + ":" + cValue.isSave() + ":" + cValue.getValue()));
                    }
                }
            }
        }
    }

    @Override
    public Collection<ChannelType> getChannels() {
        return channels.keySet();
    }

    @Override
    public String displayName() {
        return name;
    }

    @Override
    public ChannelValue getValue(ChannelType type) {
        return new ChannelValue(true, LedHandler.getInstance().getRawValue(channels.get(type)));
    }

    @Override
    public int priority() {
        return priority;
    }
}
