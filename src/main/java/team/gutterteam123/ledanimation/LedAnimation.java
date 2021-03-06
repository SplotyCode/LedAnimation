package team.gutterteam123.ledanimation;

import io.github.splotycode.mosaik.runtime.LinkBase;
import io.github.splotycode.mosaik.runtime.application.Application;
import io.github.splotycode.mosaik.runtime.pathmanager.PathManager;
import io.github.splotycode.mosaik.runtime.startup.BootContext;
import io.github.splotycode.mosaik.util.NetworkUtil;
import io.github.splotycode.mosaik.util.logger.Logger;
import io.github.splotycode.mosaik.valuetransformer.TransformerManager;
import io.github.splotycode.mosaik.webapi.WebApplicationType;
import io.github.splotycode.mosaik.webapi.config.WebConfig;
import io.github.splotycode.mosaik.webapi.request.Request;
import io.github.splotycode.mosaik.webapi.response.URLEncode;
import io.github.splotycode.mosaik.webapi.server.netty.NettyWebServer;
import io.github.splotycode.mosaik.webapi.session.Evaluators;
import io.github.splotycode.mosaik.webapi.session.SessionSystem;
import io.github.splotycode.mosaik.webapi.session.impl.CookieUUIDSessionMatcher;
import io.github.splotycode.mosaik.webapi.session.impl.StaticSessionSystem;
import lombok.Getter;
import team.gutterteam123.ledanimation.devices.AllGroup;
import team.gutterteam123.ledanimation.devices.Controllable;
import team.gutterteam123.ledanimation.handlers.ErrorFactory;
import team.gutterteam123.ledanimation.handlers.RoutingHandler;
import team.gutterteam123.ledanimation.server.ChannelInitializer;
import team.gutterteam123.ledanimation.sound.SoundToLight;
import team.gutterteam123.ledanimation.user.Account;
import team.gutterteam123.ledanimation.user.LedSession;
import team.gutterteam123.ledanimation.user.PasswordCryptor;

import java.io.File;

@Getter
public class LedAnimation extends Application implements WebApplicationType {

    private static final Logger LOGGER = Logger.getInstance(LedAnimation.class);

    public static final File WEB_PATH;

    static {
        File path = new File("web");
        try {
            if (path.exists()) {
                LOGGER.info("Using Debug Web Resources from " + path.getAbsolutePath());
            } else if (!(path = new File(PathManager.getInstance().getMainDirectory(), "web/")).exists()) {
                throw new IllegalStateException("Could not get find web assets! Tried web/ and " + path.getAbsolutePath());
            }
        } finally {
            WEB_PATH = path;
        }
    }

    public static LedAnimation getInstance() {
        return LinkBase.getApplicationManager().getApplicationByClass(LedAnimation.class);
    }

    private RoutingHandler routingHandler = new RoutingHandler();
    private SessionSystem sessionSystem = new StaticSessionSystem(
            request -> new LedSession(),
            new CookieUUIDSessionMatcher("session", false),
            Evaluators.getTimeEvaluator(1000 * 60 * 60 * 2) //2 hour
    ) {
        @Override
        public boolean hasPermission(Request request, String permission) {
            if (NetworkUtil.isLocalAddress(request.getIpAddress())) {
                return true;
            }
            boolean result = super.hasPermission(request, permission);
            if (!result) {
                request.getResponse().redirect("/login?last=" + URLEncode.encode(request.getFullUrl()), false);
            }
            return result;
        }
    };

    @Override
    public void start(BootContext bootContext) throws Exception {
        LinkBase.getInstance().getLink(TransformerManager.LINK).registerPackage("team.gutterteam123.ledanimation.transformer");

        configureWebServer();
        listen(5555, false);
        //new SoundToLight(16);
    }

    private void configureWebServer() {
        NettyWebServer webServer = new NettyWebServer(this);
        ChannelInitializer initializer = new ChannelInitializer(webServer);

        setWebServer(webServer);
        webServer.installErrorFactory(new ErrorFactory());
        webServer.getSessionLoader().register(sessionSystem);
        webServer.setChannelInitializer(initializer);

        putConfig(WebConfig.NETTY_THREADS, 1);
        //putConfig(WebConfig.IGNORE_NO_SSL_RECORD, true);
        //putConfig(WebConfig.FORCE_HTTPS, false);
        routingHandler.setUp();

        if (Controllable.FILE_SYSTEM.getEntry("All") == null) {
            Controllable.FILE_SYSTEM.putEntry("All", new AllGroup());
        }
        if (Account.FILE_SYSTEM.getEntry("root") == null) {
            Account root = new Account("root", true);
            PasswordCryptor.setPassword(root, "root");
            Account.FILE_SYSTEM.putEntry("root", root);
        }
    }

    @Override
    public String getName() {
        return "Led Animation";
    }
}
