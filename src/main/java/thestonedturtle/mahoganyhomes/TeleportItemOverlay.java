package thestonedturtle.mahoganyhomes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ImageUtil;

class TeleportItemOverlay extends WidgetItemOverlay
{
	private final ItemManager itemManager;
	private final MahoganyHomesPlugin plugin;
	private final MahoganyHomesConfig config;

	@Inject
	private TeleportItemOverlay(ItemManager itemManager, MahoganyHomesPlugin plugin, MahoganyHomesConfig config)
	{
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		showOnInventory();
		showOnEquipment();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem itemWidget)
	{
		TeleportItem teleportItem = plugin.teleportItem;

		if (teleportItem == null || itemId != teleportItem.ItemId || !config.highlightTeleports())
		{
			return;
		}

		Color color = config.highlightTeleportsColor();

		if (color == null)
		{
			return;
		}

		Rectangle bounds = itemWidget.getCanvasBounds();

		final BufferedImage outline = itemManager.getItemOutline(teleportItem.ItemId, itemWidget.getQuantity(), new Color(color.getRGB()));
		graphics.drawImage(outline, (int) bounds.getX(), (int) bounds.getY(), null);

		Image image = ImageUtil.fillImage(itemManager.getImage(teleportItem.ItemId, itemWidget.getQuantity(), false), color);
		graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
	}
}
