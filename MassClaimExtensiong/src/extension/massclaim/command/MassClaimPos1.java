package extension.massclaim.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import extension.massclaim.Main;
import extension.massclaim.Position;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;

public class MassClaimPos1 extends SubCommand {

	public MassClaimPos1() {
		super("pos1", Main.COMMAND_CLAIM_MASS_POS1_DESCRIPTION);
	}

	@Override
	protected void CommandExecute(Player player, String[] args) {
		UUID uuid = player.getUniqueId();

		Location pl = new Location(player.getWorld(), player.getLocation().getX(), 0, player.getLocation().getZ());
		
		if (Main.POSITIONS.containsKey(uuid)) {
			Main.POSITIONS.get(uuid).setPos1(pl);
		} else {
			Main.POSITIONS.put(uuid, new Position(pl, null));
		}
		
		Language.sendMessage(Main.COMMAND_CLAIM_MASS_POS1_SUCCESS, player);
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
		return null;
	}

}
