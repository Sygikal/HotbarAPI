package dev.sygii.hotbarapi.command;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import dev.sygii.hotbarapi.HotbarAPI;
import dev.sygii.hotbarapi.elements.HotbarHighlight;
import dev.sygii.hotbarapi.network.packet.HotbarHighlightPacket;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.command.CommandSource;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.command.argument.ItemStackArgument;
import net.minecraft.command.argument.ItemStackArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class CommandHandler {

    private static final SuggestionProvider<ServerCommandSource> HIGHLIGHT_SUGGESTION_PROVIDER = (context, builder) -> CommandSource.suggestIdentifiers(
            HotbarAPI.hotbarHighlights.values().stream().map(HotbarHighlight::getId), builder);

    public static void init() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> {
            dispatcher.register((CommandManager.literal("hhs").requires((serverCommandSource) -> {
                return serverCommandSource.hasPermissionLevel(2);
            })).then(CommandManager.argument("targets", EntityArgumentType.players())
                    // Add values
                    .then(CommandManager.literal("add").then(CommandManager.argument("hotbarHighlight", IdentifierArgumentType.identifier()).suggests(HIGHLIGHT_SUGGESTION_PROVIDER).then(CommandManager.argument("slot", IntegerArgumentType.integer(0, 8)).executes((commandContext) -> {
                        return executeSkillCommand(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "hotbarHighlight"),
                                IntegerArgumentType.getInteger(commandContext, "slot"));
                    }))))
                    .then(CommandManager.literal("item").then(CommandManager.argument("itemName", ItemStackArgumentType.itemStack(dedicated)).then(CommandManager.argument("hotbarHighlight", IdentifierArgumentType.identifier()).suggests(HIGHLIGHT_SUGGESTION_PROVIDER).executes((commandContext) -> {
                        return highlightItem(commandContext.getSource(), EntityArgumentType.getPlayers(commandContext, "targets"), IdentifierArgumentType.getIdentifier(commandContext, "hotbarHighlight"),
                                ItemStackArgumentType.getItemStackArgument(commandContext, "itemName"));
                    }))))));
        });
    }

    private static int highlightItem(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Identifier hotbarHighlight, ItemStackArgument item) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            int index = indexOf(serverPlayerEntity, item.getItem().getDefaultStack());
            if (index != -1) {
                ServerPlayNetworking.send(serverPlayerEntity, new HotbarHighlightPacket(index, hotbarHighlight));
                source.sendFeedback(() -> Text.literal("Highlighting item"), true);
            } else {
                source.sendFeedback(() -> Text.literal("Item not in hotbar"), true);
            }
        }

        return targets.size();
    }

    public static int indexOf(ServerPlayerEntity serverPlayerEntity, ItemStack stack) {
        for (int i = 0; i < 9; i++) {
            //? if =1.20.1 {
			ItemStack itemStack = serverPlayerEntity.getInventory().main.get(i);
			//?} else {
            /*ItemStack itemStack = serverPlayerEntity.getInventory().getMainStacks().get(i);
            *///?}
            if (!itemStack.isEmpty() && itemStack.getItem() == stack.getItem()) {
                return i;
            }
        }

        return -1;
    }

    private static int executeSkillCommand(ServerCommandSource source, Collection<ServerPlayerEntity> targets, Identifier hotbarHighlight, int slot) {
        for (ServerPlayerEntity serverPlayerEntity : targets) {
            ServerPlayNetworking.send(serverPlayerEntity, new HotbarHighlightPacket(slot, hotbarHighlight));
            source.sendFeedback(() -> Text.translatable("commands.level.changed", serverPlayerEntity.getDisplayName()), true);
        }

        return targets.size();
    }
}
