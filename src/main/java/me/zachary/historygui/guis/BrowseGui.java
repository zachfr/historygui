package me.zachary.historygui.guis;

import litebans.api.Database;
import me.zachary.historygui.Historygui;
import me.zachary.historygui.utils.GuiUtils;
import me.zachary.zachcore.guis.ZMenu;
import me.zachary.zachcore.guis.buttons.ZButton;
import me.zachary.zachcore.utils.items.ItemBuilder;
import me.zachary.zachcore.utils.xseries.SkullUtils;
import me.zachary.zachcore.utils.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class BrowseGui {
    private Historygui plugin;

    public BrowseGui(Historygui plugin) {
        this.plugin = plugin;
    }

    public void openBrowseGui(Player player){
        ZMenu browseGui = Historygui.getGUI().create(plugin.getGuiConfig().getString("Gui.Browse.Title name"), 5);
        browseGui.setPaginationButtonBuilder(GuiUtils.getPaginationButtonBuilder(player, null, null));
        GuiUtils.setGlass(browseGui, 0);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String query = "select * from {history} WHERE uuid != 'CONSOLE'";
            int slot = 10;
            int page = 0;
            try (PreparedStatement st = Database.get().prepareStatement(query)) {
                try (ResultSet rs = st.executeQuery()) {
                    while (rs.next()) {
                        OfflinePlayer target = Bukkit.getServer().getOfflinePlayer(UUID.fromString(rs.getString("uuid")));
                        ZButton playerButton = new ZButton(new ItemBuilder(SkullUtils.getSkull(target.getUniqueId()))
                                .name("&7" + target.getName())
                                .build()).withListener(inventoryClickEvent -> {
                            player.openInventory(new HistoryGui(plugin).getHistoryInventory(player, target));
                        });

                        browseGui.setButton(page, slot, playerButton);

                        slot++;
                        if(slot == 35){
                            slot = 0;
                            page++;
                            GuiUtils.setGlass(browseGui, page);
                        }
                    }
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        player.openInventory(browseGui.getInventory());
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
