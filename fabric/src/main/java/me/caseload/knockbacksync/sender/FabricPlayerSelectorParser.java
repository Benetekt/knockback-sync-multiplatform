package me.caseload.knockbacksync.sender;

import me.caseload.knockbacksync.command.generic.AbstractPlayerSelectorParser;
import me.caseload.knockbacksync.command.generic.PlayerSelector;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.minecraft.modded.parser.VanillaArgumentParsers;
import org.incendo.cloud.parser.ParserDescriptor;

import java.util.concurrent.CompletableFuture;

public class FabricPlayerSelectorParser<C> extends AbstractPlayerSelectorParser<C> {

    @Override
    public ParserDescriptor<C, PlayerSelector> descriptor() {
        return createDescriptor();
    }

    @Override
    protected ParserDescriptor<C, ?> getPlatformSpecificDescriptor() {
        return VanillaArgumentParsers.singlePlayerSelectorParser();
    }

    @Override
    protected CompletableFuture<PlayerSelector> adaptToCommonSelector(CommandContext<C> context, Object platformSpecificSelector) {
        return CompletableFuture.completedFuture(new FabricPlayerSelectorAdapter((org.incendo.cloud.minecraft.modded.data.SinglePlayerSelector) platformSpecificSelector));
    }
}
