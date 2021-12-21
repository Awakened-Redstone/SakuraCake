package com.awakenedredstone.sakuracake.integration;

import net.minecraft.entity.Entity;
import virtuoel.pehkui.api.ScaleTypes;

public class Pehkui {

    public void scaleEntityModel(Entity target, float scale) {
        ScaleTypes.MODEL_HEIGHT.getScaleData(target).setScale(scale);
        ScaleTypes.MODEL_WIDTH.getScaleData(target).setScale(scale);
        ScaleTypes.EYE_HEIGHT.getScaleData(target).setScale(scale);
    }

    public void resetScale(Entity target) {
        ScaleTypes.MODEL_HEIGHT.getScaleData(target).resetScale();
        ScaleTypes.MODEL_WIDTH.getScaleData(target).resetScale();
        ScaleTypes.EYE_HEIGHT.getScaleData(target).resetScale();
    }
}
