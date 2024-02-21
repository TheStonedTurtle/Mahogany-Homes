package thestonedturtle.mahoganyhomes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.ItemContainer;

@AllArgsConstructor
public enum TeleportItems
{
	// East Ardy
	JESS(Arrays.asList(
			new TeleportItem(ItemID.ARDOUGNE_TELEPORT, 38),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 40),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 40),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_4, 70),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_3, 70),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_2, 70),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_1, 70)
	)),
	NOELLA(Arrays.asList(
			new TeleportItem(ItemID.ARDOUGNE_TELEPORT, 16),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_4, 96),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_3, 96),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_2, 96),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_1, 96),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 100),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 100)
	)),
	ROSS(Arrays.asList(
			new TeleportItem(ItemID.ARDOUGNE_TELEPORT, 42),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 43),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 43),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_4, 92),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_3, 92),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_2, 92),
			new TeleportItem(ItemID.ARDOUGNE_CLOAK_1, 92)
	)),

	// Falador
	LARRY(Arrays.asList(
			new TeleportItem(ItemID.RING_OF_WEALTH_I5, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_I4, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_I3, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_I2, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_I1, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_5, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_4, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_3, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_2, 37),
			new TeleportItem(ItemID.RING_OF_WEALTH_1, 37),
			new TeleportItem(ItemID.SKILLS_NECKLACE6, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE5, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE4, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE3, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE2, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE1, 38),
			new TeleportItem(ItemID.FALADOR_TELEPORT, 67),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 80),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 80)
	)),
	NORMAN(Arrays.asList(
			new TeleportItem(ItemID.RING_OF_WEALTH_I5, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_I4, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_I3, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_I2, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_I1, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_5, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_4, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_3, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_2, 38),
			new TeleportItem(ItemID.RING_OF_WEALTH_1, 38),
			new TeleportItem(ItemID.SKILLS_NECKLACE6, 39),
			new TeleportItem(ItemID.SKILLS_NECKLACE5, 39),
			new TeleportItem(ItemID.SKILLS_NECKLACE4, 39),
			new TeleportItem(ItemID.SKILLS_NECKLACE3, 39),
			new TeleportItem(ItemID.SKILLS_NECKLACE2, 39),
			new TeleportItem(ItemID.SKILLS_NECKLACE1, 39),
			new TeleportItem(ItemID.FALADOR_TELEPORT, 70),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 71),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 71)
	)),
	TAU(Arrays.asList(
			new TeleportItem(ItemID.SKILLS_NECKLACE6, 45),
			new TeleportItem(ItemID.SKILLS_NECKLACE5, 45),
			new TeleportItem(ItemID.SKILLS_NECKLACE4, 45),
			new TeleportItem(ItemID.SKILLS_NECKLACE3, 45),
			new TeleportItem(ItemID.SKILLS_NECKLACE2, 45),
			new TeleportItem(ItemID.SKILLS_NECKLACE1, 45),
			new TeleportItem(ItemID.RING_OF_WEALTH_I5, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_I4, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_I3, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_I2, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_I1, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_5, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_4, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_3, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_2, 47),
			new TeleportItem(ItemID.RING_OF_WEALTH_1, 47),
			new TeleportItem(ItemID.FALADOR_TELEPORT, 78),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 79),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 79)
	)),

	// Hosidius
	BARBARA(Arrays.asList(
			new TeleportItem(ItemID.TELEPORT_TO_HOUSE, 14),
			new TeleportItem(ItemID.HOSIDIUS_TELEPORT, 14),
			new TeleportItem(ItemID.XERICS_TALISMAN, 27),
			new TeleportItem(ItemID.KHAREDSTS_MEMOIRS, 70),
			new TeleportItem(ItemID.BOOK_OF_THE_DEAD, 70)
	)),
	LEELA(Arrays.asList(
			new TeleportItem(ItemID.XERICS_TALISMAN, 30),
			new TeleportItem(ItemID.KHAREDSTS_MEMOIRS, 67),
			new TeleportItem(ItemID.BOOK_OF_THE_DEAD, 67),
			new TeleportItem(ItemID.TELEPORT_TO_HOUSE, 72),
			new TeleportItem(ItemID.HOSIDIUS_TELEPORT, 72)
	)),
	MARIAH(Arrays.asList(
			new TeleportItem(ItemID.XERICS_TALISMAN, 50),
			new TeleportItem(ItemID.KHAREDSTS_MEMOIRS, 56),
			new TeleportItem(ItemID.BOOK_OF_THE_DEAD, 56),
			new TeleportItem(ItemID.TELEPORT_TO_HOUSE, 100),
			new TeleportItem(ItemID.HOSIDIUS_TELEPORT, 100)
	)),

	// Varrock
	BOB(Arrays.asList(
			new TeleportItem(ItemID.VARROCK_TELEPORT, 60),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 100),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 100)
	)),
	JEFF(Arrays.asList(
			new TeleportItem(ItemID.VARROCK_TELEPORT, 23),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 30),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 30)
	)),
	SARAH(Arrays.asList(
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE, 28),
			new TeleportItem(ItemID.ACHIEVEMENT_DIARY_CAPE_T, 28),
			new TeleportItem(ItemID.VARROCK_TELEPORT, 37)
	));

	private final List<TeleportItem> teleportItems;

	public TeleportItem getClosestTeleportItemOnPlayer(Client client)
	{
		ItemContainer inventoryContainer = client.getItemContainer(InventoryID.INVENTORY.getId());
		ItemContainer equipmentContainer = client.getItemContainer(InventoryID.EQUIPMENT.getId());

		Collections.sort(teleportItems);

		for (TeleportItem teleportItem : teleportItems)
		{
			if (inventoryContainer != null)
			{
				for (Item item : inventoryContainer.getItems())
				{
					if (item.getId() == teleportItem.ItemId)
					{
						return teleportItem;
					}
				}
			}

			if (equipmentContainer != null)
			{
				for (Item item : equipmentContainer.getItems())
				{
					if (item.getId() == teleportItem.ItemId)
					{
						return teleportItem;
					}
				}
			}
		}

		return null;
	}
}
