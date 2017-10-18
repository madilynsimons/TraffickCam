package com.example.owner.traffickcam;

import java.io.Serializable;

import io.fotoapparat.result.PhotoResult;

public class SerializablePhoto implements Serializable{
    PhotoResult Result;
    public SerializablePhoto()
    {
        Result = null;
    }
}
