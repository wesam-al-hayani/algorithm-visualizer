package dev.wesam.visualizer.ui;

import dev.wesam.visualizer.model.AlgorithmStep;
import java.util.List;
import java.util.function.BiConsumer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public final class VisualizationCanvas extends Region {
  private static final Color BACKGROUND = Color.web("#101827");
  private static final Color PANEL = Color.web("#182338");
  private static final Color NORMAL = Color.web("#5a78a6");
  private static final Color ACTIVE = Color.web("#ffb454");
  private static final Color SECONDARY = Color.web("#b58cff");
  private static final Color COMPLETE = Color.web("#54d6a0");
  private static final Color TEXT = Color.web("#eaf0fb");
  private final Canvas canvas = new Canvas();
  private AlgorithmStep step;
  private BiConsumer<Integer, Integer> gridClickHandler;

  public VisualizationCanvas() {
    getChildren().add(canvas);
    widthProperty().addListener((o, a, b) -> redraw());
    heightProperty().addListener((o, a, b) -> redraw());
    setMinSize(360, 300);
    canvas.setOnMouseClicked(
        event -> {
          int[] cell = gridCellAt(event.getX(), event.getY());
          if (cell != null && gridClickHandler != null) gridClickHandler.accept(cell[0], cell[1]);
        });
  }

  public void show(AlgorithmStep value) {
    step = value;
    redraw();
  }

  public AlgorithmStep currentStep() {
    return step;
  }

  public void setOnGridCellClicked(BiConsumer<Integer, Integer> handler) {
    gridClickHandler = handler;
  }

  @Override
  protected void layoutChildren() {
    canvas.setWidth(getWidth());
    canvas.setHeight(getHeight());
    redraw();
  }

  @Override
  protected double computePrefWidth(double h) {
    return 720;
  }

  @Override
  protected double computePrefHeight(double w) {
    return 520;
  }

  private void redraw() {
    GraphicsContext g = canvas.getGraphicsContext2D();
    double w = canvas.getWidth(), h = canvas.getHeight();
    g.setFill(BACKGROUND);
    g.fillRoundRect(0, 0, w, h, 18, 18);
    if (step == null) {
      g.setFill(TEXT);
      g.setFont(Font.font("System", FontWeight.SEMI_BOLD, 20));
      g.setTextAlign(TextAlignment.CENTER);
      g.fillText("Choose an algorithm and press Start", w / 2, h / 2);
      return;
    }
    switch (step.kind()) {
      case ARRAY -> drawArray(g, w, h);
      case GRAPH -> drawGraph(g, w, h);
      case TREE -> drawTree(g, w, h);
      case GRID -> drawGrid(g, w, h);
      case TABLE -> drawTable(g, w, h);
      case TEXT, SETS -> drawTiles(g, w, h);
    }
  }

  private void drawArray(GraphicsContext g, double w, double h) {
    List<Integer> a = step.values();
    if (a.isEmpty()) {
      drawCentered(g, w, h, "Empty input");
      return;
    }
    int max = a.stream().mapToInt(Math::abs).max().orElse(1);
    double gap = 6,
        left = 28,
        bottom = h - 52,
        usable = w - 56,
        bar = Math.max(3, (usable - gap * (a.size() - 1)) / a.size());
    for (int i = 0; i < a.size(); i++) {
      double height = Math.max(5, Math.abs(a.get(i)) / (double) Math.max(1, max) * (h - 110));
      double x = left + i * (bar + gap), y = bottom - height;
      g.setFill(color(i));
      g.fillRoundRect(x, y, bar, height, 8, 8);
      g.setFill(TEXT);
      g.setFont(Font.font(11));
      g.setTextAlign(TextAlignment.CENTER);
      g.fillText(Integer.toString(a.get(i)), x + bar / 2, bottom + 18);
    }
  }

  private void drawGraph(GraphicsContext g, double w, double h) {
    int n = step.labels().size();
    if (n == 0) {
      drawCentered(g, w, h, "Empty graph");
      return;
    }
    double cx = w / 2, cy = h / 2, radius = Math.max(70, Math.min(w, h) * .34);
    double[] x = new double[n], y = new double[n];
    for (int i = 0; i < n; i++) {
      double angle = -Math.PI / 2 + 2 * Math.PI * i / n;
      x[i] = cx + radius * Math.cos(angle);
      y[i] = cy + radius * Math.sin(angle);
    }
    g.setLineWidth(2);
    g.setTextAlign(TextAlignment.CENTER);
    g.setFont(Font.font(11));
    for (AlgorithmStep.VisualEdge e : step.edges()) {
      g.setStroke(Color.web("#52627b"));
      g.strokeLine(x[e.from()], y[e.from()], x[e.to()], y[e.to()]);
      double mx = (x[e.from()] + x[e.to()]) / 2, my = (y[e.from()] + y[e.to()]) / 2;
      g.setFill(Color.web("#a9b7cc"));
      if (!e.label().isBlank()) g.fillText(e.label(), mx, my - 5);
      if (e.directed()) drawArrow(g, x[e.from()], y[e.from()], x[e.to()], y[e.to()]);
    }
    for (int i = 0; i < n; i++) {
      g.setFill(color(i));
      g.fillOval(x[i] - 22, y[i] - 22, 44, 44);
      g.setStroke(Color.web("#dbe7fa"));
      g.strokeOval(x[i] - 22, y[i] - 22, 44, 44);
      g.setFill(Color.web("#0b1220"));
      g.setFont(Font.font("System", FontWeight.BOLD, 13));
      g.fillText(step.labels().get(i), x[i], y[i] + 5);
    }
  }

  private void drawArrow(GraphicsContext g, double x1, double y1, double x2, double y2) {
    double a = Math.atan2(y2 - y1, x2 - x1), ex = x2 - 25 * Math.cos(a), ey = y2 - 25 * Math.sin(a);
    g.setStroke(Color.web("#52627b"));
    g.strokeLine(ex, ey, ex - 9 * Math.cos(a - .55), ey - 9 * Math.sin(a - .55));
    g.strokeLine(ex, ey, ex - 9 * Math.cos(a + .55), ey - 9 * Math.sin(a + .55));
  }

  private void drawTree(GraphicsContext g, double w, double h) {
    List<Integer> a = step.values();
    if (a.isEmpty()) {
      drawCentered(g, w, h, "Empty structure");
      return;
    }
    int levels = 32 - Integer.numberOfLeadingZeros(a.size());
    double top = 52;
    boolean heap = step.details().contains("Array representation");
    double treeHeight = heap ? h - 185 : h - 100;
    for (int i = 0; i < a.size(); i++) {
      int level = 31 - Integer.numberOfLeadingZeros(i + 1),
          first = (1 << level) - 1,
          pos = i - first,
          count = 1 << level;
      double x = (pos + 1) * w / (count + 1),
          y = top + level * Math.min(90, treeHeight / Math.max(1, levels - 1));
      if (i > 0) {
        int p = (i - 1) / 2,
            pLevel = 31 - Integer.numberOfLeadingZeros(p + 1),
            pFirst = (1 << pLevel) - 1,
            pPos = p - pFirst,
            pCount = 1 << pLevel;
        double px = (pPos + 1) * w / (pCount + 1),
            py = top + pLevel * Math.min(90, treeHeight / Math.max(1, levels - 1));
        g.setStroke(Color.web("#52627b"));
        g.setLineWidth(2);
        g.strokeLine(px, py, x, y);
      }
      g.setFill(treeColor(i));
      g.fillOval(x - 22, y - 22, 44, 44);
      g.setStroke(Color.web("#dbe7fa"));
      g.strokeOval(x - 22, y - 22, 44, 44);
      g.setFill(
          step.details().contains("red-black") && step.complete().contains(i)
              ? Color.WHITE
              : Color.web("#0b1220"));
      g.setTextAlign(TextAlignment.CENTER);
      g.setFont(Font.font("System", FontWeight.BOLD, 12));
      g.fillText(Integer.toString(a.get(i)), x, y + 4);
    }
    if (heap) drawHeapArray(g, w, h, a);
  }

  private void drawGrid(GraphicsContext g, double w, double h) {
    int columns = columnsFromDetails();
    if (columns <= 0) columns = (int) Math.ceil(Math.sqrt(step.values().size()));
    int rows = (int) Math.ceil(step.values().size() / (double) columns);
    double cell = Math.min((w - 32) / columns, (h - 32) / rows),
        left = (w - cell * columns) / 2,
        top = (h - cell * rows) / 2;
    for (int i = 0; i < step.values().size(); i++) {
      int r = i / columns, c = i % columns;
      Color fill = step.values().get(i) == 1 ? Color.web("#070b12") : color(i);
      g.setFill(fill);
      g.fillRoundRect(left + c * cell + 2, top + r * cell + 2, cell - 4, cell - 4, 6, 6);
      String symbol = i < step.labels().size() ? step.labels().get(i) : ".";
      if (symbol.equals("S") || symbol.equals("T")) {
        g.setFill(TEXT);
        g.setTextAlign(TextAlignment.CENTER);
        g.setFont(Font.font("System", FontWeight.BOLD, Math.min(14, cell * .35)));
        g.fillText(symbol, left + (c + .5) * cell, top + (r + .62) * cell);
      }
    }
  }

  private int[] gridCellAt(double x, double y) {
    if (step == null || step.kind() != AlgorithmStep.VisualKind.GRID) return null;
    int columns = columnsFromDetails();
    if (columns <= 0) return null;
    int rows = (int) Math.ceil(step.values().size() / (double) columns);
    double cell = Math.min((canvas.getWidth() - 32) / columns, (canvas.getHeight() - 32) / rows),
        left = (canvas.getWidth() - cell * columns) / 2,
        top = (canvas.getHeight() - cell * rows) / 2;
    int column = (int) ((x - left) / cell), row = (int) ((y - top) / cell);
    return row >= 0 && column >= 0 && row < rows && column < columns
        ? new int[] {row, column}
        : null;
  }

  private void drawTable(GraphicsContext g, double w, double h) {
    int columns = columnsFromDetails();
    if (columns <= 0)
      columns = Math.max(1, (int) Math.ceil(Math.sqrt(Math.max(1, step.labels().size()))));
    int rows = (int) Math.ceil(step.labels().size() / (double) columns);
    double cellW = Math.min(72, (w - 32) / columns),
        cellH = Math.min(52, (h - 32) / Math.max(1, rows)),
        left = (w - cellW * columns) / 2,
        top = (h - cellH * rows) / 2;
    g.setTextAlign(TextAlignment.CENTER);
    g.setFont(Font.font(12));
    for (int i = 0; i < step.labels().size(); i++) {
      int r = i / columns, c = i % columns;
      g.setFill(color(i));
      g.fillRoundRect(left + c * cellW + 2, top + r * cellH + 2, cellW - 4, cellH - 4, 8, 8);
      g.setFill(TEXT);
      g.fillText(step.labels().get(i), left + (c + .5) * cellW, top + (r + .58) * cellH);
    }
  }

  private void drawTiles(GraphicsContext g, double w, double h) {
    List<String> items = step.labels();
    if (items.isEmpty()) {
      drawCentered(g, w, h, step.details());
      return;
    }
    int columns = Math.min(12, items.size()),
        rows = (int) Math.ceil(items.size() / (double) columns);
    double gap = 7,
        tile = Math.min(70, (w - 40 - gap * (columns - 1)) / columns),
        total = columns * tile + (columns - 1) * gap,
        left = (w - total) / 2,
        top = (h - rows * 67) / 2;
    g.setTextAlign(TextAlignment.CENTER);
    g.setFont(Font.font("System", FontWeight.SEMI_BOLD, 14));
    for (int i = 0; i < items.size(); i++) {
      double x = left + (i % columns) * (tile + gap), y = top + (i / columns) * 67;
      g.setFill(color(i));
      g.fillRoundRect(x, y, tile, 60, 10, 10);
      g.setFill(TEXT);
      g.fillText(items.get(i), x + tile / 2, y + 36);
    }
  }

  private void drawHeapArray(GraphicsContext g, double w, double h, List<Integer> values) {
    double cell = Math.min(46, (w - 50) / Math.max(1, values.size())),
        left = (w - cell * values.size()) / 2,
        top = h - 62;
    g.setFont(Font.font(11));
    g.setTextAlign(TextAlignment.CENTER);
    for (int i = 0; i < values.size(); i++) {
      g.setFill(color(i));
      g.fillRoundRect(left + i * cell + 2, top, cell - 4, 36, 5, 5);
      g.setFill(TEXT);
      g.fillText(Integer.toString(values.get(i)), left + (i + .5) * cell, top + 22);
      g.setFill(Color.web("#75859c"));
      g.fillText(Integer.toString(i), left + (i + .5) * cell, top + 50);
    }
  }

  private int columnsFromDetails() {
    String marker = "columns=";
    int at = step.details().lastIndexOf(marker);
    if (at < 0) {
      if (step.kind() == AlgorithmStep.VisualKind.GRID && step.details().contains("×")) {
        try {
          return Integer.parseInt(step.details().split("×")[1].trim().split(" ")[0]);
        } catch (Exception ignored) {
        }
      }
      return 0;
    }
    try {
      return Integer.parseInt(
          step.details().substring(at + marker.length()).trim().split("\\D")[0]);
    } catch (Exception ignored) {
      return 0;
    }
  }

  private Color color(int index) {
    if (step.active().contains(index)) return ACTIVE;
    if (step.secondary().contains(index)) return SECONDARY;
    if (step.complete().contains(index)) return COMPLETE;
    return NORMAL;
  }

  private Color treeColor(int index) {
    if (!step.details().contains("red-black")) return color(index);
    if (step.active().contains(index)) return ACTIVE;
    if (step.secondary().contains(index)) return Color.web("#ef6262");
    if (step.complete().contains(index)) return Color.web("#111827");
    return NORMAL;
  }

  private void drawCentered(GraphicsContext g, double w, double h, String text) {
    g.setFill(TEXT);
    g.setTextAlign(TextAlignment.CENTER);
    g.setFont(Font.font("System", 16));
    g.fillText(text == null ? "" : text, w / 2, h / 2, Math.max(0, w - 60));
  }
}
