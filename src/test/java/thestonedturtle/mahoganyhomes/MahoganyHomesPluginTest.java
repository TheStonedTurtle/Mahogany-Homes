package thestonedturtle.mahoganyhomes;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class MahoganyHomesPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(MahoganyHomesPlugin.class);
		RuneLite.main(args);
	}
}