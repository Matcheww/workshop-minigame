package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class IronHelmet extends Item {

    public IronHelmet() {
        super("iron_helmet", "Iron Helmet");
    }

    @Override
    public String getDescription() {
        return "Protects the player's head.";
    }
}