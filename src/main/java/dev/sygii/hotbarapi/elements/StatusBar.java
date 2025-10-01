package dev.sygii.hotbarapi.elements;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class StatusBar extends HudElement implements Comparable<StatusBar> {
    private final Identifier id;
    /*private final Identifier texture;
    private final Position position;
    private final Direction direction;*/
    private StatusBarLogic logic;
    private StatusBarRenderer renderer;
    private final List<Identifier> beforeIds;
    private final List<Identifier> afterIds;
    private final EnumSet<GameMode> gameModes;
    private final List<StatusBarOverlay> overlays = new ArrayList<>();
    private final List<StatusBarOverlay> underlays = new ArrayList<>();
    private final Identifier toReplace;


    public StatusBar(Identifier id, StatusBarRenderer renderer, StatusBarLogic logic, List<Identifier> beforeIds, List<Identifier> afterIds, Identifier toReplace, EnumSet<GameMode> gameModes) {
        this.id = id;
        this.renderer = renderer;
        this.logic = logic;
        this.beforeIds = beforeIds;
        this.afterIds = afterIds;
        this.gameModes = gameModes;
        this.toReplace = toReplace;
    }

    public void addUnderlay(StatusBarOverlay underlay) {
        this.underlays.add(underlay);
    }

    public List<StatusBarOverlay> getUnderlays() {
        return underlays;
    }

    public void addOverlay(StatusBarOverlay overlay) {
        this.overlays.add(overlay);
    }

    public List<StatusBarOverlay> getOverlays() {
        return overlays;
    }

    public Identifier getReplacing() {
        return toReplace;
    }

    /*public StatusBar(Identifier id, Identifier texture, Position position, Direction direction, StatusBarLogic logic) {
        this(id, texture, position, direction, logic, false);
    }

    public StatusBar(Identifier id, Identifier texture, Position position, Direction direction, StatusBarLogic logic, boolean important) {
        this.id = id;
        this.texture = texture;
        this.position = position;
        this.direction = direction;
        this.logic = logic;
        this.important = important;
    }*/

    /*public void renderStatusBar(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int scaledWidth, int scaledHeight) {
        int s = (int) (scaledHeight - 39 - HotbarAPI.getHeightOffest(client, this, playerEntity));
        int xPos = position.equals(Position.LEFT) ? scaledWidth / 2 - 91 : scaledWidth / 2 + 91 - 9;

        render(client, context, playerEntity, xPos, s);
    }

    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int x, int y) {
        float current = logic.getValue(playerEntity);
        float max = logic.getMaxValue(playerEntity);
        int scale = 10;
        float apparition = max / scale;
        for(int w = 0; w < scale; ++w) {
            int xPosition = x + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + w * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(w * 8));
            context.drawTexture(getTexture(), xPosition, y, 0, 0, 9, 9, 27, 9);

            float prevasd = w * apparition;
            float asd = (w + 1) * apparition;
            float sex = (asd) - (apparition / 2);

            if (current > sex) {
                context.drawTexture(getTexture(), xPosition, y, 9, 0, 9, 9, 27, 9);
            }

            if (current > prevasd && current <= sex) {
                context.drawTexture(getTexture(), xPosition, y, 18, 0, 9, 9, 27, 9);
            }
        }
    }

    public float getHeight(MinecraftClient client, PlayerEntity playerEntity) {
        return 10;
    }*/

    public EnumSet<GameMode> getGameModes() {
        return gameModes;
    }

    public List<Identifier> getBeforeIds() {
        return beforeIds;
    }

    public List<Identifier> getAfterIds() {
        return afterIds;
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

    @Override
    public int compareTo(@NotNull StatusBar o) {
        System.out.println(this.getId() + "  |  " + this.getBeforeIds() + "  |  " + this.getAfterIds() + "  |  " + o.getId());
        if (this.getBeforeIds().contains(o.getId())) {
            return -1;
        }
        if (this.getAfterIds().contains(o.getId())) {
            return 1;
        }
        return 0;
    }

    /*public Identifier getTexture() {
        return texture;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public enum Position {
        LEFT,
        RIGHT;
    }

    public enum Direction {
        L2R,
        R2L;
    }*/
}
