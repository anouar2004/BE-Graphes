package org.insa.graphs.gui.observers;
 
import java.awt.Color;
 
import org.insa.graphs.gui.drawing.Drawing;
 
/**
 * Observer graphique pour A* — utilise des couleurs distinctes de Dijkstra.
 *
 * Dijkstra : CYAN  (noeuds atteints) / BLUE  (noeuds marqués)
 * A*        : ORANGE (noeuds atteints) / RED   (noeuds marqués)
 */
public class AStarGraphicObserver extends ShortestPathGraphicObserver {
 
    public AStarGraphicObserver(Drawing drawing) {
        // On appelle le constructeur parent pour initialiser drawing,
        // puis on remplace les deux overlays par les couleurs A*.
        super(drawing);
        psOverlay1 = drawing.createPointSetOverlay(1, Color.BLUE);
        psOverlay2 = drawing.createPointSetOverlay(1, Color.RED);
    }
}