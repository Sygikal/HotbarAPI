package dev.sygii.hotbarapi.elements;

import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatusBarOverlay extends HudElement {
    private final Identifier id;
    private final Identifier targetBar;
    private StatusBarLogic logic;
    private StatusBarRenderer renderer;
    private final boolean underlay;

    public StatusBarOverlay(Identifier id, Identifier targetBar, StatusBarRenderer renderer, StatusBarLogic logic, boolean underlay) {
        this.id = id;
        this.targetBar = targetBar;
        this.renderer = renderer;
        this.logic = logic;
        this.underlay = underlay;
    }

    public Identifier getTarget() {
        return this.targetBar;
    }

    public boolean isUnderlay() {
        return this.underlay;
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
