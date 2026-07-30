package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.Hotbar;

/**
 * View layer. Thin, named configuration of ContainerView for a Hotbar - 9
 * slots, one row.
 *
 * Extends ContainerView rather than composing one: a HotbarView genuinely
 * IS a container view laid out for a Hotbar, with no extra behavior of its
 * own to justify a wrapping object (unlike InventoryView, which adds real
 * show/hide behavior on top).
 */
public class HotbarView extends ContainerView {

    private static final int COLUMNS = 9;

    public HotbarView(Hotbar hotbar, DragAndDropController dragAndDropController) {
        super(hotbar, COLUMNS, dragAndDropController);
        getStyleClass().add("hotbar-view");
    }
}