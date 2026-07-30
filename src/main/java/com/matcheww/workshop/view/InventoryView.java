package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.Inventory;

/**
 * View layer. ContainerView configured for the 27-slot inventory (9 wide,
 * 3 rows), plus show()/hide() so it can be toggled open and closed.
 *
 * Starts hidden and unmanaged - setManaged(false) means a hidden inventory
 * takes up no layout space, rather than leaving an invisible gap where it
 * would otherwise sit.
 *
 * Visibility is plain presentation state with no Model impact whatsoever
 * (no Slot or Container is touched by opening or closing this panel), so
 * it is handled entirely here rather than routed through a Controller -
 * doing so would just add a round-trip for a boolean flip with no game
 * rule attached to it.
 */
public class InventoryView extends ContainerView {

    private static final int COLUMNS = 9;

    public InventoryView(Inventory inventory, DragAndDropController dragAndDropController) {
        super(inventory, COLUMNS, dragAndDropController);
        getStyleClass().add("inventory-view");
        setVisible(false);
        setManaged(false);
    }

    public void show() {
        setVisible(true);
        setManaged(true);
    }

    public void hide() {
        setVisible(false);
        setManaged(false);
    }
}