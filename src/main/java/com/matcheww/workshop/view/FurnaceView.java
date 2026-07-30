package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.Furnace;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * View layer. Does NOT reuse ContainerView - a furnace is not a
 * rectangular grid, it is three individually-positioned slots (input
 * above fuel, output to the side) plus a progress indicator.
 *
 * The ProgressBar is a deliberate, justified use of a local JavaFX
 * property (owned by this View, not the Model): FurnaceController calls
 * updateProgress() with plain ints after each tick, and this class alone
 * decides how that becomes a visual fraction. The Model stays completely
 * unaware that JavaFX exists.
 */
public class FurnaceView extends VBox {

    private final SlotView inputSlotView;
    private final SlotView fuelSlotView;
    private final SlotView outputSlotView;
    private final ProgressBar progressBar;

    public FurnaceView(Furnace furnace, DragAndDropController dragAndDropController) {
        super(6);
        getStyleClass().add("furnace-view");
        setAlignment(Pos.CENTER);
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        inputSlotView = new SlotView(furnace.getInputSlot());
        fuelSlotView = new SlotView(furnace.getFuelSlot());
        outputSlotView = new SlotView(furnace.getOutputSlot());

        dragAndDropController.register(inputSlotView);
        dragAndDropController.register(fuelSlotView);
        dragAndDropController.register(outputSlotView);

        progressBar = new ProgressBar(0);
        progressBar.getStyleClass().add("smelt-progress-bar");
        progressBar.setPrefWidth(50);

        VBox inputFuelColumn = new VBox(6, inputSlotView, fuelSlotView);
        inputFuelColumn.setAlignment(Pos.CENTER);

        Label arrow = new Label("=>");
        arrow.getStyleClass().add("crafting-arrow-label");

        HBox row = new HBox(10, inputFuelColumn, progressBar, arrow, outputSlotView);
        row.setAlignment(Pos.CENTER);

        getChildren().add(row);
    }

    /** Called by FurnaceController after every tick, with plain data - never a Model type. */
    public void updateProgress(int smeltProgress, int smeltTime) {
        double fraction = smeltTime > 0 ? Math.min(1.0, (double) smeltProgress / smeltTime) : 0.0;
        progressBar.setProgress(fraction);
    }

    public void refreshSlots() {
        inputSlotView.refresh();
        fuelSlotView.refresh();
        outputSlotView.refresh();
    }
}