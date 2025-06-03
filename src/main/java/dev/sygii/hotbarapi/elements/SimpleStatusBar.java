package dev.sygii.hotbarapi.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class SimpleStatusBar extends StatusBar {

    private RunningFloat maxValue;
    private RunningFloat value;

    public interface RunningFloat {
        float run(PlayerEntity playerEntity);
    }

    public SimpleStatusBar(Identifier id, Identifier texture, Position position, Direction direction, RunningFloat maxValue, RunningFloat value) {
        super(id, texture, position, direction);
        this.maxValue = maxValue;
        this.value = value;
    }

    @Override
    public void render(MinecraftClient client, DrawContext context, PlayerEntity playerEntity, int x, int y) {
        float current = value.run(playerEntity);
        //System.out.println(((PlayerEntityAccessor)playerEntity).getStamina());
        float max = maxValue.run(playerEntity);
        int scale = 10;
        float apparition = max / scale;
        for(int w = 0; w < scale; ++w) {
            int xPosition = x + (getDirection().equals(Direction.L2R) ? (getPosition().equals(Position.RIGHT) ? -72 : 0) + (w + 1) * 8 : (getPosition().equals(Position.LEFT) ? 72 : 0) + -((w + 1) * 8));
            context.drawTexture(RenderLayer::getGuiTextured, getTexture(), xPosition, y, 0, 0, 9, 9, 27, 9);

            float prevasd = w * apparition;
            float asd = (w + 1) * apparition;
            float sex = (asd) - (apparition / 2);

            if (current > sex) {
                context.drawTexture(RenderLayer::getGuiTextured, getTexture(), xPosition, y, 9, 0, 9, 9, 27, 9);
            }

            if (current > prevasd && current <= sex) {
                context.drawTexture(RenderLayer::getGuiTextured, getTexture(), xPosition, y, 18, 0, 9, 9, 27, 9);
            }
        }
    }

}
