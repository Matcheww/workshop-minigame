package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class IronLeggings extends Item {

    public IronLeggings() {
        super("iron_leggings", "Iron Leggings");
    }

    @Override
    public String getDescription() {
        return "Protects the player's legs.";
    }
}