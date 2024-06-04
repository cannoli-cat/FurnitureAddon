package cannolicat.furnitureaddon;

import cannolicat.furnitureaddon.conditions.FurnitureVariableSetCondition;
import cannolicat.furnitureaddon.mechanics.AddFurnitureVariableMechanic;
import cannolicat.furnitureaddon.mechanics.UnsetFurnitureVariableMechanic;
import cannolicat.furnitureaddon.mechanics.SetFurnitureVariableMechanic;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.compatibility.CrucibleSupport;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public final class FurnitureAddon extends JavaPlugin implements Listener {
    private static FurnitureAddon inst;
    public HashMap<AbstractEntity, ArrayList<NamespacedKey>> tempVars;

    @Override
    public void onEnable() {
        inst = this;
        tempVars = new HashMap<>();
        registerPlaceholders();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        if(!tempVars.isEmpty()) {
            for (AbstractEntity e : tempVars.keySet()) {
                for (NamespacedKey key : tempVars.get(e)) {
                    e.getDataContainer().remove(key);
                }
            }
        }
    }

    @EventHandler
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event)	{
        switch (event.getMechanicName().toUpperCase()) {
            case "SETFURNITUREVARIABLE", "SETFVAR", "SFVAR" ->
                event.register(new SetFurnitureVariableMechanic(event.getConfig()));
            case "UNSETFURNITUREVARIABLE", "FURNITUREUNSET", "UNSETFVAR" ->
                event.register(new UnsetFurnitureVariableMechanic(event.getConfig()));
            case "FURNITUREVARIABLEADD", "ADDFVAR", "FVARADD" ->
                event.register(new AddFurnitureVariableMechanic(event.getConfig()));
            default -> {}
        }
    }

    @EventHandler
    public void onMythicConditionLoad(MythicConditionLoadEvent e) {
        switch (e.getConditionName().toUpperCase()) {
            case "FVARSET", "FURNITUREVARIABLESET", "FVARISSET" ->
                e.register(new FurnitureVariableSetCondition(e.getConfig()));
            default -> {}
        }
    }

    @EventHandler
    public void onMythicReload(MythicReloadedEvent e) {
        registerPlaceholders();
    }

    private void registerPlaceholders() {
        CrucibleSupport crucible = MythicBukkit.inst().getCompatibility().getCrucible().get();
        PlaceholderExecutor manager = MythicBukkit.inst().getPlaceholderManager();

        manager.register("caster.fvar", Placeholder.meta((meta, arg) -> getFurnitureKey(meta.getCaster().getEntity(), arg, crucible)));

        manager.register("trigger.fvar", Placeholder.meta((meta, arg) -> getFurnitureKey(meta.getTrigger(), arg, crucible)));

        manager.register("target.fvar", Placeholder.target((meta, target, arg) -> getFurnitureKey(target, arg, crucible)));
    }

    @Nullable
    private String getFurnitureKey(AbstractEntity target, String arg, CrucibleSupport crucible) {
        if (crucible.isFurniture(target)) {
            PersistentDataContainer data = target.getDataContainer();
            NamespacedKey key = new NamespacedKey(this, arg);

            if(data.has(key, PersistentDataType.STRING)) {
                return data.get(key, PersistentDataType.STRING);
            }
            else if (data.has(key, PersistentDataType.INTEGER)) {
                return data.get(key, PersistentDataType.INTEGER) + "";
            }
            else if (data.has(key, PersistentDataType.FLOAT)) {
                return data.get(key, PersistentDataType.FLOAT) + "";
            }

            return "UNDEFINED";
        }
        return "UNDEFINED";
    }

    public static FurnitureAddon inst() {
        return inst;
    }
}
