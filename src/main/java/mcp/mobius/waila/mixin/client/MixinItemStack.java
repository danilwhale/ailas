package mcp.mobius.waila.mixin.client;

import mcp.mobius.waila.Waila;
import mcp.mobius.waila.utils.ModIdentification;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipFlag;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getTooltip", at = @At("TAIL"))
    private void appendModName(PlayerEntity player, TooltipFlag flag, CallbackInfoReturnable<List<Text>> cir) {
        List<Text> components = cir.getReturnValue();
        ItemStack stack = (ItemStack) (Object) this;
        components.add(new LiteralText(String.format(Waila.CONFIG.get().getFormatting().getModName(), ModIdentification.getModInfo(stack.getItem()).getName())));
    }
}
