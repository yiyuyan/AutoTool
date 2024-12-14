package cn.ksmcbrigade.at;

import cn.ksmcbrigade.at.utils.ClientRegistry;
import cn.ksmcbrigade.at.utils.InventoryUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import java.util.Objects;

import static cn.ksmcbrigade.at.utils.InventoryUtils.find;
import static cn.ksmcbrigade.at.utils.InventoryUtils.thanLast;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AutoTool.MODID)
public class AutoTool {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "at";

    public AutoTool() {
        MinecraftForge.EVENT_BUS.register(this);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT,Config.SPEC);
        ClientRegistry.registerKey(Config.ENABLED_KEY,Config.ENABLED_SWORD_KEY);
    }

    @SubscribeEvent
    public void keyInput(InputEvent.Key event){
        if(Config.ENABLED_KEY.isDown()){
            Config.ENABLED.set(!Config.ENABLED.get());
            if(Minecraft.getInstance().player!=null){
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("name.at.auto_tool").append(" : ").append(String.valueOf(Config.ENABLED.get())),true);
            }
        }
        if(Config.ENABLED_SWORD_KEY.isDown()){
            Config.ENABLED_SWORD.set(!Config.ENABLED_SWORD.get());
            if(Minecraft.getInstance().player!=null){
                Minecraft.getInstance().player.displayClientMessage(Component.translatable("name.at.auto_tool_sword").append(" : ").append(String.valueOf(Config.ENABLED_SWORD.get())),true);
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.PlayerTickEvent event){
        Minecraft MC = Minecraft.getInstance();
        if(event.player!=MC.player) return;
        if(Config.ENABLED.get()){
            if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(),Minecraft.getInstance().options.keyAttack.getKey().getValue())==0 && Config.MOUSE_DOWN_ONLY.get()) return;
            HitResult hitResult = MC.hitResult;
            if((hitResult instanceof BlockHitResult blockHitResult)){
                BlockState state = event.player.level().getBlockState(blockHitResult.getBlockPos());
                Block block = state.getBlock();
                String reg = Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(block)).getPath();
                Inventory inventory = event.player.getInventory();
                int result = -1;
                if(reg.toLowerCase().contains("ore") || reg.toLowerCase().contains("stone") || reg.toLowerCase().contains("granite") || reg.toLowerCase().contains("andesite") || reg.toLowerCase().contains("diorite")){
                    result = find(inventory, PickaxeItem.class);
                }
                else if(((block instanceof RotatedPillarBlock) && !(block instanceof HayBlock) && !(block instanceof ChainBlock)) || reg.toLowerCase().contains("wood") || reg.toLowerCase().contains("planks")){
                    result = find(inventory, AxeItem.class);
                }
                else if(block instanceof HayBlock || block instanceof FarmBlock){
                    result = find(inventory, HoeItem.class);
                }
                else if(block instanceof SnowyDirtBlock || block instanceof DirtPathBlock || block instanceof RootedDirtBlock || reg.toLowerCase().contains("dirt")){
                    result = find(inventory, ShovelItem.class);
                }
                else if(block instanceof WebBlock){
                    result = find(inventory, SwordItem.class);
                }
                else{
                    result = InventoryUtils.findBySpeed(inventory,state);
                }
                if(result!=-1){
                    inventory.selected = result;
                }
                else if(TieredItem.class.isAssignableFrom(inventory.getSelected().getItem().getClass())){
                    int slot = InventoryUtils.findNotToolItem(inventory);
                    if(slot!=-1){
                        inventory.selected = slot;
                    }
                }
            }
            else if(hitResult.getType().equals(HitResult.Type.ENTITY) && Config.ENABLED_SWORD.get()){
                Entity entity = ((EntityHitResult)hitResult).getEntity();
                if(entity==null) return;
                if(!entity.isAttackable()){
                    return;
                }
                if(entity.isInvulnerable()){
                    return;
                }
                if(!(entity instanceof LivingEntity) && Config.LIVING_ONLY.get()) return;
                if(entity instanceof Animal && Config.BLOCK_ANIMALS.get()) return;
                if(entity instanceof Monster && Config.BLOCK_MONSTERS.get()) return;
                if(entity instanceof Player && Config.BLOCK_PLAYERS.get()) return;
                if(entity instanceof LivingEntity living){
                    if((living.isFallFlying() || (living instanceof Player player1 && player1.getAbilities().flying)) && Config.BLOCK_FLIGHTS.get()){
                        return;
                    }
                    if(living.isSleeping() && Config.BLOCK_SLEEPING.get()){
                        return;
                    }
                }
                if(entity.getTeam()!=null && event.player.getTeam()!=null && entity.getTeam().equals(event.player.getTeam()) && Config.BLOCK_TEAM.get()) return;
                int sword = -1;
                for (int i = 0; i < 9; i++) {
                    Item item = event.player.getInventory().getItem(i).getItem();
                    if((item instanceof SwordItem) || (item instanceof AxeItem)){
                        if(sword==-1){
                            sword = i;
                        }
                        else{
                            if(thanLast(event.player.getInventory().getItem(i).getItem(),event.player.getInventory().getItem(sword).getItem())){
                                sword = i;
                            }
                        }
                    }
                }
                if(sword==-1) return;
                event.player.getInventory().selected = sword;
            }
        }
    }
}
