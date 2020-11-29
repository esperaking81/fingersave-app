package com.digitalpersona.uareu.UareUSampleJava;

import com.digitalpersona.uareu.Fmd;

import java.io.Serializable;

public class MyFmd implements Serializable {

    // private Fmd.Format m_format;
    // private byte[] m_data;

    public MyFmd(int cbeffid, int width, int height, int resolution) {
        this.cbeffid = cbeffid;
        this.width = width;
        this.height = height;
        this.resolution = resolution;
        // this.m_format = m_format;
        // this.m_data = m_data;
    }

    public int cbeffid;
    public int width;
    public int height;
    public int resolution;
}
