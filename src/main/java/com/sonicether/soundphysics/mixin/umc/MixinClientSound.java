package com.sonicether.soundphysics.mixin.umc;

import cam72cam.mod.MinecraftClient;
import cam72cam.mod.math.Vec3d;
import cam72cam.mod.resource.Identifier;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.sonicether.soundphysics.SoundPhysics;
import net.minecraft.util.SoundCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import paulscode.sound.SoundSystem;

import java.util.function.Supplier;

@Mixin(targets = "cam72cam.mod.sound.ClientSound", remap = false)
public abstract class MixinClientSound {
    @Shadow @Final private Identifier oggLocation;

    @Shadow public abstract void stop();

    @Shadow private String id;

    @Shadow abstract void init();

    @Shadow public abstract void setPosition(Vec3d pos);

    @Shadow public abstract void update();

    @Shadow @Final private boolean repeats;

    @Shadow private Vec3d currentPos;

    @Shadow @Final private Supplier<SoundSystem> sndSystem;

    @Shadow @Final private float attenuationDistance;

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    private void injectPlay(Vec3d pos, CallbackInfo ci) {
        stop();
        if (this.id == null)
            init();
        setPosition(pos);
        update();
        SoundPhysics.setLastSound(SoundCategory.AMBIENT, this.oggLocation.toString());
        if (this.repeats || this.currentPos == null || !MinecraftClient.isReady()) {
            this.sndSystem.get().play(this.id);
        } else if (MinecraftClient.getPlayer().getPosition().distanceTo(this.currentPos) < this.attenuationDistance * 1.1D * SoundPhysics.soundDistanceAllowance) {
            this.sndSystem.get().play(this.id);
        }
        ci.cancel();
    }

    // Commented code to change the position of the sound source depending on the scale of the train
    // Could be implemented but needs more work/proper positions for like the wheels and stuff
			/*toInject = new InsnList();

			toInject.add(new LdcInsnNode(1.75d));
			toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
			toInject.add(new FieldInsnNode(Opcodes.GETFIELD, "cam72cam/immersiverailroading/sound/ClientSound", "gauge",
					"Lcam72cam/immersiverailroading/library/Gauge;"));
			toInject.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "cam72cam/immersiverailroading/library/Gauge", "scale", "()D", false));
			toInject.add(new InsnNode(Opcodes.DMUL));
			toInject.add(new InsnNode(Opcodes.DADD));

			// Target method: update
			bytes = patchMethodInClass(obfuscated, bytes, "update", "()V", Opcodes.INVOKESPECIAL,
					AbstractInsnNode.METHOD_INSN, "<init>", "(ILjava/lang/String;FFF)V", -1, toInject, true, 0, 0, false, -5, -1);*/
    @Inject(method = "update", at = @At(value = "INVOKE", target = "Lpaulscode/sound/SoundSystem;CommandQueue(Lpaulscode/sound/CommandObject;)Z", ordinal = 0))
    private void injectGlobalVolume(CallbackInfo ci, @Local LocalFloatRef vol) {
        vol.set(vol.get() * SoundPhysics.globalVolumeMultiplier0);
    }
}
