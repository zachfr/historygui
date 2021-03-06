package me.zachary.historygui.utils;

import me.zachary.historygui.Historygui;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.guis.pagination.ZPaginationButtonBuilder;
import me.zachary.zachcore.guis.pagination.ZPaginationButtonType;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.function.Consumer;
import java.util.stream.IntStream;

public class GuiUtils {
    private static final Historygui plugin = JavaPlugin.getPlugin(Historygui.class);

    public static void setGlass(ZMenu menu, int page) {
        int[] TILES_TO_UPDATE;
        switch (menu.getRowsPerPage()) {
            case 3:
                TILES_TO_UPDATE = new int[]{
                        0,  1,  2,  3,  4,  5,  6,  7,  8,
                        9,                              17,
                        18, 19, 20, 21, 22, 23, 24, 25, 26
                };
                break;
            case 4:
                TILES_TO_UPDATE = new int[]{
                        0, 1, 2, 3, 4, 5, 6, 7, 8,
                        9,                              17,
                        18,                             26,
                        27, 28, 29, 30, 31, 32, 33, 34, 35
                };
                break;
            case 5:
                TILES_TO_UPDATE = new int[]{
                        0,  1,  2,  3,  4,  5,  6,  7,  8,
                        9,                             17,
                        18,                            26,
                        27,                            35,
                        36, 37, 38, 39, 40, 41, 42, 43, 44
                };
                break;
            default:
                TILES_TO_UPDATE = null;
                break;
        }
        IntStream.range(0, TILES_TO_UPDATE.length).map(i -> TILES_TO_UPDATE.length - i + -1).forEach(
                index -> menu.setButton(page, TILES_TO_UPDATE[index], new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Global glass color")).parseItem()).name(" ").build()))
        );
    }

    public static ZPaginationButtonBuilder getPaginationButtonBuilder(Player player, me.zachary.historygui.player.Player target, Runnable runnable, Boolean sort, Consumer<InventoryClickEvent> sortClick){
        return (type, inventory) -> {
            switch (type) {
                case CLOSE_BUTTON:
                    if(runnable == null){
                        return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Close button.Item")).parseItem())
                                .name(plugin.getGuiConfig().getString("Gui.Pagination.Close button.Name"))
                                .build()
                        ).withListener(event -> {
                            event.getWhoClicked().closeInventory();
                        });
                    }else{
                        return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Back button.Item")).parseItem())
                                .name(plugin.getGuiConfig().getString("Gui.Pagination.Back button.Name"))
                                .build()
                        ).withListener(event -> {
                            Bukkit.getScheduler().runTask(plugin, runnable);
                        });
                    }

                case PREV_BUTTON:
                    if (inventory.getCurrentPage() > 0) return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Previous button.Item")).parseItem())
                            .name(plugin.getGuiConfig().getString("Gui.Pagination.Previous button.Name"))
                            .lore(LoreUtils.getLore("Gui.Pagination.Previous button.Lore", "{page}", String.valueOf(inventory.getCurrentPage())))
                            .build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.previousPage(event.getWhoClicked());
                    });
                    else return null;

                case CURRENT_BUTTON:
                    return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Current button.Item")).parseItem())
                            .name(plugin.getGuiConfig().getString("Gui.Pagination.Current button.Name").replace("{currentpage}", String.valueOf(inventory.getCurrentPage() + 1)).replace("{maxpage}", String.valueOf(inventory.getMaxPage())))
                            .lore(LoreUtils.getLore("Gui.Pagination.Current button.Lore", "{page}", String.valueOf(inventory.getCurrentPage() + 1)))
                            .build()
                    ).withListener(event -> event.setCancelled(true));

                case NEXT_BUTTON:
                    if (inventory.getCurrentPage() < inventory.getMaxPage() - 2) return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Next button.Item")).parseItem())
                            .name(plugin.getGuiConfig().getString("Gui.Pagination.Next button.Name"))
                            .lore(LoreUtils.getLore("Gui.Pagination.Next button.Lore", "{page}", String.valueOf(inventory.getCurrentPage() + 2)))
                            .build()
                    ).withListener(event -> {
                        event.setCancelled(true);
                        inventory.nextPage(event.getWhoClicked());
                    });
                    else return null;
                case UNASSIGNED:
                    if(sort != null)
                        return new ZButton(new ItemBuilder(XMaterial.valueOf(plugin.getGuiConfig().getString("Gui.Pagination.Sort button.Item")).parseItem())
                            .name(plugin.getGuiConfig().getString("Gui.Pagination.Sort button.Name"))
                            .lore(LoreUtils.getLore("Gui.Pagination.Sort button.Lore", "{sort}", sort ? plugin.getGuiConfig().getStringList("Sort placeholder.Descending") : plugin.getGuiConfig().getStringList("Sort placeholder.Ascending")))
                            .build()).withListener(sortClick::accept);
                    else return null;
                case CUSTOM_1:
                case CUSTOM_2:
                case CUSTOM_3:
                case CUSTOM_4:
                default:
                    return null;
            }
        };
    }
}
