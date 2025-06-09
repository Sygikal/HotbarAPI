package dev.sygii.hotbarapi.elements;

import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatusBarOverlay extends HudElement {
    private final Identifier id;
    private StatusBarLogic logic;
    private StatusBarRenderer renderer;

    public StatusBarOverlay(Identifier id, StatusBarRenderer renderer, StatusBarLogic logic) {
        this.id = id;
        this.renderer = renderer;
        this.logic = logic;
    }

    public StatusBarRenderer getRenderer() {
        return renderer;
    }

    public StatusBarLogic getLogic() {
        return logic;
    }

    public void setLogic(StatusBarLogic logic) {
        this.logic = logic;
    }

    public Identifier getId() {
        return id;
    }
}
