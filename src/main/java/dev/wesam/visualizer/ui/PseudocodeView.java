package dev.wesam.visualizer.ui;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/** A selectable, line-oriented pseudocode view with a proper active-line background. */
public final class PseudocodeView extends ListView<String> {
  private int activeLine = -1;

  public PseudocodeView() {
    getStyleClass().add("pseudocode-view");
    setFocusTraversable(true);
    setCellFactory(
        ignored ->
            new ListCell<>() {
              @Override
              protected void updateItem(String line, boolean empty) {
                super.updateItem(line, empty);
                setText(empty ? null : line);
                getStyleClass().remove("pseudocode-active");
                if (!empty && getIndex() == activeLine) getStyleClass().add("pseudocode-active");
              }
            });
    addEventFilter(KeyEvent.KEY_PRESSED, this::copySelectedLine);
  }

  public void showCode(String pseudocode, int highlightedLine) {
    String code = pseudocode == null ? "" : pseudocode;
    getItems().setAll(code.split("\\R", -1));
    activeLine = highlightedLine;
    refresh();
    if (activeLine >= 0 && activeLine < getItems().size()) scrollTo(activeLine);
  }

  public int activeLine() {
    return activeLine;
  }

  private void copySelectedLine(KeyEvent event) {
    if (!event.isShortcutDown() || event.getCode() != KeyCode.C) return;
    String selected = getSelectionModel().getSelectedItem();
    if (selected == null) return;
    ClipboardContent content = new ClipboardContent();
    content.putString(selected);
    Clipboard.getSystemClipboard().setContent(content);
    event.consume();
  }
}
