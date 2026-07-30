package com.matcheww.workshop.controller;

import com.matcheww.workshop.model.Item;
import com.matcheww.workshop.model.ItemStack;
import com.matcheww.workshop.model.Slot;
import com.matcheww.workshop.view.DragGhostView;
import com.matcheww.workshop.view.SlotView;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Controller layer. The single, central handler for every drag gesture
 * across every SlotView in the application, regardless of which container
 * it belongs to - one class instead of duplicating drag logic per
 * container view.
 *
 * Belongs in the Controller layer: it validates whether a move is legal,
 * invokes the mutating Slot/CraftingTable methods that actually move an
 * ItemStack, and updates the affected SlotViews afterward.
 *
 * Does NOT belong in the Model layer: it contains no rules about what a
 * Slot or Item fundamentally is - it only orchestrates existing methods.
 * Does NOT belong in the View layer: it never constructs a JavaFX
 * Region/Label or sets a style itself - all rendering decisions stay
 * inside SlotView/DragGhostView's own methods.
 *
 * This class deliberately knows nothing about CraftingTable or Furnace by
 * name. Two small extension points keep it that way:
 * - addMoveListener(): any Controller that cares "did anything move" can
 *   subscribe and re-check its own slots, rather than this class knowing
 *   which slots belong to which feature.
 * - registerCraftingOutput(): the one genuinely special case (see its
 *   own comment below), expressed as a plugged-in Supplier rather than an
 *   instanceof check against CraftingTable.
 */
public class DragAndDropController {

    private final List<SlotView> registeredSlotViews = new ArrayList<>();
    private final DragGhostView dragGhost = new DragGhostView();
    private final List<Runnable> moveListeners = new ArrayList<>();
    private final Map<SlotView, Supplier<ItemStack>> craftingOutputActions = new HashMap<>();
    private final Map<SlotView, Runnable> craftingOutputCallbacks = new HashMap<>();

    private SlotView sourceSlotView;

    /** Mounts this controller's drag ghost into the given overlay layer. Must be called once, before any registration. */
    public void attachOverlay(Pane overlay) {
        overlay.getChildren().add(dragGhost);
    }

    /** Wires a SlotView's mouse gestures to this controller. Called once per SlotView, typically by ContainerView. */
    public void register(SlotView slotView) {
        registeredSlotViews.add(slotView);
        slotView.setOnMousePressed(event -> beginDrag(slotView, event));
        slotView.setOnMouseDragged(this::updateDrag);
        slotView.setOnMouseReleased(this::endDrag);
    }

    /** Notified after every successful move anywhere in the app. Cheap re-checks (e.g. "does the grid still match a recipe?") are expected on the other end. */
    public void addMoveListener(Runnable listener) {
        moveListeners.add(listener);
    }

    /**
     * Registers the crafting output slot's special pickup behavior: rather
     * than removing whatever ItemStack is currently sitting in the slot
     * (which is only ever a preview written by CraftingController, not
     * real consumed state), dragging out of this slot calls craftAction
     * (craftingTable::craft) to actually consume the grid, and afterCollect
     * (craftingController::onGridChanged) to refresh the preview for
     * whatever recipe, if any, the grid still matches afterward.
     */
    public void registerCraftingOutput(SlotView outputView, Supplier<ItemStack> craftAction, Runnable afterCollect) {
        registeredSlotViews.add(outputView);
        craftingOutputActions.put(outputView, craftAction);
        craftingOutputCallbacks.put(outputView, afterCollect);
        outputView.setOnMousePressed(event -> beginDrag(outputView, event));
        outputView.setOnMouseDragged(this::updateDrag);
        outputView.setOnMouseReleased(this::endDrag);
    }

    private void beginDrag(SlotView slotView, MouseEvent event) {
        if (slotView.getSlot().isEmpty()) {
            return;
        }
        sourceSlotView = slotView;
        dragGhost.showFor(slotView.getSlot().getItemStack(), event.getSceneX(), event.getSceneY());
    }

    private void updateDrag(MouseEvent event) {
        if (sourceSlotView == null) {
            return;
        }
        dragGhost.moveTo(event.getSceneX(), event.getSceneY());
    }

    private void endDrag(MouseEvent event) {
        if (sourceSlotView == null) {
            return;
        }
        dragGhost.hide();

        SlotView targetSlotView = findSlotViewAt(event.getSceneX(), event.getSceneY());
        if (targetSlotView != null && targetSlotView != sourceSlotView) {
            if (craftingOutputActions.containsKey(sourceSlotView)) {
                collectCraftingOutput(sourceSlotView, targetSlotView);
            } else {
                moveItem(sourceSlotView, targetSlotView);
                notifyMoveListeners();
            }
        }
        sourceSlotView = null;
    }

    private SlotView findSlotViewAt(double sceneX, double sceneY) {
        for (SlotView candidate : registeredSlotViews) {
            Bounds boundsInScene = candidate.localToScene(candidate.getBoundsInLocal());
            if (boundsInScene != null && boundsInScene.contains(sceneX, sceneY)) {
                return candidate;
            }
        }
        return null;
    }

    /**
     * Moves as much of the source stack into the target as fits, leaving
     * any remainder in the source - correct for a full move into an empty
     * slot, a partial merge that overflows, and a no-op when the target
     * holds an incompatible item or is already full. Computed from the
     * target's quantity before and after calling addItem(), since addItem
     * can partially succeed even when it returns false.
     */
    private void moveItem(SlotView sourceView, SlotView targetView) {
        Slot source = sourceView.getSlot();
        Slot target = targetView.getSlot();

        if (source.isEmpty()) {
            return;
        }

        ItemStack sourceStack = source.getItemStack();
        Item item = sourceStack.getItem();
        int beforeQuantity = quantityOf(target);

        target.addItem(item, sourceStack.getQuantity());

        int amountAbsorbed = quantityOf(target) - beforeQuantity;
        if (amountAbsorbed > 0) {
            source.removeItem(amountAbsorbed);
        }

        sourceView.refresh();
        targetView.refresh();
    }

    /**
     * Only crafts if the full result will fit in the target - crafting
     * consumes the grid immediately and irreversibly, so we must know the
     * result has somewhere to go before calling craftAction at all, or a
     * result that didn't fully fit would simply be destroyed.
     */
    private void collectCraftingOutput(SlotView sourceView, SlotView targetView) {
        Slot previewSlot = sourceView.getSlot();
        if (previewSlot.isEmpty()) {
            return;
        }

        ItemStack previewStack = previewSlot.getItemStack();
        Slot target = targetView.getSlot();

        if (!targetCanFullyAccept(target, previewStack)) {
            return;
        }

        ItemStack crafted = craftingOutputActions.get(sourceView).get();
        if (crafted == null) {
            return;
        }

        target.addItem(crafted.getItem(), crafted.getQuantity());
        targetView.refresh();

        craftingOutputCallbacks.get(sourceView).run();
    }

    private boolean targetCanFullyAccept(Slot target, ItemStack stack) {
        if (target.isEmpty()) {
            return stack.getQuantity() <= stack.getItem().getMaxStackSize();
        }
        ItemStack targetStack = target.getItemStack();
        if (!targetStack.getItem().equals(stack.getItem())) {
            return false;
        }
        return targetStack.getRemainingCapacity() >= stack.getQuantity();
    }

    private void notifyMoveListeners() {
        for (Runnable listener : moveListeners) {
            listener.run();
        }
    }

    private int quantityOf(Slot slot) {
        return slot.isEmpty() ? 0 : slot.getItemStack().getQuantity();
    }
}