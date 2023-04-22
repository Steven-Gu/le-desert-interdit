import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

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
    protected int nbJoueur;
    protected int difficulte;
    /**
     * On stocke un tableau de cellules.
     */
    protected Case[][] cases;
    protected ArrayList<Carte_Tempete> cartes = new ArrayList<>();
    protected ArrayList<player> cartes_joueurs = new ArrayList<>();
    protected ArrayList<Equipement> toolsCartes = new ArrayList<>();
    protected Case CaseTempete;
    protected ArrayList<player> players = new ArrayList<player>();
    protected int sableReste = 48;

    protected int niveauTempete;

    protected boolean helice = false;
    protected boolean boite_de_vitesses = false;
    protected boolean cristal_d_energie = false;
    protected boolean systeme_de_navigation = false;

    protected boolean estGagne = false;
    protected boolean estPerdu = false;

    /**
     * Construction : on initialise un tableau de cases.
     */
    public DModele() {
        /**
         * Pour éviter les problèmes aux bords, on ajoute une ligne et une
         * colonne de chaque côté, dont les cellules n'évolueront pas.
         */
        cases = new Case[LARGEUR][HAUTEUR];
        Random random = new Random();
        cases[2][2] = new tempete(this,2,2);
        int numTunnel = 0;
        int numOasis = 0;
        int numpiste = 0;
        int snc = 0;
        int snl = 0;
        int bvc = 0;
        int bvl = 0;
        int cec = 0;
        int cel = 0;
        int hc = 0;
        int hl = 0;

        boolean estmirage = true;
        while (numTunnel < 3 || numOasis < 3 || numpiste < 1 || snc < 1 || snl < 1
                || bvc < 1|| bvl < 1 || cec < 1 || cel < 1 || hc < 1 || hl < 1
        ){
            int i = random.nextInt(LARGEUR);
            int j = random.nextInt(HAUTEUR);
            if (cases[i][j] == null) {
                if (numTunnel < 3) {
                    cases[i][j] = new tunnel(this, i, j);
                    numTunnel++;
                } else if (numOasis < 3) {
                    if (estmirage) {
                        cases[i][j] = new mirage(this, i, j);
                        estmirage=false;
                    }
                    else cases[i][j] = new oasis(this, i, j);
                    numOasis++;
                } else if (numpiste < 1){
                    cases[i][j] = new piste(this, i, j);
                    numpiste++;
                } else if (snc < 1){
                    cases[i][j] = new systeme_de_navigation_col(this, i, j);
                    snc++;
                }else if (snl < 1){
                    cases[i][j] = new systeme_de_navigation_ligne(this, i, j);
                    snl++;
                }else if (bvc < 1){
                    cases[i][j] = new boite_de_vitesses_col(this, i, j);
                    bvc++;
                }else if (bvl < 1){
                    cases[i][j] = new boite_de_vitesses_ligne(this, i, j);
                    bvl++;
                }else if (cec < 1){
                    cases[i][j] = new cristal_d_energie_col(this, i, j);
                    cec++;
                }else if (cel < 1){
                    cases[i][j] = new cristal_d_energie_ligne(this, i, j);
                    cel++;
                }else if (hc < 1){
                    cases[i][j] = new helice_col(this, i, j);
                    hc++;
                }else if (hl < 1){
                    cases[i][j] = new helice_ligne(this, i, j);
                    hl++;
                }
            }
        }


        for (int i = 0; i < LARGEUR; i++) {
            for (int j = 0; j < HAUTEUR; j++) {
                if (cases[i][j]==null) {
                    cases[i][j] = new city(this, i, j);
                }
            }
        }
        initSable();
        initCartes();
        initTools();
        initToolsCartes();
    }
    public void initSable(){
        CaseTempete = cases[2][2];
        cases[0][2].ajouteSable();
        cases[1][1].ajouteSable();
        cases[1][3].ajouteSable();
        cases[2][0].ajouteSable();
        cases[2][4].ajouteSable();
        cases[3][1].ajouteSable();
        cases[3][3].ajouteSable();
        cases[4][2].ajouteSable();
    }
    public void initCartes(){
        for(int i = 0; i < 3; i++) cartes.add(new tempete_se_dechaine(this));
        for(int i = 0; i < 4; i++) cartes.add(new vague_de_chaleur(this));
        for(int direction = 0; direction < 4; direction++){
            cartes.add(new le_vent_souffle(this,3,direction));
            for(int c = 0; c < 2; c++) cartes.add(new le_vent_souffle(this,2,direction));
            for(int j = 0; j < 3; j++) cartes.add(new le_vent_souffle(this,1,direction));
        }

        Collections.shuffle(cartes);
    }
    public Case getCase (int x, int y){return cases[x][y];}

    public void initTools(){
        ArrayList<Integer> positions = new ArrayList<>();
        Random random = new Random();
        while (positions.size() < 12) {
            int position = random.nextInt(25);
            if (!positions.contains(position)&& position!=12) {
                positions.add(position);
            }
        }

        for (int position : positions) {
            int row = position / 5;
            int col = position % 5;
            cases[row][col].hasEquipement = true;
        }
    }
    public void initToolsCartes(){
        for(int i = 0; i < 3; i++) toolsCartes.add(new Equipement(1));
        for(int i = 0; i < 3; i++) toolsCartes.add(new Equipement(2));
        for(int i = 0; i < 2; i++) toolsCartes.add(new Equipement(3));
        for(int i = 0; i < 2; i++) toolsCartes.add(new Equipement(4));
        toolsCartes.add(new Equipement(5));
        toolsCartes.add(new Equipement(6));
        Collections.shuffle(toolsCartes);
    }

    public Carte_Tempete tireCarteTempete(){
        if (cartes.size() == 0) initCartes();
        Carte_Tempete c = this.cartes.get(0);
        this.cartes.remove(c);
        return c;
    }

    public void initCarteJoueur(){
        cartes_joueurs.add(new alpiniste());
        cartes_joueurs.add(new explorateur());
        cartes_joueurs.add(new porteuse());
        cartes_joueurs.add(new meteorologue());
        cartes_joueurs.add(new navigateur());
        cartes_joueurs.add(new archeologue());
        Collections.shuffle(cartes_joueurs);
    }

    public player tirerCarteJoueur(){
        player p = this.cartes_joueurs.get(0);
        this.cartes_joueurs.remove(p);
        players.add(p);
        return p;
    }

    public void win(){
        boolean b = true;
        if(helice && cristal_d_energie && systeme_de_navigation && boite_de_vitesses){
            for(player p : players){
                if(!(p.position.est_releve && p.position instanceof piste)) b = false;
            }
        }else b = false;
        estGagne = b;
    }
    public void lose(){
        estPerdu = true;
    }
}

abstract class Case {
    /** On conserve un pointeur vers la classe principale du modèle. */
    protected DModele modele;
    protected boolean helice = false;
    protected boolean boite_de_vitesses = false;
    protected boolean cristal_d_energie = false;
    protected boolean systeme_de_navigation = false;
    protected ArrayList<player> players = new ArrayList<player>();
    protected int sable = 0;
    protected boolean estBouclier = false;
    protected boolean hasEquipement = false;
    private int x, y;
    public Case(DModele modele, int x, int y) {
        this.modele = modele;
        this.x = x;
        this.y = y;
    }

    public void setX(int x){this.x = x;}
    public void setY(int y){this.y = y;}
    public int get_x(){
        return this.x;
    }

    public int get_y(){
        return this.y;
    }
    public void ajouteSable(){
        if (modele.sableReste > 0){
            this.modele.sableReste--;
            this.sable++;
        }else{
            this.modele.lose();
        }
    }

    public boolean retireSable(int i){
        if(this.sable >= i){
            this.sable -= i;
            this.modele.sableReste+=i;
            return true;
        }else{
            return false;
        }
    }
    public int getSable(){return this.sable;}

    protected boolean est_releve = false;
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            return true;
        }else return false;
    }
    abstract public String toString();

    //return neighbors
    public ArrayList<Case> get_4(){
        ArrayList<Case> cases = new ArrayList<>();
        if(this.x - 1 >=0) cases.add(this.modele.getCase(this.x-1,this.y));
        if(this.x + 1 <=4) cases.add(this.modele.getCase(this.x+1,this.y));
        if(this.y - 1 >=0) cases.add(this.modele.getCase(this.x,this.y-1));
        if(this.y + 1 <=4) cases.add(this.modele.getCase(this.x,this.y+1));
        return cases;
    }

    //return all neighbors including diagonal
    public ArrayList<Case> get_All(){
        ArrayList<Case> cases = new ArrayList<>();
        if(this.x - 1 >=0) cases.add(this.modele.getCase(this.x-1,this.y));
        if(this.x + 1 <=4) cases.add(this.modele.getCase(this.x+1,this.y));
        if(this.y - 1 >=0) cases.add(this.modele.getCase(this.x,this.y-1));
        if(this.y + 1 <=4) cases.add(this.modele.getCase(this.x,this.y+1));
        if(this.x - 1 >=0 && this.y - 1 >=0) cases.add(this.modele.getCase(this.x-1,this.y-1));
        if(this.x + 1 <=4 && this.y + 1 <=4) cases.add(this.modele.getCase(this.x+1,this.y+1));
        if(this.y - 1 >=0 && this.x + 1 >=0) cases.add(this.modele.getCase(this.x+1,this.y-1));
        if(this.y + 1 <=4 && this.x - 1 >=0) cases.add(this.modele.getCase(this.x-1,this.y+1));
        return cases;
    }

}

class city extends Case{
    public city(DModele modele, int x, int y){
        super(modele,x, y);
    }
    public String toString(){return "cite";}
}
class tempete extends Case{
    public tempete(DModele modele, int x, int y){
        super(modele,x, y);
    }
    public String toString(){return "Tempete";}
}

class oasis extends Case{
    public oasis(DModele modele, int x, int y){super(modele,x, y);}
    protected boolean mirage = false;
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if(!this.mirage){
                for (player p:this.players){p.ajouteGourde(2);}
            }
            return true;
        }else return false;
    }
     public String toString(){
        if (this.mirage) {return "mirage";}
        else return "oasis";
    }
}

class mirage extends oasis{
    public mirage(DModele modele, int x, int y){
        super(modele,x, y);
        this.mirage = true;
    }
    public String toString() {
        if(this.est_releve)
            return "mirage";
        else{return "oasis";}
    }
}

class piste extends Case{
    public piste(DModele modele, int x, int y){super(modele,x, y);}
    public String toString(){return "piste";}
}

class tunnel extends Case{
    private tunnel autre1,autre2;
    public tunnel(DModele modele, int x, int y){super(modele,x, y);}

    public void setAutres(tunnel autre1, tunnel autre2) {
        this.autre1 = autre1;
        this.autre2 = autre2;
    }
    public String toString(){return "tunnel";}
}

class helice_ligne extends Case{
    public helice_ligne(DModele modele, int x, int y){super(modele,x, y);}
    protected helice_col col;
    public void setCol(helice_col hc){this.col = hc;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.col.est_releve){this.modele.getCase(this.get_x(),this.col.get_y()).helice = true;}
            return true;
        }else return false;
    }
    public String toString(){return "ligne_helice";}
}

class helice_col extends Case{
    public helice_col(DModele modele, int x, int y){super(modele,x, y);}
    protected helice_ligne l;
    public void setCol(helice_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).helice = true;}
            return true;
        }else return false;
    }
    public String toString(){return "colonne_helice";}
}

class boite_de_vitesses_ligne extends Case{
        public boite_de_vitesses_ligne(DModele modele, int x, int y){super(modele,x, y);}
        protected boite_de_vitesses_col col;
        public void setCol(boite_de_vitesses_col hc){this.col = hc;}
        public boolean releve(){
            if (!this.est_releve) {
                this.est_releve = true;
                if (this.col.est_releve){this.modele.getCase(this.get_x(),this.col.get_y()).boite_de_vitesses = true;}
                return true;
            }else return false;
        }
        public String toString(){return "ligne_boite_de_vitesses";}
    }

class boite_de_vitesses_col extends Case{
    public boite_de_vitesses_col(DModele modele, int x, int y){super(modele,x, y);}
    protected boite_de_vitesses_ligne l;
    public void setCol(boite_de_vitesses_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).boite_de_vitesses = true;}
            return true;
        }else return false;
    }
    public String toString(){return "colonne_boite_de_vitesses";}
}

class cristal_d_energie_ligne extends Case{
    public cristal_d_energie_ligne(DModele modele, int x, int y){super(modele,x, y);}
    protected cristal_d_energie_col col;
    public void setCol(cristal_d_energie_col c){this.col = c;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.col.est_releve){this.modele.getCase(this.get_x(),this.col.get_y()).cristal_d_energie = true;}
            return true;
        }else return false;
    }
    public String toString(){return "ligne_cristal_d_energie";}
}

class cristal_d_energie_col extends Case{
    public cristal_d_energie_col(DModele modele, int x, int y){super(modele,x, y);}
    protected cristal_d_energie_ligne l;
    public void setCol(cristal_d_energie_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).cristal_d_energie = true;}
            return true;
        }else return false;
    }
    public String toString(){return "colonne_cristal_d_energie";}
}

class systeme_de_navigation_ligne extends Case{
    public systeme_de_navigation_ligne(DModele modele, int x, int y){super(modele,x, y);}
    protected systeme_de_navigation_col col;
    public void setCol(systeme_de_navigation_col c){this.col = c;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.col.est_releve){this.modele.getCase(this.get_x(),this.col.get_y()).systeme_de_navigation = true;}
            return true;
        }else return false;
    }
    public String toString(){return "ligne_systeme_de_navigation";}
}

class systeme_de_navigation_col extends Case{
    public systeme_de_navigation_col(DModele modele, int x, int y){super(modele,x, y);}
    protected systeme_de_navigation_ligne l;
    public void setCol(systeme_de_navigation_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).systeme_de_navigation = true;}
            return true;
        }else return false;
    }
    public String toString(){return "colonne_systeme_de_navigation";}
}


abstract class player{
    protected Case position;
    protected String name;
    protected Color couleur;
    protected int move = 4;
    protected int water = 4;
    protected int maxWater=4;
    protected ArrayList<Equipement> tools;

    public void setCase(Case c){
        this.position = c;
    }

    public void newturn(){
        this.move =4;
        this.modele.notifyObservers();
    }
    public ArrayList<Case> casedispo() {
        ArrayList<Case> list = new ArrayList<>();
        boolean have_alpiniste = false;
        for (player p : this.position.players) {
            if (p instanceof alpiniste) have_alpiniste = true;
        }
        if (this.position.getSable() >= 2 && !have_alpiniste) return list;
        for (Case c : this.position.get_4()) {
            if (!(c instanceof tempete) && c.getSable() < 2) list.add(c);
        }
        return list;
    }


    public void releveP(){
        if(this.position.getSable()==0){
            if(this.position.releve()){
                this.move -= 1;
            }
        }
    }
    public void shareWater(player a){
        if(a.position == this.position && this.water > 1){
            a.ajouteGourde(1);
            this.water -= 1;
        }
    }

    public boolean drink(){
        if(this.position instanceof tunnel || this.position.estBouclier) return true;
        if(this.water > 1) {
            this.water --;
            return true;
        }else return false;
    }

    // add gourde
    public void ajouteGourde(int gourde){
        if(this.water + gourde<4) this.water +=gourde;
        else this.water = 4;
    }

    //move to another case
    public void deplace(Case c){
        if(this.casedispo().contains(c)){
            this.position.players.remove(this);
            this.position = c;
            this.position.players.add(this);
            this.move = this.move -1;
        }
    }

    //remove sand on the given case
    public void remove_sand(Case c){
        if(this.casedispo().contains(c) || c==this.position){
            if(c.retireSable(1)) this.move = this.move -1;
        }
    }

    public void use_tool(Equipement tool, Case c){
        if(this.tools.contains(tool)){
            switch(tool.n_tool){
                case 1:
                    if(this.position.get_4().contains(c)){
                        while (c.retireSable(1)) {}
                        tool.used = true;
                    }
                case 2:
                    if(c.sable<=1 && ! (c instanceof tempete)){
                        this.position = c;
                        tool.used = true;
                    }
                case 3:
                    this.position.estBouclier = true;
                    tool.used = true;
                case 4:

                case 5:
                    this.move +=2;
                    tool.used = true;
                case 6:
                    ArrayList<player> list = new ArrayList<>();
                    list = this.position.players;
                    for(player n: list){
                        n.ajouteGourde(2);
                    }
                    tool.used = true;
            }

        }
    }

}

class archeologue extends player{
    @Override
    public void remove_sand(Case c) {
        if(this.casedispo().contains(c) || c==this.position){
            if(c.retireSable(2)) this.move = this.move -1;
            else if(c.retireSable(1)) this.move = this.move -1;
        }
    }
}

class alpiniste extends player{
    @Override
    public ArrayList<Case> casedispo() {
        ArrayList<Case> list = new ArrayList<>();
        for(Case c: this.position.get_4()){
            if(!(c instanceof tempete)) list.add(c);
        }
        return list;
    }


    public void deplaceAvec(Case c,player a) {
        this.deplace(c);
        if(this.position.players.contains(a)) {
            a.position.players.remove(a);
            a.setCase(c);
            a.position.players.add(a);
        }
    }

}
class explorateur extends player{
    @Override
    public ArrayList<Case> casedispo() {
        ArrayList<Case> list = new ArrayList<>();
        boolean have_alpiniste = false;
        for (player p : this.position.players) {
            if (p instanceof alpiniste) have_alpiniste = true;
        }
        if (this.position.getSable() >= 2 && !have_alpiniste) return list;
        for (Case c : this.position.get_All()) {
            if (!(c instanceof tempete) && c.getSable() < 2) list.add(c);
        }
        return list;
    }
}
class meteorologue extends player{
    public void voirCarte(){

    }
}
class navigateur extends player{
    //tims he can navigate
    protected int n = 3;
    private void deplaceNormal(player a,Case c1){
        a.deplace(c1);
    }
    private void deplaceAlpiniste(alpiniste a,Case c1, player b){
        a.deplaceAvec(c1,b);
    }
    public void navigate(alpiniste a,Case c1,Case c2, Case c3, player p1, player p2,player p3){
        deplaceAlpiniste(a,c1,p1);
        deplaceAlpiniste(a,c2,p2);
        deplaceAlpiniste(a,c3,p3);
    }
    public void navigate(player a,Case c1,Case c2, Case c3){
        deplaceNormal(a,c1);
        deplaceNormal(a,c2);
        deplaceNormal(a,c3);
    }


}
class porteuse extends player{
    @Override
    public void ajouteGourde(int gourde) {
        if(this.water + gourde<5) this.water +=gourde;
        else this.water = 5;
    }

    public void getWater(){
        if((this.position) instanceof oasis){
            this.water ++;
            this.move --;
        }
    }

    @Override
    public void shareWater( player a) {
        ArrayList<Case> list = new ArrayList<>();
        list = this.position.get_4();
        if(list.contains(a.position) && this.water > 1){
            a.ajouteGourde(1);
            this.water -= 1;
        }
    }
}


abstract class Carte_Tempete{
    protected DModele modele;
    public Carte_Tempete(DModele modele){this.modele=modele;}
    abstract public void effet();
}

class vague_de_chaleur extends Carte_Tempete{

    public vague_de_chaleur(DModele modele){super(modele);}

    @Override
    public void effet() {
        for (player p:this.modele.players){
            if (!p.drink()) this.modele.lose();
        }
    }
}

class tempete_se_dechaine extends Carte_Tempete{
    public tempete_se_dechaine(DModele modele){super(modele);}
    @Override
    public void effet() {
        if(++this.modele.niveauTempete>=7)
            this.modele.lose();
    }
}

class le_vent_souffle extends Carte_Tempete{
    private int distance;
    private int direction;
    public le_vent_souffle(DModele modele,int distance,int direction){
        super(modele);
        this.distance=distance;
        this.direction=direction;
    }
    @Override
    public void effet() {
        int tx = this.modele.CaseTempete.get_x();
        int ty = this.modele.CaseTempete.get_y();
        switch (direction){
            case 0://left
                for(int i=0; i<distance;i++){
                    if (tx > 0) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx - 1][ty];
                        this.modele.cases[tx][ty].setX(tx);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setX(tx-1);
                        this.modele.cases[tx - 1][ty] = tmp;
                        tx--;
                    }
                }
                break;
            case 1://right
                for(int i=0; i<distance;i++){
                    if (tx < 4) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx + 1][ty];
                        this.modele.cases[tx][ty].setX(tx);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setX(tx+1);
                        this.modele.cases[tx + 1][ty] = tmp;
                        tx++;
                    }
                }
                break;
            case 2://front
                for(int i=0; i<distance;i++){
                    if (ty > 0) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx][ty - 1];
                        this.modele.cases[tx][ty].setY(ty);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setY(ty-1);
                        this.modele.cases[tx][ty - 1] = tmp;
                        ty--;
                    }
                }
                break;
            case 3://back
                for(int i=0; i<distance;i++){
                    if (ty < 4) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx][ty + 1];
                        this.modele.cases[tx][ty].setY(ty);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setY(ty+1);
                        this.modele.cases[tx][ty + 1] = tmp;
                        ty++;
                    }
                }
                break;
        }

    }
}

class Equipement{
    protected String nom;
    protected int n_tool;
    protected boolean used = false;

    public Equipement(int n){
        n_tool = n;
        switch(n){
            case 1:
                nom = "blaster";
                break;
            case 2:
                nom = "jetback";
                break;
            case 3:
                nom = "bouclier";
                break;
            case 4:
                nom = "terrascope";
                break;
            case 5:
                nom = "boost";
                break;
            case 6:
                nom = "reserveEau";
                break;
        }
    }
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
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1F);
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
        for(int i = 0; i < this.modele.players.size();i++){
            player p = this.modele.players.get(i);
            g.setColor(p.couleur);
            g.fillRect(p.position.get_y()*TAILLE + 20*i,p.position.get_x()*TAILLE+45,20,30);
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
        if (c instanceof tempete) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(Color.ORANGE);
        }
        /** Coloration d'un rectangle. */
        g.fillRect(x, y, TAILLE-30, TAILLE-30);
        g.setColor(Color.BLACK);
        if (c.est_releve || (c instanceof oasis)) g.drawString(c.toString(),x+5,y+20);
        if(c.getSable() != 0){
            g.drawString("sable: " + Integer.toString(c.getSable()),x+5,y+35);
        }
    }

    public void actionPerformed(ActionEvent e){

    }
    public void mousePressed(MouseEvent e){
        int y = e.getX()/TAILLE;
        int x = e.getY()/TAILLE;

        if (SwingUtilities.isLeftMouseButton(e)) {
            this.controleur.deplace(x,y);
        } else if (SwingUtilities.isRightMouseButton(e)) {
            this.controleur.removeSand(x,y);
        }
    }
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}