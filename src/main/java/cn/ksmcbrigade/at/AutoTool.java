package cn.ksmcbrigade.at;

import cn.ksmcbrigade.at.utils.ClientRegistry;
import cn.ksmcbrigade.at.utils.InventoryUtils;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.Objects;

import static cn.ksmcbrigade.at.utils.InventoryUtils.find;
import static cn.ksmcbrigade.at.utils.InventoryUtils.thanLast;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(AutoTool.MODID)
public class AutoTool
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "at";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public AutoTool(IEventBus modEventBus, ModContainer modContainer)
    {
        NeoForge.EVENT_BUS.register(this);
        modContainer.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
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
    public void tick(PlayerTickEvent event){
        Minecraft MC = Minecraft.getInstance();
        if(event.getEntity()!=MC.player) return;
        if(Config.ENABLED.get()){
            if(GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(),Minecraft.getInstance().options.keyAttack.getKey().getValue())==0 && Config.MOUSE_DOWN_ONLY.get()) return;
            HitResult hitResult = MC.hitResult;
            if((hitResult instanceof BlockHitResult blockHitResult)){
                BlockState state = event.getEntity().level().getBlockState(blockHitResult.getBlockPos());
                Block block = state.getBlock();
                String reg = Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(block)).getPath();
                Inventory inventory = event.getEntity().getInventory();
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
                if(entity.getTeam()!=null && event.getEntity().getTeam()!=null && entity.getTeam().equals(event.getEntity().getTeam()) && Config.BLOCK_TEAM.get()) return;
                int sword = -1;
                for (int i = 0; i < 9; i++) {
                    Item item = event.getEntity().getInventory().getItem(i).getItem();
                    if((item instanceof SwordItem) || (item instanceof AxeItem)){
                        if(sword==-1){
                            sword = i;
                        }
                        else{
                            if(thanLast(event.getEntity().getInventory().getItem(i).getItem(),event.getEntity().getInventory().getItem(sword).getItem())){
                                sword = i;
                            }
                        }
                    }
                }
                if(sword==-1) return;
                event.getEntity().getInventory().selected = sword;
            }
        }
    }
}
