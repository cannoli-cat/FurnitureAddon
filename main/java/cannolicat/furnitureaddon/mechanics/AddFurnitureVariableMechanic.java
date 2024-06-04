package cannolicat.furnitureaddon.mechanics;

import cannolicat.furnitureaddon.FurnitureAddon;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class AddFurnitureVariableMechanic implements ITargetedEntitySkill {
    protected final NamespacedKey key;
    protected final float amount;

    public AddFurnitureVariableMechanic(MythicLineConfig config) {
        this.key = new NamespacedKey(FurnitureAddon.inst(), config.getString(new String[] { "variable", "v", "var"}));
        this.amount = config.getFloat(new String[] { "amount", "a" }, 1);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if(key == null) return SkillResult.INVALID_CONFIG;

        if(MythicBukkit.inst().getCompatibility().getCrucible().get().isFurniture(abstractEntity)) {
            PersistentDataContainer pdc = abstractEntity.getDataContainer();

            if (pdc.has(key, PersistentDataType.STRING))
                return SkillResult.INVALID_CONFIG;
            else if (pdc.has(key, PersistentDataType.INTEGER))
                pdc.set(key, PersistentDataType.INTEGER, pdc.get(key, PersistentDataType.INTEGER) + Math.round(amount));
            else if (pdc.has(key, PersistentDataType.FLOAT))
                pdc.set(key, PersistentDataType.FLOAT, pdc.get(key, PersistentDataType.FLOAT) + amount);
            else
                return SkillResult.CONDITION_FAILED;

            return SkillResult.SUCCESS;
        }
        return SkillResult.INVALID_TARGET;
    }
}
