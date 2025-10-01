package dev.sygii.hotbarapi.elements;

import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class StatusBarOverlay extends HudElement {
    private final Identifier id;
    private final Identifier targetId;
    private final Identifier texture;
    private StatusBarLogic logic;
    private StatusBarRenderer renderer;
    private final boolean underlay;

    public StatusBarOverlay(Identifier id, Identifier targetId, Identifier texture, StatusBarRenderer renderer, StatusBarLogic logic, boolean underlay) {
        this.id = id;
        this.targetId = targetId;
        this.renderer = renderer;
        this.texture = texture;
        this.logic = logic;
        this.underlay = underlay;
    }

    public void update(StatusBar targetBar) {
       renderer.update(texture, targetBar.getRenderer().getPosition(), targetBar.getRenderer().getDirection());
    }

    public Identifier getTarget() {
        return this.targetId;
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
