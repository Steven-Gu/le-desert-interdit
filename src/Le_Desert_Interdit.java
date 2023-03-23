import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Le_Desert_Interdit{
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            /** Voici le contenu qui nous intéresse. */
            DModele modele = new DModele();
            DVue vue = new DVue(modele);
        });
    }

}

interface Observer{
    public void update();
}

abstract class Observable {
    /**
     * On a une liste [observers] d'observateurs, initialement vide, à laquelle
     * viennent s'inscrire les observateurs via la méthode [addObserver].
     */
    private ArrayList<Observer> observers;
    public Observable() {
        this.observers = new ArrayList<Observer>();
    }
    public void addObserver(Observer o) {
        observers.add(o);
    }

    /**
     * Lorsque l'état de l'objet observé change, il est convenu d'appeler la
     * méthode [notifyObservers] pour prévenir l'ensemble des observateurs
     * enregistrés.
     * On le fait ici concrètement en appelant la méthode [update] de chaque
     * observateur.
     */
    public void notifyObservers() {
        for(Observer o : observers) {
            o.update();
        }
    }
}

class DModele extends Observable {
    /**
     * On fixe la taille de la grille.
     */
    public static final int HAUTEUR = 5, LARGEUR = 5;
    /**
     * On stocke un tableau de cellules.
     */
    private Case[][] cases;
    protected static int sableReste = 48;
    /**
     * Construction : on initialise un tableau de cases.
     */
    public DModele() {
        /**
         * Pour éviter les problèmes aux bords, on ajoute une ligne et une
         * colonne de chaque côté, dont les cellules n'évolueront pas.
         */
        cases = new Case[LARGEUR][HAUTEUR];
        for (int i = 0; i < LARGEUR; i++) {
            for (int j = 0; j < HAUTEUR; j++) {
                cases[i][j] = new Case(this, i, j);
            }
        }
        init();
    }
    public void init(){
        cases[2][2].EstTempete = true;
        cases[0][2].ajouteSable();
        cases[1][1].ajouteSable();
        cases[1][3].ajouteSable();
        cases[2][0].ajouteSable();
        cases[2][4].ajouteSable();
        cases[3][1].ajouteSable();
        cases[3][3].ajouteSable();
        cases[4][2].ajouteSable();
    }
    public Case getCase (int x, int y){return cases[x][y];}
}

class Case {
    /** On conserve un pointeur vers la classe principale du modèle. */
    private DModele modele;
    protected boolean EstTempete = false;
    protected int sable = 0;
    private final int x, y;
    public Case(DModele modele, int x, int y) {
        this.modele = modele;
        this.x = x;
        this.y = y;
    }

    public boolean EstTempete(){return this.EstTempete;}
    public boolean ajouteSable(){
        if (modele.sableReste > 0){
            this.modele.sableReste--;
            this.sable++;
            return true;
        }else{
            return false;
        }
    }
    public boolean retireSable(){
        if(this.sable > 0){
            this.sable--;
            this.modele.sableReste++;
            return true;
        }else{
            return false;
        }
    }
    public int getSable(){return this.sable;}

}

class DVue {
    /**
     * JFrame est une classe fournie pas Swing. Elle représente la fenêtre
     * de l'application graphique.
     */
    private JFrame frame;
    /**
     * VueGrille et VueCommandes sont deux classes définies plus loin, pour
     * nos deux parties de l'interface graphique.
     */
    private VueGrille grille;

    /** Construction d'une vue attachée à un modèle. */
    public DVue(DModele modele) {
        /** Définition de la fenêtre principale. */
        frame = new JFrame();
        frame.setTitle("Le Desert Interdit");
        /**
         * On précise un mode pour disposer les différents éléments à
         * l'intérieur de la fenêtre. Quelques possibilités sont :
         *  - BorderLayout (défaut pour la classe JFrame) : chaque élément est
         *    disposé au centre ou le long d'un bord.
         *  - FlowLayout (défaut pour un JPanel) : les éléments sont disposés
         *    l'un à la suite de l'autre, dans l'ordre de leur ajout, les lignes
         *    se formant de gauche à droite et de haut en bas. Un élément peut
         *    passer à la ligne lorsque l'on redimensionne la fenêtre.
         *  - GridLayout : les éléments sont disposés l'un à la suite de
         *    l'autre sur une grille avec un nombre de lignes et un nombre de
         *    colonnes définis par le programmeur, dont toutes les cases ont la
         *    même dimension. Cette dimension est calculée en fonction du
         *    nombre de cases à placer et de la dimension du contenant.
         */
        frame.setLayout(new FlowLayout());

        /** Définition des deux vues et ajout à la fenêtre. */
        grille = new VueGrille(modele);
        frame.add(grille);
        /**
         * Remarque : on peut passer à la méthode [add] des paramètres
         * supplémentaires indiquant où placer l'élément. Par exemple, si on
         * avait conservé la disposition par défaut [BorderLayout], on aurait
         * pu écrire le code suivant pour placer la grille à gauche et les
         * commandes à droite.
         *     frame.add(grille, BorderLayout.WEST);
         *     frame.add(commandes, BorderLayout.EAST);
         */

        /**
         * Fin de la plomberie :
         *  - Ajustement de la taille de la fenêtre en fonction du contenu.
         *  - Indiquer qu'on quitte l'application si la fenêtre est fermée.
         *  - Préciser que la fenêtre doit bien apparaître à l'écran.
         */
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

class VueGrille extends JPanel implements Observer {
    /** On maintient une référence vers le modèle. */
    private DModele modele;
    /** Définition d'une taille (en pixels) pour l'affichage des cellules. */
    private final static int TAILLE = 150;

    /** Constructeur. */
    public VueGrille(DModele modele) {
        this.modele = modele;
        /** On enregistre la vue [this] en tant qu'observateur de [modele]. */
        modele.addObserver(this);
        /**
         * Définition et application d'une taille fixe pour cette zone de
         * l'interface, calculée en fonction du nombre de cellules et de la
         * taille d'affichage.
         */
        Dimension dim = new Dimension(TAILLE*DModele.LARGEUR,
                TAILLE*DModele.HAUTEUR);
        this.setPreferredSize(dim);
    }

    /**
     * L'interface [Observer] demande de fournir une méthode [update], qui
     * sera appelée lorsque la vue sera notifiée d'un changement dans le
     * modèle. Ici on se content de réafficher toute la grille avec la méthode
     * prédéfinie [repaint].
     */
    public void update() { repaint(); }

    /**
     * Les éléments graphiques comme [JPanel] possèdent une méthode
     * [paintComponent] qui définit l'action à accomplir pour afficher cet
     * élément. On la redéfinit ici pour lui confier l'affichage des cellules.
     *
     * La classe [Graphics] regroupe les éléments de style sur le dessin,
     * comme la couleur actuelle.
     */
    public void paintComponent(Graphics g) {
        super.repaint();
        /** Pour chaque cellule... */
        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
        g.setFont(newFont);
        for(int i=0; i<DModele.LARGEUR; i++) {
            for(int j=0; j<DModele.HAUTEUR; j++) {
                /**
                 * ... Appeler une fonction d'affichage auxiliaire.
                 * On lui fournit les informations de dessin [g] et les
                 * coordonnées du coin en haut à gauche.
                 */
                paint(g, modele.getCase(i, j), i*TAILLE, j*TAILLE);
            }
        }
    }
    /**
     * Fonction auxiliaire de dessin d'une cellule.
     * Ici, la classe [Cellule] ne peut être désignée que par l'intermédiaire
     * de la classe [CModele] à laquelle elle est interne, d'où le type
     * [CModele.Cellule].
     * Ceci serait impossible si [Cellule] était déclarée privée dans [CModele].
     */
    private void paint(Graphics g, Case c, int x, int y) {
        /** Sélection d'une couleur. */
        if (c.EstTempete()) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(Color.ORANGE);
        }
        /** Coloration d'un rectangle. */
        g.fillRect(x, y, TAILLE-30, TAILLE-30);
        g.setColor(Color.BLACK);
        if(c.getSable() != 0){
            g.drawString(Integer.toString(c.getSable()),x,y+30);
        }
    }
}