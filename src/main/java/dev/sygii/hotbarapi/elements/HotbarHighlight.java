package dev.sygii.hotbarapi.elements;

import dev.sygii.hotbarapi.HotbarAPI;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class HotbarHighlight {
    private final Identifier id;
    @Nullable
    private final Identifier texture;
    private final Color color;

    private int ticksSelected = 0;

    public HotbarHighlight(Identifier id, Color color){
        this(id, null, color);
    }

    public HotbarHighlight(Identifier id, @Nullable Identifier texture, Color color){
        this.id = id;
        this.texture = texture;
        this.color = color;
    }

    public void reset() {
        ticksSelected = 0;
    }

    public void tick() {
        ticksSelected++;
    }

    public int getTicksSelected() {
        return ticksSelected;
    }

    public Identifier getId() {
        return id;
    }

    public @Nullable Identifier getTexture() {
        return texture;
    }

    public Color getColor() {
        return color;
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(getId());
        if (getTexture() == null) {
            buf.writeIdentifier(HotbarAPI.NULL_HOTBAR);
        }else {
            buf.writeIdentifier(getTexture());
        }
        buf.writeInt(getColor().getRGB());
    }

    public static HotbarHighlight read(PacketByteBuf buf) {
        Identifier id = buf.readIdentifier();
        Identifier texture = buf.readIdentifier();
        Color color = new Color(buf.readInt());
        if (texture == HotbarAPI.NULL_HOTBAR) {
            return new HotbarHighlight(id, color);
        }else {
            return new HotbarHighlight(id, texture, color);
        }
    }
}
