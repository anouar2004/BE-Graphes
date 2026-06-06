package org.insa.graphs.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.insa.graphs.algorithm.shortestpath.Marathon;
import org.insa.graphs.gui.drawing.Drawing;
import org.insa.graphs.gui.drawing.Drawing.AlphaMode;
import org.insa.graphs.gui.drawing.DrawingClickListener;
import org.insa.graphs.gui.drawing.overlays.MarkerOverlay;
import org.insa.graphs.gui.drawing.overlays.PathOverlay;
import org.insa.graphs.model.Graph;
import org.insa.graphs.model.Node;
import org.insa.graphs.model.Path;
import org.insa.graphs.model.Point;

/**
 * Panel dédié à l'algorithme Marathon.
 * Permet de sélectionner S0 (clic sur la carte), L (distance en km) et N
 * (nombre de segments), puis de lancer le calcul.
 */
public class MarathonPanel extends JPanel implements DrawingClickListener {

    private static final long serialVersionUID = 1L;

    // --- Références extérieures ---
    private Graph graph;
    private Drawing drawing;

    // --- Nœud de départ sélectionné ---
    private Node selectedNode = null;
    private MarkerOverlay currentMarker = null;  // marqueur S0 sur la carte
    private PathOverlay currentPath = null;       // dernier parcours dessiné

    // --- Listeners pour lancer le thread ---
    private final List<ActionListener> runListeners = new ArrayList<>();

    // --- Widgets ---
    private final JLabel nodeLabel;
    private final JTextField distanceField;
    private final JButton runButton;
    private final JButton clearButton;
    private final JLabel resultLabel;

    public MarathonPanel(Component parent) {

        setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY),
                new EmptyBorder(8, 8, 8, 8)));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setVisible(false);

        // Titre
        JLabel title = new JLabel("Marathon");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setFont(title.getFont().deriveFont(title.getFont().getSize() + 2f));
        add(title);
        add(Box.createVerticalStrut(6));

        // --- Sélection du point de départ ---
        JLabel s0Title = new JLabel("Point de départ (clic sur la carte) :");
        s0Title.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(s0Title);

        nodeLabel = new JLabel("  Aucun point sélectionné");
        nodeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        nodeLabel.setForeground(Color.GRAY);
        add(nodeLabel);
        add(Box.createVerticalStrut(6));

        // --- Distance L ---
        JLabel distLabel = new JLabel("Distance cible L (km) :");
        distLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(distLabel);

        distanceField = new JTextField("42.195");
        distanceField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 28));
        distanceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(distanceField);
        add(Box.createVerticalStrut(6));

        // --- Nombre de segments N (constante interne) ---
        JLabel segLabel = new JLabel("Nombre de segments N : 4 (fixé)");
        segLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        segLabel.setForeground(Color.GRAY);
        add(segLabel);
        add(Box.createVerticalStrut(10));

        // --- Boutons ---
        JPanel buttonRow = new JPanel();
        buttonRow.setLayout(new BoxLayout(buttonRow, BoxLayout.X_AXIS));
        buttonRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        runButton = new JButton("Lancer");
        runButton.setEnabled(false);
        runButton.setFocusPainted(false);
        runButton.addActionListener(e -> onRun());

        clearButton = new JButton("Effacer");
        clearButton.setFocusPainted(false);
        clearButton.addActionListener(e -> onClear());

        buttonRow.add(runButton);
        buttonRow.add(Box.createHorizontalStrut(6));
        buttonRow.add(clearButton);
        add(buttonRow);
        add(Box.createVerticalStrut(8));

        // --- Label résultat ---
        resultLabel = new JLabel(" ");
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultLabel.setForeground(Color.DARK_GRAY);
        add(resultLabel);
    }

    // ------------------------------------------------------------------
    // Appelé quand l'utilisateur clique sur la carte
    // ------------------------------------------------------------------
    @Override
    public void mouseClicked(Point point) {
        if (!isVisible() || graph == null || drawing == null) return;

        // Trouver le nœud le plus proche du clic
        Node nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Node node : graph.getNodes()) {
            double d = point.distanceTo(node.getPoint());
            if (d < minDist) {
                minDist = d;
                nearest = node;
            }
        }
        if (nearest == null) return;

        selectedNode = nearest;
        nodeLabel.setText(String.format("  Nœud #%d (%.5f, %.5f)",
                selectedNode.getId(),
                selectedNode.getPoint().getLatitude(),
                selectedNode.getPoint().getLongitude()));
        nodeLabel.setForeground(Color.BLACK);

        // Supprimer l'ancien marqueur et en placer un nouveau
        if (currentMarker != null) {
            currentMarker.delete();   // delete() est la méthode de Overlay
        }
        currentMarker = drawing.drawMarker(selectedNode.getPoint(),
                Color.GREEN, Color.GREEN, AlphaMode.TRANSPARENT);

        runButton.setEnabled(true);
        resultLabel.setText(" ");
    }

    // ------------------------------------------------------------------
    // Lancement du marathon
    // ------------------------------------------------------------------
    private void onRun() {
        if (selectedNode == null || graph == null) return;

        double L;
        try {
            L = Double.parseDouble(distanceField.getText().replace(",", ".")) * 1000.0;
        } catch (NumberFormatException ex) {
            resultLabel.setText("Distance invalide !");
            resultLabel.setForeground(Color.RED);
            return;
        }
        if (L <= 0) {
            resultLabel.setText("La distance doit être > 0.");
            resultLabel.setForeground(Color.RED);
            return;
        }

        runButton.setEnabled(false);
        clearButton.setEnabled(false);
        resultLabel.setText("Calcul en cours…");
        resultLabel.setForeground(Color.DARK_GRAY);

        final Node s0    = selectedNode;
        final double finalL = L;

        new Thread(() -> {
            Path path = Marathon.dorun(graph, s0, finalL);

            javax.swing.SwingUtilities.invokeLater(() -> {
                runButton.setEnabled(true);
                clearButton.setEnabled(true);

                if (path == null) {
                    resultLabel.setText("Aucun parcours trouvé (INFEASIBLE).");
                    resultLabel.setForeground(Color.RED);
                } else {
                    double lengthKm  = path.getLength() / 1000.0;
                    double targetKm  = finalL / 1000.0;
                    double errorPct  = Math.abs(lengthKm - targetKm) / targetKm * 100.0;

                    resultLabel.setText(String.format(
                            "Parcours : %.2f km  (écart : %.1f %%)", lengthKm, errorPct));
                    resultLabel.setForeground(
                            errorPct <= 1.0 ? Color.GREEN.darker() : Color.ORANGE.darker());

                    // Effacer le précédent tracé s'il existe
                    if (currentPath != null) {
                        currentPath.delete();   // delete() de Overlay
                    }
                    currentPath = drawing.drawPath(path, Color.MAGENTA, true);

                    // Notifier les listeners (PathsPanel dans MainWindow)
                    for (ActionListener l : runListeners) {
                        l.actionPerformed(new ActionEvent(path,
                                ActionEvent.ACTION_PERFORMED, "marathon"));
                    }
                }
            });
        }).start();
    }

    // ------------------------------------------------------------------
    // Effacer la sélection et les overlays
    // ------------------------------------------------------------------
    private void onClear() {
        selectedNode = null;
        nodeLabel.setText("  Aucun point sélectionné");
        nodeLabel.setForeground(Color.GRAY);
        runButton.setEnabled(false);
        resultLabel.setText(" ");

        if (currentMarker != null) {
            currentMarker.delete();
            currentMarker = null;
        }
        if (currentPath != null) {
            currentPath.delete();
            currentPath = null;
        }
    }

    // ------------------------------------------------------------------
    // Setters appelés par MainWindow
    // ------------------------------------------------------------------
    public void setGraph(Graph graph) {
        this.graph = graph;
        onClear();
    }

    public void setDrawing(Drawing drawing) {
        this.drawing = drawing;
        onClear();
    }

    public void addRunListener(ActionListener l) {
        runListeners.add(l);
    }
}