package extension.massclaim.command;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import extension.massclaim.Cuboid;
import extension.massclaim.Main;
import extension.massclaim.Position;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import io.github.toberocat.improvedfactions.utility.ClaimStatus.Status;

public class MassClaimUnclaim extends SubCommand {

	private String factionRg;
	
	public MassClaimUnclaim() {
		super("unclaim", Main.COMMAND_CLAIM_MASS_UNCLAIM_DESCRIPTION);
		factionRg = null;
	}
	
	public MassClaimUnclaim(String factionRg) {
		super("unclaim", Main.COMMAND_CLAIM_MASS_UNCLAIM_DESCRIPTION);
		this.factionRg = factionRg;
	}

	@Override
	protected void CommandExecute(Player player, String[] args) {
        if (FactionUtils.getFactionByRegistry("safezone") == null) {
            Faction faction = Faction.create(player, Language.format(ImprovedFactionsMain.getPlugin().getConfig().getString("general.safezoneText")));
            faction.setRegistryName("safezone");
            faction.getPowerManager().setPower(999999);
        }
		UUID uuid = player.getUniqueId();
		if (Main.POSITIONS.containsKey(uuid)) {
			Position position = Main.POSITIONS.get(uuid);
			
			if (position.getPos1() != null && position.getPos2() != null) {
				Faction faction = null;
				
				if (factionRg == null) {
					faction = FactionUtils.getFaction(player);
				} else {
					faction = FactionUtils.getFactionByRegistry(factionRg);
				}
				
                Cuboid cuboid = new Cuboid(position.getPos1(), position.getPos2());
                
                ArrayList<Chunk> claimedChunks = new ArrayList<>(); 
                
                for (Block block : cuboid) {
                	if (!claimedChunks.contains(block.getChunk())) {
                    	ClaimChunk(faction, block.getChunk(), player);
                    	claimedChunks.add(block.getChunk());
                	}
                }
                
                Main.POSITIONS.remove(uuid);
				Language.sendMessage(Main.COMMAND_CLAIM_MASS_UNCLAIM_SUCCESS, player);
			} else {
				Language.sendMessage(Main.COMMAND_CLAIM_MASS_CLAIM_NOT_ALL_POSITIONS_SET, player);
			}
		} else {
			Language.sendMessage(Main.COMMAND_CLAIM_MASS_CLAIM_NOT_ALL_POSITIONS_SET, player);
		}
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
		return null;
	}
	
    public void ClaimChunk(Faction faction, Chunk chunk, Player player) {

        faction.UnClaimChunk(chunk, status -> {
            if (status == null) { //No power left
                Language.sendMessage(LangMessage.UNCLAIM_ONE_SOMETHING_WRONG, player);
            } else if (status.getClaimStatus() == Status.NOT_ALLOWED_WORLD) {
                player.sendMessage(Language.getPrefix() + "§cCannot unclaim in this world");
            } else if (status.getClaimStatus() == Status.SUCCESS) { //Claimed chunk successfully
                Language.sendMessage(LangMessage.UNCLAIM_ONE_SUCCESS, player);
            } else if (status.getClaimStatus() == Status.NEED_CONNECTION) { //Chunk needs to be connected
                Language.sendMessage(LangMessage.UNCLAIM_ONE_DISCONNECTED, player);
            } else if (status.getClaimStatus() == Status.NOT_CLAIMED) {
                Language.sendMessage(LangMessage.UNCLAIM_ONE_ALREADY_WILDNESS, player);
            } else if (status.getClaimStatus() == Status.NOT_PROPERTY) {
                Language.sendMessage(LangMessage.UNCLAIM_ONE_NOT_YOUR_PROPERTY, player);
            } else if (status.getClaimStatus() == Status.ALREADY_CLAIMED) { //Chunk claimed by another faction
                if (status.getFactionClaim().getRegistryName().equals(faction.getRegistryName())) {
                    player.sendMessage(Language.getPrefix() + "§cThis chunk is already your property");
                } else {
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_NOT_YOUR_PROPERTY, player);
                }
            }
        });
    }

}
