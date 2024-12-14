package cn.ksmcbrigade.at.utils;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.ArrayUtils;

public class ClientRegistry {
    public static void registerKey(KeyMapping... keyMappings) {
        for (KeyMapping keyMapping : keyMappings) {
            Minecraft.getInstance().options.keyMappings = ArrayUtils.add(Minecraft.getInstance().options.keyMappings, keyMapping);
        }
    }
}
