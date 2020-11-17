package com.digitalpersona.uareu.UareUSampleJava;

import com.digitalpersona.uareu.Fmd;

public class MyFMD implements Fmd {
    @Override
    public int getCbeffId() {
        return 0;
    }

    @Override
    public int getCaptureEquipmentCompliance() {
        return 0;
    }

    @Override
    public int getCaptureEquipmentId() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getResolution() {
        return 0;
    }

    @Override
    public int getViewCnt() {
        return 0;
    }

    @Override
    public Format getFormat() {
        return null;
    }

    @Override
    public Fmv[] getViews() {
        return new Fmv[0];
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }
}
