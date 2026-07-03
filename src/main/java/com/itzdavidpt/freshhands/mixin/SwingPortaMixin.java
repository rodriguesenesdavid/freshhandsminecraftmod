package com.itzdavidpt.freshhands.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.block.BlockState;
import net.minecraft.block.DoorBlock;
import net.minecraft.util.Hand;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class SwingPortaMixin {

    private float anguloAnimacao = 0.0f;

    @Inject(method = "renderArm", at = @At("HEAD"))
    private void aoRenderizarOBraço(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equivalence, float pitch, Arm arm, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        long windowHandle = client.getWindow().getHandle();
        boolean segurandoQ = GLFW.glfwGetKey(windowHandle, GLFW.GLFW_KEY_Q) == GLFW.GLFW_PRESS;
        double mouseXSpeed = client.mouse.getXVelocity(); 

        if (segurandoQ) {
            if (Math.abs(mouseXSpeed) > 1.0) {
                anguloAnimacao = Math.min(anguloAnimacao + 5.0f, 45.0f);
            }

            if (arm == Arm.RIGHT) {
                matrices.translate(-0.1F, 0.2F * (anguloAnimacao / 45.0f), -0.3F * (anguloAnimacao / 45.0f));
                matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-anguloAnimacao));
            }

            if (anguloAnimacao >= 30.0f) { 
                HitResult hit = client.crosshairTarget;
                if (hit != null && hit.getType() == HitResult.Type.BLOCK) {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    BlockState estadoBloco = client.world.getBlockState(blockHit.getBlockPos());

                    if (estadoBloco.getBlock() instanceof DoorBlock) {
                        if (!estadoBloco.get(DoorBlock.OPEN)) {
                            client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
                        }
                    }
                }
            }
        } else {
            anguloAnimacao = Math.max(anguloAnimacao - 5.0f, 0.0f);
        }
    }
}
