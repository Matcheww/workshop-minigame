package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.ItemContainer;

/**
 * View layer. Thin, named configuration of ContainerView for an
 * ItemContainer - 54 slots, laid out 9 wide (6 rows) to match the visual
 * width of the hotbar and inventory panels beneath it.
 *
 * Column count (9) is a View-layer layout decision, independent of
 * ItemContainer.SIZE (the Model's total slot count) - they are unrelated
 * numbers that happen to divide evenly here.
 */
public class ItemContainerView extends ContainerView {

    private static final int COLUMNS = 9;

    public ItemContainerView(ItemContainer itemContainer, DragAndDropController dragAndDropController) {
        super(itemContainer, COLUMNS, dragAndDropController);
        getStyleClass().add("item-container-view");
    }
}