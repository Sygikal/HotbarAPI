package dev.sygii.hotbarapi.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class StatusBarLogic {
    private final Identifier id;
    private RunningFloat maxValue;
    private RunningFloat value;

    public StatusBarLogic(Identifier id, RunningFloat maxValue, RunningFloat value) {
        this.id = id;
        this.maxValue = maxValue;
        this.value = value;
    }

    public void setValue(RunningFloat value) {
        this.value = value;
    }

    public void setMaxValue(RunningFloat maxValue) {
        this.maxValue = maxValue;
    }

    public float getValue(PlayerEntity playerEntity) {
        return value.run(playerEntity);
    }

    public float getMaxValue(PlayerEntity playerEntity) {
        return maxValue.run(playerEntity);
    }

    public Identifier getId() {
        return id;
    }

    public boolean isVisible(MinecraftClient client, PlayerEntity playerEntity) {
        return true;
    }

    public interface RunningFloat {
        float run(PlayerEntity playerEntity);
    }
}
