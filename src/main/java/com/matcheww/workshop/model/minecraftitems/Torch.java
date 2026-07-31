package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class Torch extends Item {

    public Torch() {
        super("torch", "Torch");
    }

    @Override
    public String getDescription() {
        return "Can be used as a light source.";
    }
}
