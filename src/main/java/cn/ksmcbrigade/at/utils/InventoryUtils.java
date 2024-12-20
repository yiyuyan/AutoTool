package cn.ksmcbrigade.at.utils;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;

public class InventoryUtils {
    public static int find(Inventory inventory, Class<? extends Item> target){
        int result = -1;
        for (int i = 0; i < 9; i++) {
            Item item = inventory.getItem(i).getItem();
            if(item.getClass().equals(target)){
                if(result==-1 || thanLast(item,inventory.getItem(result).getItem())){
                    result = i;
                }
            }
        }
        return result;
    }

    public static int findBySpeed(Inventory inventory, BlockState state){
        ItemRecord record = new ItemRecord(Items.AIR.getDefaultInstance(),-1,-1);
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getItem(i);
            float speed = item.getDestroySpeed(state);
            if(speed>record.speed){
                record = new ItemRecord(item,i,speed);
            }
        }
        return record.slot;
    }

    public static int findNotToolItem(Inventory inventory){
        for (int i = 0; i < 9; i++) {
            ItemStack item = inventory.getItem(i);
            if(item.isEmpty() || !item.getItem().getClass().equals(TieredItem.class)){
                return i;
            }
        }
        return -1;
    }

    public static boolean thanLast(Item now,Item last){
        Tier tier = ((TieredItem)now).getTier();
        Tier lastTier = ((TieredItem)last).getTier();
        if(tier.equals(Tiers.NETHERITE)){
            return true;
        }
        else if(tier.equals(Tiers.DIAMOND) && lastTier.equals(Tiers.NETHERITE)){
            return false;
        }
        else if(tier.equals(Tiers.IRON) && (lastTier.equals(Tiers.NETHERITE) || lastTier.equals(Tiers.DIAMOND))){
            return false;
        }
        else if(tier.equals(Tiers.STONE) && (lastTier.equals(Tiers.NETHERITE) || lastTier.equals(Tiers.DIAMOND) || lastTier.equals(Tiers.IRON))){
            return false;
        }
        else if(tier.equals(Tiers.GOLD) && (lastTier.equals(Tiers.NETHERITE) || lastTier.equals(Tiers.DIAMOND) || lastTier.equals(Tiers.IRON) || lastTier.equals(Tiers.STONE))){
            return false;
        }
        else return !tier.equals(Tiers.GOLD) && !tier.equals(Tiers.WOOD);
    }

    private record ItemRecord(ItemStack item, int slot, float speed){}
}
