package com.matcheww.workshop.model.minecraftitems;

import com.matcheww.workshop.model.Item;

public class WoodenPlanks extends Item {

    public WoodenPlanks() {
        super("wooden_planks", "Wooden Planks");
    }

    @Override
    public String getDescription() {
        return "Planks cut from logs. A basic building and crafting material.";
    }
}
