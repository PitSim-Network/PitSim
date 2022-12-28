package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.AMultiCommand;

public class BaseSetCommand extends AMultiCommand {
	public BaseSetCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}
}
