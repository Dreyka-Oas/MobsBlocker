package oas.work.mobs_blocker.command;

import org.checkerframework.checker.units.qual.s;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.commands.Commands;

@Mod.EventBusSubscriber
public class SpawnblockerCommand {
	@SubscribeEvent
	public static void registerCommand(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("spawnblocker").requires(s -> s.hasPermission(4))

		);
	}

}