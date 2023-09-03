package dev.gallon.motorassistance.forge.adapters

import com.mrcrayfish.controllable.Controllable
import dev.gallon.motorassistance.common.interfaces.Block
import dev.gallon.motorassistance.common.interfaces.Minecraft
import dev.gallon.motorassistance.common.interfaces.Player
import net.minecraft.world.entity.Mob
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult
import net.minecraftforge.fml.ModList
import net.minecraft.client.Minecraft as ForgeMinecraft

class ForgeMinecraftAdapter(
    private val minecraft: ForgeMinecraft,
) : Minecraft {

    override fun getPlayer(): Player? = minecraft
        .player
        ?.let(::ForgePlayerAdapter)

    override fun attackKeyPressed(): Boolean =
        minecraft.options.keyAttack.isDown ||
            (
                ModList.get().isLoaded("controllable") &&
                    Controllable.getController()?.run { rTriggerValue > 0.0F } ?: false
                )

    override fun playerAimingMob(): Boolean =
        minecraft.crosshairPickEntity is Mob

    override fun getPointedBlock(maxRange: Double): Block? =
        minecraft
            .hitResult
            ?.let { target ->
                when (target) {
                    is EntityHitResult -> getPlayer()
                        ?.rayTrace(
                            maxRange,
                            target.entity.eyePosition.toPosition(),
                            target.entity.lookAngle.toRotation(),
                        )

                    is BlockHitResult -> ForgeBlockAdapter(target)
                    else -> null
                }
            }
}
