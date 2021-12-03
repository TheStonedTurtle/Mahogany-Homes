package thestonedturtle.mahoganyhomes;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class MahoganyHomesInventoryOverlay extends WidgetItemOverlay {

    private static final Color FILL_COLOR = new Color(0, 255, 0, 50);

    private final Cache<Long, Image> fillCache;
    private final MahoganyHomesPlugin plugin;
    private final MahoganyHomesConfig config;
    private final ItemManager itemManager;

    @Inject
    private MahoganyHomesInventoryOverlay(ItemManager itemManager, MahoganyHomesPlugin plugin, MahoganyHomesConfig config) {
        this.plugin = plugin;
        this.config = config;
        showOnInventory();
        this.itemManager = itemManager;
        fillCache = CacheBuilder.newBuilder()
                .concurrencyLevel(1)
                .maximumSize(32)
                .build();
    }

    @Override
    public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem) {
        final Home home = plugin.getCurrentHome();
        if (home == null)
            return;

        int tabItemId = -1;

        if (config.highlightTeleTabs()) {
            switch (home.getName().toUpperCase()) {
                case "JESS":
                case "NOELLA":
                case "ROSS":
                    tabItemId = ItemID.ARDOUGNE_TELEPORT;
                    break;
                case "LARRY":
                case "NORMAN":
                case "TAU":
                    tabItemId = ItemID.FALADOR_TELEPORT;
                    break;
                case "BARBARA":
                case "LEELA":
                case "MARIAH":
                    tabItemId = ItemID.TELEPORT_TO_HOUSE;
                    break;
                case "BOB":
                case "JEFF":
                case "SARAH":
                    tabItemId = ItemID.VARROCK_TELEPORT;
                    break;
                default:
                    break;
            }

            if (tabItemId < 0)
                return;

            if (tabItemId == itemId) {
                Rectangle bounds = widgetItem.getCanvasBounds();
                final Image image = getFillImage(FILL_COLOR, widgetItem.getId(), widgetItem.getQuantity());
                graphics.drawImage(image, (int) bounds.getX(), (int) bounds.getY(), null);
            }
        }
    }

    private Image getFillImage(Color color, int itemId, int qty)
    {
        long key = (((long) itemId) << 32) | qty;
        Image image = fillCache.getIfPresent(key);
        if (image == null)
        {
            int alpha = (int) (((float)(config.inventoryHighlightAlpha() / 100.0)) * 255.0);
            final Color fillColor = ColorUtil.colorWithAlpha(color, alpha);
            image = ImageUtil.fillImage(itemManager.getImage(itemId, qty, false), fillColor);
            fillCache.put(key, image);
        }
        return image;
    }

    void invalidateCache() {
        fillCache.invalidateAll();
    }
}
