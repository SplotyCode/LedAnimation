package team.gutterteam123.ledanimation.handlers;

import io.github.splotycode.mosaik.webapi.handler.anotation.check.Mapping;
import io.github.splotycode.mosaik.webapi.handler.anotation.check.NeedPermission;
import team.gutterteam123.ledanimation.LedAnimation;
import team.gutterteam123.ledanimation.devices.LedHandler;

@NeedPermission
public class MiscellaneousHandler {

    @Mapping("miscellaneous/restart")
    public void restart() {
        LedAnimation.getInstance().getWebServer().shutdown(() -> System.exit(101));
    }

    @Mapping("miscellaneous/ping")
    public void ping() {}

    @Mapping("miscellaneous/restartOla")
    public void restartOla() {
        LedHandler.getInstance().reConnect(true);
    }

}
