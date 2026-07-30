package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class Stick extends Item {

    public Stick() {
        super("stick", "Stick");
    }

    @Override
    public String getDescription() {
        return "A simple wooden stick, used in many crafting recipes.";
    }
}
