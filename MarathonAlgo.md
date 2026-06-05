sujet choisi : Le marathon 

Contrainte : 
- a partir d'un point de départ, faire un parcour en forme de boucle 
- le parcours doit faire L km avec un incertitdue de 1% 
- on ne doit pas reppaser par les même chemin 

solution envisagé : 

Au lieu de directmement, la boucle, on va déoupe le trajet en plus petite parties.
Dans un pemier tmeps, on place a point intermédiant à L/N (N => le nombre de point intémadiaire)
puis via un Dijkstra ou A* on récupere le plus court chemin (une distance de L + esp)
et on recommancere N-1 fois 
Pour terminer, on refait un Dijkstra ou A* pour récuper le chemin sur le plus cours segment 

//------------------Algorimtm-------------------------\\

***Marathon***

entrée : 
- S0 le somment de déprt
- L la longeur du parcours 
- N le nombre de somment intermediaire 

Sorite : 
- solution un chemin formant une boucle 

// initialisation 

SommetDepart[N]
SommetDepart[0]
SommetAdmissible < - liste vide  //List des somment admissible 
SommetArriver = NULL
SommetUsed <- Ensemble vide
esp = L/(N * 10)
tol = esp/2
Rayon = L/N - esp
solution = NULL


pour i allant de 0 à N-1 : 
    // On regarde si on trouve des somment admissible sur le Rayon 
    SommetAdmissible <- SommetRayon(SommetDepart[i],S0, Rayon, tol,i,graphe)
    Si SommeAdmissible est vide 
        renvoyer une erreur
    // On choisi le prochain sommet intermediaire 
    Si( i = 0 )
        index = nombre aléatoire entre 0 et N-1
        SommetDepart[i + 1] = SommetAdmissible[index]
    Sinon
        pour j allant de 0 à taille(SommetAdmissible) - 1
            Si (SommetAdmissible[j] != SommetDepart[i - 1])
                SommetDepart[i + 1] = SommetAdmissible[j]
    solution += AStar(SommetDepart[i],SommetDepart[i + 1],SommentUsed) 
    SommentUsed <- Acr utlise dans solution 
Solution += AStar(SommetDepart[N - 1], S0,SommentUsed)
renvoyer Solution 

***SommetRayon***

entrée :
- Sc le sommetCourant 
- S0 le somment d'origine
- Rayon 
- Tolerance 
- i la i-eme itération 
- Graphe 
sortie : 
- SommetAdmissible la liste des sorite admissible
Si tol > Rayon 
    renvoyer SommetAdmissible
ListSommet <- liste vide 
Tol = tolerance
pour chaque sommet S dans le Graphe 
    distSc = distance(Sc, S)
    distS0 = distance(S0, S)
    Si (disSC >= Rayon - Tol et disSc <= Rayon + Tol)
        Si (disS0 >= Rayon - Tol et disS0 <= Rayon + Tol et i != 0)
            ajouter S à SommetAdmissible  
        sinon
            ajouter S à SommetAdmissible
Si SommetAdmissibles est vide 
    renvoyer SommetRayon(tol * 2) // on change la tolerance  
renvoyer SommetAdmissible


***Dijkstra (modification a apporter)***

entrée : 
- S0, Sd les sommet d'origine et de destination 
- ArcsInterdits ensembles des arcs déjà utliser 

...
pour chaque Arc arc depuis SommetCourant :
    Si arc appartient à  ArcsInterdits :
        continuer   
...