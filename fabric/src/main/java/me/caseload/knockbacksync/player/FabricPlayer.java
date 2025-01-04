package me.caseload.knockbacksync.player;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.protocol.world.BoundingBox;
import com.github.retrooper.packetevents.protocol.world.Location;
import com.github.retrooper.packetevents.util.Vector3d;
import me.caseload.knockbacksync.world.FabricWorld;
import me.caseload.knockbacksync.world.PlatformWorld;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class FabricPlayer implements PlatformPlayer {
    public final ServerPlayer fabricPlayer;
    private String clientBrand = "fabric";

    public FabricPlayer(ServerPlayer player) {
        this.fabricPlayer = player;
    }

    @Override
    public UUID getUUID() {
        return fabricPlayer.getUUID();
    }

    @Override
    public String getName() {
        return fabricPlayer.getName().getString();
    }

    @Override
    public double getX() {
        return fabricPlayer.getX();
    }

    @Override
    public double getY() {
        return fabricPlayer.getY();
    }

    @Override
    public double getZ() {
        return fabricPlayer.getZ();
    }

    @Override
    public float getPitch() {
        return fabricPlayer.getXRot();
    }

    @Override
    public float getYaw() {
        return fabricPlayer.getYRot();
    }

    @Override
    public boolean isOnGround() {
        return fabricPlayer.onGround();
    }

    @Override
    public int getPing() {
        return fabricPlayer.connection.latency();
    }

    @Override
    public boolean isGliding() {
        return fabricPlayer.isFallFlying();
    }

    @Override
    public PlatformWorld getWorld() {
        return new FabricWorld(fabricPlayer.level());
    }

    @Override
    public Vector3d getLocation() {
        return new Vector3d(fabricPlayer.getX(), fabricPlayer.getY(), fabricPlayer.getZ());
    }

    @Override
    public void sendMessage(@NotNull String s) {
        fabricPlayer.sendSystemMessage(Component.literal(s));
    }

    @Override
    public double getAttackCooldown() {
        // this is what paper does I have no idea how this works
        return fabricPlayer.getAttackStrengthScale(0.5f);
    }

    @Override
    public boolean isSprinting() {
        return fabricPlayer.isSprinting();
    }

    @Override
    public int getMainHandKnockbackLevel() {
        Optional<Holder.Reference<Enchantment>>
                entry = VanillaRegistries.createLookup().asGetterLookup().get(
                Registries.ENCHANTMENT, Enchantments.KNOCKBACK
        );
        Holder<Enchantment> registryEntry1 = entry.orElseThrow(); // Reference implements RegistryEntry, this is fine
        return EnchantmentHelper.getItemEnchantmentLevel(registryEntry1, fabricPlayer.getMainHandItem());
    }

    @Override
    public @Nullable Integer getNoDamageTicks() {
        return fabricPlayer.invulnerableTime;
    }

    @Override
    public void setVelocity(Vector3d adjustedVelocity) {
        fabricPlayer.setDeltaMovement(adjustedVelocity.x, adjustedVelocity.y, adjustedVelocity.z);
        // TODO
        // fix paper-ism? for some reason setVelocity() in paper marks the entity as hurt marked every time its called?
        fabricPlayer.hurtMarked = true;
    }

    @Override
    public Vector3d getVelocity() {
        final Vec3 fabricVelocity = fabricPlayer.getDeltaMovement();
        return new Vector3d(fabricVelocity.x, fabricVelocity.y, fabricVelocity.z);
    }

    @Override
    public double getJumpPower() {
        double jumpVelocity = 0.42;
        MobEffectInstance jumpEffect = fabricPlayer.getEffect(MobEffects.JUMP);
        if (jumpEffect != null) {
            int amplifier = jumpEffect.getAmplifier();
            jumpVelocity += (amplifier + 1) * 0.1F;
        }

        return jumpVelocity;
    }
    @Override
    public BoundingBox getBoundingBox() {
        AABB boundingBox = fabricPlayer.getBoundingBox();
        return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ, boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
    }

    @Override
    public User getUser() {
        return PacketEvents.getAPI().getPlayerManager().getUser(fabricPlayer);
    }

    @Override
    public void setClientBrand(String brand) {
        this.clientBrand = brand;
    }

    @Override
    public String getClientBrand() {
        return this.clientBrand;
    }
}
