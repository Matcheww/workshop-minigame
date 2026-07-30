package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class Coal extends Item {

    public Coal() {
        super("coal", "Coal");
    }

    @Override
    public String getDescription() {
        return "Can be used as furnace fuel.";
    }
}
