package extension.massclaim;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import extension.massclaim.command.MassSubCommand;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.FactionCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.AdminSubCommand;
import io.github.toberocat.improvedfactions.commands.factionCommands.ClaimChunkSubCommand;
import io.github.toberocat.improvedfactions.extentions.Extension;
import io.github.toberocat.improvedfactions.extentions.ExtensionRegistry;
import io.github.toberocat.improvedfactions.language.LangDefaultDataAddon;
import io.github.toberocat.improvedfactions.language.LangMessage;

public class Main extends Extension {

	public static final String COMMAND_CLAIM_MASS_DESCRIPTION = "command.claim.mass.description";
	public static final String COMMAND_CLAIM_MASS_POS1_DESCRIPTION = "command.claim.mass.pos1.description";
	public static final String COMMAND_CLAIM_MASS_POS2_DESCRIPTION = "command.claim.mass.pos2.description";
	public static final String COMMAND_CLAIM_MASS_CLAIM_DESCRIPTION = "command.claim.mass.claim.description";
	public static final String COMMAND_CLAIM_MASS_CLAIM_SUCCESS = "command.claim.mass.claim.success";
	public static final String COMMAND_CLAIM_MASS_CLAIM_NOT_ALL_POSITIONS_SET = "command.claim.mass.claim.not-all-positions-set";
	public static final String COMMAND_CLAIM_MASS_UNCLAIM_DESCRIPTION = "command.claim.mass.unclaim.description";
	public static final String COMMAND_CLAIM_MASS_UNCLAIM_SUCCESS = "command.claim.mass.unclaim.success";

	public static final String COMMAND_CLAIM_MASS_POS1_SUCCESS = "command.claim.mass.pos1.success";
	public static final String COMMAND_CLAIM_MASS_POS2_SUCCESS = "command.claim.mass.pos2.success";

	
	public static Map<UUID, Position> POSITIONS = new HashMap<>();
	
	@Override
	protected ExtensionRegistry register() {
		return new ExtensionRegistry("Massclaim", "1.0", new String[] {
				"BETAv4.0.0"
		});
	}
	
	@Override
	public boolean preLoad(ImprovedFactionsMain plugin) {
		LangMessage.langDefaultDataAddons.add(new LangDefaultDataAddon() {
			
			@Override
			public Map<String, String> Add() {
				Map<String, String> messages = new HashMap<>();
				messages.put(COMMAND_CLAIM_MASS_DESCRIPTION, "&fClaim land fast using a world-edit like system");
				messages.put(COMMAND_CLAIM_MASS_POS1_DESCRIPTION, "&fSet the first position for your mass claim");
				messages.put(COMMAND_CLAIM_MASS_POS2_DESCRIPTION, "&Set the second position for your mass claim");
				messages.put(COMMAND_CLAIM_MASS_CLAIM_DESCRIPTION, "&fClaim all chunks between your set positions");
				messages.put(COMMAND_CLAIM_MASS_UNCLAIM_DESCRIPTION, "&fUnclaim all chunks between your set positions");
				
				messages.put(COMMAND_CLAIM_MASS_POS1_SUCCESS, "&a&lSuccessfully&f set the first position");
				messages.put(COMMAND_CLAIM_MASS_POS2_SUCCESS, "&a&lSuccessfully&f set the second position");
				
				messages.put(COMMAND_CLAIM_MASS_UNCLAIM_SUCCESS, "&a&lSuccessfully&f unclaimed");
				messages.put(COMMAND_CLAIM_MASS_CLAIM_SUCCESS, "&a&lSuccessfully&f claimed");
				messages.put(COMMAND_CLAIM_MASS_CLAIM_NOT_ALL_POSITIONS_SET, "&c&lFaild&f claiming. Not all positions are set");

				return messages;
			}
		});
		return true;
	}
	
	@Override
	protected void OnEnable(ImprovedFactionsMain plugin) {
		FactionCommand.subCommands.add(new MassSubCommand("mass", null));
		AdminSubCommand.subCommands.add(new MassSubCommand("massSafezone", "safezone"));
	}

}
