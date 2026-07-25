package com.matcheww.workshop.model;

public class OakLog extends Item {

    public OakLog() {
        super("oak_log", "Oak Log");
    }

    @Override
    public String getDescription() {
        return "A log harvested from an oak tree. Can be crafted into planks.";
    }
}
