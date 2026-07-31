package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class Charcoal extends Item {

    public Charcoal() {
        super("charcoal", "Charcoal");
    }

    @Override
    public String getDescription() {
        return "Alternative to coal as furnace fuel.";
    }
}
