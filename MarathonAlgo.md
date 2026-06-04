sujet choisi : Le marathon 

Contrainte : 
- a partir d'un point de départ, faire un parcour en forme de boucle 
- le parcours doit faire L km
- on ne doit pas reppaser par les même chemin 

solution envisagé : 

Au lieu de directmement, la boucle, on va déoupe le trajet en plus petite parties.
Dans un pemier tmeps, on place a point intermédiant à L/N (N => le nombre de point intémadiaire)
puis via un Dijkstra ou A* on récupere le plus court chemin (une distance de L + esp)
et on recommancere N-1 fois 
POout terminer, on refait un Dijkstra ou A* pour récuper le chemin sur le plus cours segment 