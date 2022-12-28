package dev.kyro.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.ACommand;
import dev.kyro.arcticapi.commands.AMultiCommand;
import dev.kyro.arcticapi.misc.AOutput;
import dev.kyro.pitsim.controllers.LockdownManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class LockdownCommand extends ACommand {
	public LockdownCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}

	@Override
	public void execute(CommandSender sender, Command command, String alias, List<String> args) {
		if(args.size() < 1) {
			AOutput.send(sender, "&c&lLOCKDOWN! &7Verification currently " + getText(LockdownManager.verificationRequired()));
			AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha currently " + getText(LockdownManager.captchaRequired()));
			return;
		}
		String type = args.get(0).toLowerCase();

		if(type.equals("verification") || type.equals("verify")) {
			if(args.size() < 2) {
				AOutput.send(sender, "&c&lLOCKDOWN! &7Verification currently " + getText(LockdownManager.verificationRequired()));
				AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha currently " + getText(LockdownManager.captchaRequired()));
				return;
			}
			String enabled = args.get(1).toLowerCase();

			if(enabled.equals("on") || enabled.equals("yes")) {
				if(LockdownManager.verificationRequired()) {
					AOutput.error(sender, "&c&lLOCKDOWN! &7Verification already enabled");
					return;
				}
				LockdownManager.enableVerification();
				AOutput.error(sender, "&c&lLOCKDOWN! &7Verification enabled");
			} else if(enabled.equals("off") || enabled.equals("no")) {
				if(!LockdownManager.verificationRequired()) {
					AOutput.send(sender, "&c&lLOCKDOWN! &7Verification not enabled");
					return;
				}
				LockdownManager.disableVerification();
				AOutput.error(sender, "&c&lLOCKDOWN! &7Verification disabled");
			} else {
				AOutput.send(sender, "&c&lLOCKDOWN! &7verify <on/off>");
			}
		} else if(type.equals("captcha")) {
			if(args.size() < 2) {
				AOutput.send(sender, "&c&lLOCKDOWN! &7Verification currently " + getText(LockdownManager.verificationRequired()));
				AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha currently " + getText(LockdownManager.captchaRequired()));
				return;
			}
			String enabled = args.get(1).toLowerCase();

			if(enabled.equals("on") || enabled.equals("yes")) {
				if(LockdownManager.captchaRequired()) {
					AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha already enabled");
					return;
				}
				LockdownManager.enableCaptcha();
				AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha enabled");
			} else if(enabled.equals("off") || enabled.equals("no")) {
				if(!LockdownManager.captchaRequired()) {
					AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha not enabled");
					return;
				}
				LockdownManager.disableCaptcha();
				AOutput.send(sender, "&c&lLOCKDOWN! &7Captcha disabled");
			} else {
				AOutput.send(sender, "&c&lLOCKDOWN! &7captcha <on/off>");
			}
		} else {
			AOutput.error(sender, "&c&lLOCKDOWN! &7<verify|captcha> <on/off>");
		}
	}

	@Override
	public List<String> getTabComplete(Player player, String current, List<String> args) {
		return null;
	}

	public static String getText(boolean value) {
		return value ? "&a&lON" : "&c&lOFF";
	}
}
