Projet "Le Desert Interdit"

Binôme:Tianwen GU et Hongfei ZHANG

Notre projet met en œuvre le jeu "Forbidden Desert", dans lequel les joueurs peuvent tirer des cartes de personnage, 
se déplacer dans le désert à la recherche de pièces détachées, tirer de l'équipement, utiliser leurs compétences et 
leur équipement pour leurs tâches afin de lutter contre les tempêtes du désert et finalement s'échapper.

Nous avons utilisé le framework MVC (modele, vue, controleur) pour réaliser ce projet. La partie principale du 
jeu est dans modele, vue fournit une interface pour interagir avec l'utilisateur, toutes les commandes seront 
passées par controleur et chaque fois que modele est changé, avec l'aide de l'interface Obsevator, nous 
pouvons mettre à jour l'affichage de l'interface dans vue afin que nous puissions interagir avec l'utilisateur 
en temps réel.

Notre modele se compose de deux parties principales : case et player, qui sont deux classes abstraites, les différents 
terrains héritent de la classe abstraite case et les différents personnages héritent de la classe abstraite player. 
Nous avons également deux classes abstraites, Carte_Tempete et Equipement, qui représentent les cartes météo 
et les cartes d'équipement que nous devons tirer pour modifier le modele au cours du jeu.


La répartition des tâches est la suivante : hongfei ZHANG est responsable du player, de l‘equipement et de ses sous-classes 
ainsi que du DVue et toutes les parties des vue, tandis que Tianwen GU est responsable du Case, de la Carte_Tempete 
et de ses sous-classes, du DModele et du DControleur.

Notre programme fait pratiquement tout, mais l'inconvénient est que lorsque l'on utilise des équipements ou des compétences 
d'alpiniste, il faut entrer les coordonnées manuellement au lieu de cliquer sur la carte, ce qui rend les choses un peu plus 
compliquées pour le joueur. De plus, les emplacements actuels que le joueur peut atteindre ne sont pas spécifiquement indiqués, 
de sorte que le joueur doit se familiariser avec les règles du jeu.

