package cn.ksmcbrigade.at;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue ENABLED = builder.define("enable",false);
    public static final ModConfigSpec.BooleanValue ENABLED_SWORD = builder.define("enable_sword",true);

    public static final ModConfigSpec.BooleanValue MOUSE_DOWN_ONLY = builder.define("mouse_down_only",true);

    public static final ModConfigSpec.BooleanValue BLOCK_FLIGHTS = builder.comment("When the enable_sword enabled,these configurations will take effects").define("block_flights",true);
    public static final ModConfigSpec.BooleanValue BLOCK_ANIMALS = builder.define("block_animals",false);
    public static final ModConfigSpec.BooleanValue BLOCK_MONSTERS = builder.define("block_monsters",false);
    public static final ModConfigSpec.BooleanValue BLOCK_PLAYERS = builder.define("block_players",false);
    public static final ModConfigSpec.BooleanValue BLOCK_SLEEPING = builder.define("block_sleeping",true);
    public static final ModConfigSpec.BooleanValue BLOCK_TEAM = builder.define("block_team",true);
    public static final ModConfigSpec.BooleanValue LIVING_ONLY = builder.define("living_only",false);

    public static final ModConfigSpec SPEC = builder.build();

    public static final KeyMapping ENABLED_KEY = new KeyMapping("key.at.enable", InputConstants.KEY_F6,KeyMapping.CATEGORY_GAMEPLAY);
    public static final KeyMapping ENABLED_SWORD_KEY = new KeyMapping("key.at.enable_sword", InputConstants.KEY_F7,KeyMapping.CATEGORY_GAMEPLAY);
}
