package team.gutterteam123.ledanimation.animation.keyframes;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import team.gutterteam123.ledanimation.animation.ExecutionContext;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@EqualsAndHashCode
public abstract class KeyFrame implements Serializable {

    private static final long serialVersionUID = 1L;

    protected short start, end;
    protected int layer;
    protected String name;

    public KeyFrame(String name, short start, short end, int layer) {
        this.name = name;
        this.start = start;
        this.end = end;
        this.layer = layer;
    }

    public abstract void executeAction(ExecutionContext context);

    public abstract void step(ExecutionContext context);

}
