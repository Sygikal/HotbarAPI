package dev.sygii.hotbarapi;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.net.IDN;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StatusBar extends HudElement {
    private final Identifier id;
    private final Identifier texture;
    private final Position position;
    private final Direction direction;
    private final boolean important;

    public StatusBar(Identifier id, Identifier texture, Position position, Direction direction) {
        this(id, texture, position, direction, false);
    }

    public StatusBar(Identifier id, Identifier texture, Position position, Direction direction, boolean important) {
        this.id = id;
        this.texture = texture;
        this.position = position;
        this.direction = direction;
        this.important = important;
    }

    public void renderStatusBar(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int scaledWidth, int scaledHeight) {
        int s = (int) (scaledHeight - 39 - HotbarAPI.getHeightOffest(client, this, playerEntity));
        int xPos = position.equals(Position.LEFT) ? scaledWidth / 2 - 91 : scaledWidth / 2 + 91 - 9;

        /*float health = playerEntity.getHealth();
        float maxHealth = playerEntity.getMaxHealth();
        int scale = 10;
        float apparition = maxHealth / scale;
        for(int w = 0; w < scale; ++w) {
            int xPosition = xPos + (direction.equals(Direction.L2R) ? (position.equals(Position.RIGHT) ? -72 : 0) + w * 8 : (position.equals(Position.LEFT) ? 72 : 0) + -(w * 8));
            context.drawTexture(getTexture(), xPosition, s, 0, 0, 9, 9, 27, 9);

            if (w * apparition + 1 < health) {
                context.drawTexture(getTexture(), xPosition, s, 9, 0, 9, 9, 27, 9);
            }

            //System.out.println(w * apparition + 1);

            if (w * apparition + 1 == health) {
                //context.drawTexture(getTexture(), xPosition, s, 18, 0, 9, 9, 27, 9);
            }
        }*/

        render(client, context, playerEntity, xPos, s);
    }

    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int x, int y) {
        float health = playerEntity.getHealth();
        float maxHealth = playerEntity.getMaxHealth();
        int scale = 10;
        float apparition = maxHealth / scale;
        for(int w = 0; w < scale; ++w) {
            int xPosition = x + (direction.equals(Direction.L2R) ? (position.equals(Position.RIGHT) ? -72 : 0) + w * 8 : (position.equals(Position.LEFT) ? 72 : 0) + -(w * 8));
            context.drawTexture(getTexture(), xPosition, y, 0, 0, 9, 9, 27, 9);

            if (w * apparition + 1 < health) {
                context.drawTexture(getTexture(), xPosition, y, 9, 0, 9, 9, 27, 9);
            }

            //System.out.println(w * apparition + 1);

            if (w * apparition + 1 == health) {
                context.drawTexture(getTexture(), xPosition, y, 18, 0, 9, 9, 27, 9);
            }
        }
    }

    public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
        return true;
    }

    public float getHeight(MinecraftClient client, PlayerEntity playerEntity) {
        return 10;
    }

    public Identifier getId() {
        return id;
    }

    public Identifier getTexture() {
        return texture;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isImportant() {
        return important;
    }

    public enum Position {
        LEFT,
        RIGHT;
    }

    public enum Direction {
        L2R,
        R2L;
    }
}
