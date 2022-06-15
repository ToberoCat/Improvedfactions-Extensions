package extension.massclaim.command;

import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import extension.massclaim.Main;
import extension.massclaim.Position;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.language.Language;

public class MassClaimPos2 extends SubCommand {

	public MassClaimPos2() {
		super("pos2", Main.COMMAND_CLAIM_MASS_POS2_DESCRIPTION);
	}

	@Override
	protected void CommandExecute(Player player, String[] args) {
		UUID uuid = player.getUniqueId();
		
		Location pl = new Location(player.getWorld(), player.getLocation().getX(), 0, player.getLocation().getZ());

		if (Main.POSITIONS.containsKey(uuid)) {
			Main.POSITIONS.get(uuid).setPos2(pl);
		} else {
			Main.POSITIONS.put(uuid, new Position(null, pl));
		}
		
		Language.sendMessage(Main.COMMAND_CLAIM_MASS_POS2_SUCCESS, player);
	}

	@Override
	protected List<String> CommandTab(Player arg0, String[] arg1) {
		return null;
	}

}
