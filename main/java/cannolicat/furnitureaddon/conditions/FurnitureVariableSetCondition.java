package cannolicat.furnitureaddon.conditions;

import cannolicat.furnitureaddon.FurnitureAddon;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class FurnitureVariableSetCondition implements IEntityCondition {
    protected final NamespacedKey key;

    public FurnitureVariableSetCondition(MythicLineConfig config) {
        this.key = new NamespacedKey(FurnitureAddon.inst(), config.getString(new String[] {"var", "variable"}));
    }

    @Override
    public boolean check(AbstractEntity abstractEntity) {
        if(MythicBukkit.inst().getCompatibility().getCrucible().get().isFurniture(abstractEntity)) {
            PersistentDataContainer pdc = abstractEntity.getDataContainer();
            return pdc.has(key, PersistentDataType.STRING)
                    || pdc.has(key, PersistentDataType.INTEGER)
                    || pdc.has(key, PersistentDataType.FLOAT);
        }
        else return false;
    }
}
