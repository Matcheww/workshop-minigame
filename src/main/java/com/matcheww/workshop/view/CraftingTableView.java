package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.CraftingTable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/**
 * View layer. Composes the 3x3 crafting grid (a ContainerView) with a
 * standalone output SlotView - the output slot is a separate Slot object
 * on CraftingTable, not part of its own slot list, so it is wired
 * independently here rather than through ContainerView.
 *
 * The output SlotView is deliberately NOT registered for dragging in this
 * constructor. Collecting a craft result needs special handling (calling
 * craftingTable.craft() rather than a plain removeItem), which requires a
 * CraftingController that does not exist yet at this point in
 * construction - GameApplication wires that registration afterward.
 */
public class CraftingTableView extends HBox {

    private final ContainerView gridView;
    private final SlotView outputSlotView;

    public CraftingTableView(CraftingTable craftingTable, DragAndDropController dragAndDropController) {
        super(12);
        getStyleClass().add("crafting-table-view");
        setAlignment(Pos.CENTER);

        gridView = new ContainerView(craftingTable, CraftingTable.COLS, dragAndDropController);

        Label arrow = new Label("=>");
        arrow.getStyleClass().add("crafting-arrow-label");

        outputSlotView = new SlotView(craftingTable.getOutputSlot());

        getChildren().addAll(gridView, arrow, outputSlotView);
    }

    public ContainerView getGridView() {
        return gridView;
    }

    public SlotView getOutputSlotView() {
        return outputSlotView;
    }
}