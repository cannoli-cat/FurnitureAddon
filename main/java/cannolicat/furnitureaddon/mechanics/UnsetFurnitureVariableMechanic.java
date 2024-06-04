package cannolicat.furnitureaddon.mechanics;

import cannolicat.furnitureaddon.FurnitureAddon;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;

public class UnsetFurnitureVariableMechanic implements ITargetedEntitySkill {
    protected final NamespacedKey key;

    public UnsetFurnitureVariableMechanic(MythicLineConfig config) {
        this.key = new NamespacedKey(FurnitureAddon.inst(), config.getString(new String[] {"variable", "var", "v"} ));
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if(key == null) return SkillResult.INVALID_CONFIG;

        if(MythicBukkit.inst().getCompatibility().getCrucible().get().isFurniture(abstractEntity)) {
            abstractEntity.getDataContainer().remove(key);
            if(FurnitureAddon.inst().tempVars.containsKey(abstractEntity) && FurnitureAddon.inst().tempVars.get(abstractEntity).contains(key)) {
                FurnitureAddon.inst().tempVars.get(abstractEntity).remove(key);
            }
            return SkillResult.SUCCESS;
        }
        return SkillResult.INVALID_TARGET;
    }
}
