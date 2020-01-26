package team.gutterteam123.ledanimation.handlers;

import io.github.splotycode.mosaik.util.ThreadUtil;
import io.github.splotycode.mosaik.webapi.handler.anotation.check.Mapping;
import io.github.splotycode.mosaik.webapi.handler.anotation.check.NeedPermission;

@NeedPermission
public class ToolHandler {

    @Mapping("tool/lag")
    public void createLag() {
        ThreadUtil.sleep(5000);
    }

}
