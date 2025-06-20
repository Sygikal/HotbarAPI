package dev.sygii.hotbarapi.elements;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//? if >1.21.1 {
/*import net.minecraft.client.render.RenderLayer;
import java.util.function.Function;
*///?}

//? if >=1.21.6 {
/*import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.gl.RenderPipelines;
*///?}

public class StatusBarRenderer {
    private final Identifier id;
    private Identifier texture;
    private Position position;
    private Direction direction;

    public StatusBarRenderer(Identifier id, Identifier texture, Position position, Direction direction) {
        this.id = id;
        this.texture = texture;
        this.position = position;
        this.direction = direction;
    }

    public StatusBarRenderer update(Identifier texture, Position position, Direction direction) {
        this.setPosition(position);
        this.setDirection(direction);
        this.setTexture(texture);
        return this;
    }

    private void setTexture(Identifier texture) {  this.texture = texture; }

    private void setPosition(Position position) {
        this.position = position;
    }

    private void setDirection(Direction direction) {
        this.direction = direction;
    }

    /*public void renderStatusBar(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int scaledWidth, int scaledHeight) {
        int s = (int) (scaledHeight - 39 - HotbarAPI.getHeightOffest(client, this, playerEntity));
        int xPos = position.equals(Position.LEFT) ? scaledWidth / 2 - 91 : scaledWidth / 2 + 91 - 9;

        render(client, context, playerEntity, xPos, s);
    }*/

    //? if >=1.21.6 {
    /*RenderPipeline LAYER = RenderPipelines.GUI_TEXTURED;
    *///?} else if >=1.21.1 {
    /*Function<Identifier, RenderLayer> LAYER = RenderLayer::getGuiTexturedOverlay; // getGuiTexturedOverlay
     *///?}

    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int x, int y, StatusBarLogic logic) {
        float current = logic.getValue(playerEntity);
        float max = logic.getMaxValue(playerEntity);
        int scale = 10;
        float apparition = max / scale;
        for(int w = 0; w < scale; ++w) {
            int xPosition = x + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + w * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -(w * 8));
            //? if =1.20.1 {
            context.drawTexture(getTexture(), xPosition, y, 0, 0, 9, 9, 27, 9);
            //?} else {
            /*context.drawTexture(LAYER, getTexture(), xPosition, y, 0, 0, 9, 9, 27, 9);
            *///?}
            float prevasd = w * apparition;
            float asd = (w + 1) * apparition;
            float sex = (asd) - (apparition / 2);

            if (current > sex) {
                //? if =1.20.1 {
                context.drawTexture(getTexture(), xPosition, y, 9, 0, 9, 9, 27, 9);
                 //?} else {
                /*context.drawTexture(LAYER, getTexture(), xPosition, y, 9, 0, 9, 9, 27, 9);
                *///?}
            }

            if (current > prevasd && current <= sex) {
                //? if =1.20.1 {
                context.drawTexture(getTexture(), xPosition, y, 18, 0, 9, 9, 27, 9);
                 //?} else {
                /*context.drawTexture(LAYER, getTexture(), xPosition, y, 18, 0, 9, 9, 27, 9);
                *///?}
            }
        }
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

    public enum Position {
        LEFT,
        RIGHT;
    }

    public enum Direction {
        L2R,
        R2L;
    }
}
