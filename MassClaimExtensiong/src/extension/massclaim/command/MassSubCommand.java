package extension.massclaim.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import extension.massclaim.Main;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommand;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings;
import io.github.toberocat.improvedfactions.commands.subCommands.SubCommandSettings.NYI;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;

public class MassSubCommand extends SubCommand {

	public ArrayList<SubCommand> subCommands = new ArrayList<>();;
	public MassSubCommand(String command, String frg) {
		super(command, Main.COMMAND_CLAIM_MASS_DESCRIPTION);
		subCommands.add(new MassClaimPos1());
		subCommands.add(new MassClaimPos2());
		subCommands.add(new MassClaimClaim(frg));
		subCommands.add(new MassClaimUnclaim(frg));
	}

	@Override
	public SubCommandSettings getSettings() {
		return super.getSettings().setNeedsFaction(NYI.Yes);
	}
	
	@Override
	protected void CommandExecute(Player player, String[] args) {
        if(!SubCommand.CallSubCommands(subCommands, player, args)) {
            Language.sendMessage(LangMessage.THIS_COMMAND_DOES_NOT_EXIST, player);
        }
	}

	@Override
	protected List<String> CommandTab(Player player, String[] args) {
        return SubCommand.CallSubCommandsTab(subCommands, player, args);
	}

}
