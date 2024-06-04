package cannolicat.furnitureaddon.mechanics;

import cannolicat.furnitureaddon.FurnitureAddon;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;

public class SetFurnitureVariableMechanic implements ITargetedEntitySkill {
    protected final String key, type;
    protected final Object value;
    protected final Boolean temporary;

    public SetFurnitureVariableMechanic(MythicLineConfig config) {
        this.key = config.getString(new String[]{"variable", "var"});
        this.type = config.getString(new String[]{"type", "t"});
        this.temporary = config.getBoolean(new String[]{"temporary", "temp"}, false);

        switch (this.type.toUpperCase()) {
            case "INTEGER" ->
                this.value = config.getInteger(new String[] {"value", "v", "val"});
            case "FLOAT" ->
                this.value = config.getFloat(new String[] {"value", "v", "val"}, 1);
            case "STRING" ->
                this.value = config.getString(new String[] {"value", "v", "val"});
            default ->
                this.value = null;
        }
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity) {
        if(value == null || key == null || type == null) return SkillResult.INVALID_CONFIG;

        if(MythicBukkit.inst().getCompatibility().getCrucible().get().isFurniture(abstractEntity)) {
            NamespacedKey key = new NamespacedKey(FurnitureAddon.inst(), this.key);

            if (value instanceof Integer) {
                abstractEntity.getDataContainer().set(key, PersistentDataType.INTEGER, (Integer) value);
            } else if (value instanceof Float) {
                abstractEntity.getDataContainer().set(key, PersistentDataType.FLOAT, (Float) value);
            } else {
                abstractEntity.getDataContainer().set(key, PersistentDataType.STRING, (String) value);
            }

            if(temporary) {
                if(FurnitureAddon.inst().tempVars.containsKey(abstractEntity)) {
                    FurnitureAddon.inst().tempVars.get(abstractEntity).add(key);
                }
                else {
                    ArrayList<NamespacedKey> list = new ArrayList<>();
                    list.add(key);
                    FurnitureAddon.inst().tempVars.put(abstractEntity, list);
                }
            }

            return SkillResult.SUCCESS;
        }

        return SkillResult.INVALID_TARGET;
    }
}
