package dev.sygii.hotbarapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.ColorHelper;

import java.awt.*;

public class ColorUtil {

    public static void setColor(DrawContext context, int hex) {
        //? if =1.20.1 {
        RenderSystem.setShaderColor(ColorHelper.Argb.getRed(hex) / 255.0F, ColorHelper.Argb.getGreen(hex) / 255.0F, ColorHelper.Argb.getBlue(hex) / 255.0F, ColorHelper.Argb.getAlpha(hex) / 255.0F);
        //?} else if <1.21.6 {
        /*RenderSystem.setShaderColor(ColorHelper.getRed(hex) / 255.0F, ColorHelper.getGreen(hex) / 255.0F, ColorHelper.getBlue(hex) / 255.0F, ColorHelper.getAlpha(hex) / 255.0F);
        *///?}
        //context.setShaderColor(ColorHelper.Argb.getRed(hex) / 255.0F, ColorHelper.Argb.getGreen(hex) / 255.0F, ColorHelper.Argb.getBlue(hex) / 255.0F, ColorHelper.Argb.getAlpha(hex) / 255.0F);
    }

    public static int fade(int color1, int color2, float offset) {
        if (offset > 1) {
            offset = 1 - offset % 1;
        }

        double invert = 1 - offset;
        int r = (int) ((color1 >> 16 & 0xFF) * invert + (color2 >> 16 & 0xFF) * offset);
        int g = (int) ((color1 >> 8 & 0xFF) * invert + (color2 >> 8 & 0xFF) * offset);
        int b = (int) ((color1 & 0xFF) * invert + (color2 & 0xFF) * offset);
        int a = (int) ((color1 >> 24 & 0xFF) * invert + (color2 >> 24 & 0xFF) * offset);
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xff);
    }

    public static int getRainbow(int speed, int offset, float saturation) {
        float hue = (float) (System.currentTimeMillis() % (int)speed) + (offset);
        while (hue > speed) {
            hue -= speed;
        }
        hue /= speed;
        return Color.HSBtoRGB(hue, saturation, 1f);
    }

}
