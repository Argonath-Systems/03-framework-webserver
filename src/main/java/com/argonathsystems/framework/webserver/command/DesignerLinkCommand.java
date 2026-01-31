package com.argonathsystems.framework.webserver.command;

import com.argonathsystems.framework.accessorapi.CommandAccessor;
import com.argonathsystems.framework.accessorapi.command.CommandSender;
import com.argonathsystems.framework.webserver.controller.AuthController;

import java.util.UUID;

/**
 * /designer link {@literal <code>} - Link in-game account to Designer portal.
 * 
 * <p>This command allows players to link their in-game account with their
 * Designer portal account. The linking process:
 * <ol>
 *   <li>Player logs into Designer portal with Discord</li>
 *   <li>Player generates a 6-character link code in the portal</li>
 *   <li>Player enters {@code /designer link <code>} in-game</li>
 *   <li>Accounts are linked if code is valid</li>
 * </ol>
 * 
 * <p>Usage: {@code /designer link <6-character-code>}
 * 
 * <p>Example: {@code /designer link ABC123}
 * 
 * @author Argonath Systems
 * @version 1.0.0
 * @since 1.0.0
 */
public class DesignerLinkCommand implements CommandAccessor.CommandExecutor {

    /** Permission required to use this command */
    public static final String PERMISSION = "argonath.designer.link";
    
    /** Command name */
    public static final String COMMAND_NAME = "designer";
    
    private final AuthController authController;

    /**
     * Create a designer link command.
     * 
     * @param authController The auth controller for linking accounts
     */
    public DesignerLinkCommand(AuthController authController) {
        this.authController = authController;
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        // Must be a player
        if (!sender.isPlayer()) {
            sender.sendMessage("§cThis command can only be run by a player.");
            return false;
        }

        // Check subcommand
        if (args.length < 1) {
            sendUsage(sender);
            return false;
        }

        String subcommand = args[0].toLowerCase();
        
        return switch (subcommand) {
            case "link" -> handleLink(sender, args);
            case "status" -> handleStatus(sender);
            case "help" -> handleHelp(sender);
            default -> {
                sendUsage(sender);
                yield false;
            }
        };
    }

    /**
     * Handle /designer link {@literal <code>}
     */
    private boolean handleLink(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /designer link <code>");
            sender.sendMessage("§7Get a link code from the Designer portal.");
            return false;
        }

        String code = args[1].toUpperCase();
        
        // Validate code format
        if (code.length() != 6 || !code.matches("[A-Z0-9]+")) {
            sender.sendMessage("§cInvalid link code format.");
            sender.sendMessage("§7Codes are 6 characters (letters and numbers).");
            return false;
        }

        UUID playerUuid = sender.getPlayerId().orElseThrow();
        String playerName = sender.getName();

        // Attempt to link
        boolean success = authController.linkPlayer(code, playerUuid, playerName);

        if (success) {
            sender.sendMessage("§a✓ Account linked successfully!");
            sender.sendMessage("§7Your in-game account is now connected to the Designer portal.");
            sender.sendMessage("§7You can now access features that require account verification.");
            return true;
        } else {
            sender.sendMessage("§cLink failed. The code may be invalid or expired.");
            sender.sendMessage("§7Generate a new code from the Designer portal and try again.");
            return false;
        }
    }

    /**
     * Handle /designer status
     */
    private boolean handleStatus(CommandSender sender) {
        // For now, we can't check status from in-game without additional infrastructure
        // This would require querying the user store by player UUID
        sender.sendMessage("§eLink Status");
        sender.sendMessage("§7To check your link status, visit the Designer portal.");
        sender.sendMessage("§7Portal: §bhttps://designer.argonath.systems");
        return true;
    }

    /**
     * Handle /designer help
     */
    private boolean handleHelp(CommandSender sender) {
        sender.sendMessage("§6§l━━━ Designer Commands ━━━");
        sender.sendMessage("");
        sender.sendMessage("§e/designer link <code>");
        sender.sendMessage("§7  Link your in-game account to the Designer portal.");
        sender.sendMessage("§7  Get a code from the portal settings.");
        sender.sendMessage("");
        sender.sendMessage("§e/designer status");
        sender.sendMessage("§7  Check your account link status.");
        sender.sendMessage("");
        sender.sendMessage("§e/designer help");
        sender.sendMessage("§7  Show this help message.");
        sender.sendMessage("");
        sender.sendMessage("§7Need help? Visit §bhttps://designer.argonath.systems");
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage("§cUsage: /designer <link|status|help>");
        sender.sendMessage("§7Use §e/designer help §7for more information.");
    }

    /**
     * Register this command with a command accessor.
     * 
     * @param commandAccessor The command accessor to register with
     */
    public void register(CommandAccessor commandAccessor) {
        commandAccessor.register(COMMAND_NAME, this);
    }
}
