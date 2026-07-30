package com.matcheww.workshop.view;

import com.matcheww.workshop.controller.DragAndDropController;
import com.matcheww.workshop.model.Container;
import com.matcheww.workshop.model.Slot;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * View layer. A reusable grid of SlotViews for any Container, given a
 * column count. Hotbar (9, one row), Inventory (27, three rows),
 * ItemContainer (54, six rows), and the crafting grid (3x3) all reuse this
 * one class instead of four near-identical grid-building implementations.
 *
 * Belongs in the View layer: it only lays out SlotViews and asks the given
 * DragAndDropController to register each one - the same "forward to a
 * controller" pattern SlotView itself follows, one level up.
 *
 * Does NOT belong in the Controller layer: it never inspects or mutates
 * the Container's Slots itself, only reads getSlots() once to build the
 * grid.
 */
public class ContainerView extends GridPane {

    private final List<SlotView> slotViews = new ArrayList<>();

    public ContainerView(Container container, int columns, DragAndDropController dragAndDropController) {
        getStyleClass().add("container-view");
        setHgap(4);
        setVgap(4);
        // A GridPane is resizable and has no max size cap by default, so a
        // parent that stretches its children (e.g. an HBox, or BorderPane's
        // center region) would otherwise stretch this grid taller/wider
        // than its rows actually need, leaving its content anchored at the
        // top-left of the extra space instead of where a sibling node's
        // own alignment expects it to be.
        setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        List<Slot> slots = container.getSlots();
        for (int i = 0; i < slots.size(); i++) {
            SlotView slotView = new SlotView(slots.get(i));
            dragAndDropController.register(slotView);
            slotViews.add(slotView);

            int row = i / columns;
            int col = i % columns;
            add(slotView, col, row);
        }
    }

    /** Refreshes every slot in the grid; useful after a bulk Model change (e.g. a completed craft). */
    public void refreshAll() {
        for (SlotView slotView : slotViews) {
            slotView.refresh();
        }
    }
}