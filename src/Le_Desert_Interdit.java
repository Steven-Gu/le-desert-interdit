import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicOptionPaneUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Le_Desert_Interdit{
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            /* Voici le contenu qui nous intéresse. */
            DModele modele = new DModele();
            DVue vue = new DVue(modele);
        });
    }

}

interface Observer{
    public void update();
}

abstract class Observable {
    private  ArrayList<Observer> observers;
    public Observable() {
        this.observers = new ArrayList<>();
    }
    public void addObserver(Observer o) {
        observers.add(o);
    }
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
    protected Case pos_initiale;
    protected ArrayList<player> players = new ArrayList<>();
    protected static int sableReste = 48;

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
        /*
          Pour éviter les problèmes aux bords, on ajoute une ligne et une
          colonne de chaque côté, dont les cellules n'évolueront pas.
         */
        cases = new Case[LARGEUR][HAUTEUR];
        Random random = new Random();
        int init_i,init_j;
        do{
        init_i = random.nextInt(LARGEUR);
        init_j = random.nextInt(HAUTEUR);
        }while (init_i == 2 && init_j == 2);

        cases[2][2] = new tempete(this,2,2);
        ArrayList<tunnel> tunnels = new ArrayList<>();
        systeme_de_navigation_ligne SNL = null;
        systeme_de_navigation_col SNC = null;
        boite_de_vitesses_col BVC = null;
        boite_de_vitesses_ligne BVL = null;
        cristal_d_energie_col CEC = null;
        cristal_d_energie_ligne CEL = null;
        helice_col HC = null;
        helice_ligne HL = null;
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
                    tunnel newTunnel = new tunnel(this, i, j);
                    cases[i][j] = newTunnel;
                    tunnels.add(newTunnel);
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
                    SNC = new systeme_de_navigation_col(this, i, j);
                    cases[i][j] = SNC;
                    snc++;
                }else if (snl < 1){
                    SNL = new systeme_de_navigation_ligne(this, i, j);
                    cases[i][j] = SNL;
                    snl++;
                }else if (bvc < 1){
                    BVC = new boite_de_vitesses_col(this, i, j);
                    cases[i][j] = BVC;
                    bvc++;
                }else if (bvl < 1){
                    BVL = new boite_de_vitesses_ligne(this, i, j);
                    cases[i][j] = BVL;
                    bvl++;
                }else if (cec < 1){
                    CEC = new cristal_d_energie_col(this, i, j);
                    cases[i][j] = CEC;
                    cec++;
                }else if (cel < 1){
                    CEL = new cristal_d_energie_ligne(this, i, j);
                    cases[i][j] = CEL;
                    cel++;
                }else if (hc < 1){
                    HC = new helice_col(this, i, j);
                    cases[i][j] = HC;
                    hc++;
                }else {
                    HL = new helice_ligne(this, i, j);
                    cases[i][j] = HL;
                    hl++;
                }
            }
        }
        if (tunnels.size() == 3) {
            tunnels.get(0).setAutres(tunnels.get(1), tunnels.get(2));
            tunnels.get(1).setAutres(tunnels.get(0), tunnels.get(2));
            tunnels.get(2).setAutres(tunnels.get(0), tunnels.get(1));
        }
        SNL.setCol(SNC);
        SNC.setLigne(SNL);
        BVC.setLigne(BVL);
        BVL.setCol(BVC);
        CEL.setCol(CEC);
        CEC.setLigne(CEL);
        HL.setCol(HC);
        HC.setLigne(HL);

        for (int i = 0; i < LARGEUR; i++) {
            for (int j = 0; j < HAUTEUR; j++) {
                if (cases[i][j]==null) {
                    cases[i][j] = new city(this, i, j);
                }
            }
        }
        pos_initiale = cases[init_i][init_j];
        initSable();
        initCartes();
        initTools();
        initToolsCartes();
        initCarteJoueur();
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
        c.effet();
        notifyObservers();
        return c;
    }

    public void initCarteJoueur() {
        cartes_joueurs.add(new alpiniste(this, "", Color.BLACK));
        cartes_joueurs.add(new explorateur(this, "", Color.BLACK));
        cartes_joueurs.add(new porteuse(this, "", Color.BLACK));
        cartes_joueurs.add(new meteorologue(this, "", Color.BLACK));
        cartes_joueurs.add(new navigateur(this, "", Color.BLACK));
        cartes_joueurs.add(new archeologue(this, "", Color.BLACK));
        Collections.shuffle(cartes_joueurs);
    }

    public player tirerCarteJoueur(String playerName, Color c) {
        player p = this.cartes_joueurs.get(0);
        this.cartes_joueurs.remove(p);

        p.name = playerName;
        p.couleur = c;
        p.position = pos_initiale;
        pos_initiale.players.add(p);

        players.add(p);
        notifyObservers();
        return p;
    }


    public void win(){
        boolean b = true;
        if(helice && cristal_d_energie && systeme_de_navigation && boite_de_vitesses){
            for(player p : players){
                if (!(p.position.est_releve && p.position instanceof piste)) {
                    b = false;
                    break;
                }
            }
        }else b = false;
        estGagne = b;
        notifyObservers();
    }
    public void lose(){
        estPerdu = true;
        notifyObservers();
    }
}

abstract class Case {
    /** On conserve un pointeur vers la classe principale du modèle. */
    protected DModele modele;
    protected boolean helice = false;
    protected boolean boite_de_vitesses = false;
    protected boolean cristal_d_energie = false;
    protected boolean systeme_de_navigation = false;
    protected ArrayList<player> players = new ArrayList<>();
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
        this.modele.notifyObservers();
    }

    public boolean retireSable(int i){
        if(this.sable >= i){
            this.sable -= i;
            this.modele.sableReste+=i;
            this.modele.notifyObservers();
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
            this.modele.notifyObservers();
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
        if(this.y - 1 >=0 && this.x + 1 <=4) cases.add(this.modele.getCase(this.x+1,this.y-1));
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
            this.modele.notifyObservers();
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

    public tunnel getAutre1() {
        return autre1;
    }

    public tunnel getAutre2() {
        return autre2;
    }


}

class helice_ligne extends Case{
    public helice_ligne(DModele modele, int x, int y){super(modele,x, y);}
    protected helice_col col;
    public void setCol(helice_col hc){this.col = hc;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.col.est_releve){this.modele.getCase(this.get_x(),this.col.get_y()).helice = true;}
            this.modele.notifyObservers();
            return true;
        }else return false;
    }
    public String toString(){return "ligne_helice";}
}

class helice_col extends Case{
    public helice_col(DModele modele, int x, int y){super(modele,x, y);}
    protected helice_ligne l;
    public void setLigne(helice_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).helice = true;}
            this.modele.notifyObservers();
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
                this.modele.notifyObservers();
                return true;
            }else return false;
        }
        public String toString(){return "ligne_boite_de_vitesses";}
    }

class boite_de_vitesses_col extends Case{
    public boite_de_vitesses_col(DModele modele, int x, int y){super(modele,x, y);}
    protected boite_de_vitesses_ligne l;
    public void setLigne(boite_de_vitesses_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).boite_de_vitesses = true;}
            this.modele.notifyObservers();
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
            this.modele.notifyObservers();
            return true;
        }else return false;
    }
    public String toString(){return "ligne_cristal_d_energie";}
}

class cristal_d_energie_col extends Case{
    public cristal_d_energie_col(DModele modele, int x, int y){super(modele,x, y);}
    protected cristal_d_energie_ligne l;
    public void setLigne(cristal_d_energie_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).cristal_d_energie = true;}
            this.modele.notifyObservers();
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
            this.modele.notifyObservers();
            return true;
        }else return false;
    }
    public String toString(){return "ligne_systeme_de_navigation";}
}

class systeme_de_navigation_col extends Case{
    public systeme_de_navigation_col(DModele modele, int x, int y){super(modele,x, y);}
    protected systeme_de_navigation_ligne l;
    public void setLigne(systeme_de_navigation_ligne l){this.l = l;}
    public boolean releve(){
        if (!this.est_releve) {
            this.est_releve = true;
            if (this.l.est_releve){this.modele.getCase(this.l.get_x(),this.get_y()).systeme_de_navigation = true;}
            this.modele.notifyObservers();
            return true;
        }else return false;
    }
    public String toString(){return "colonne_systeme_de_navigation";}
}


abstract class player{
    protected DModele modele;
    protected Case position;
    protected String name;
    protected Color couleur;
    protected int move = 4;
    protected int water = 4;
    protected int maxWater=4;
    protected ArrayList<Equipement> tools;
    public player(DModele modele, String name, Color c) {
        this.modele = modele;
        this.name = name;
        this.couleur = c;
    }

    public void setCase(Case c){
        this.position = c;
        c.players.add(this);
        this.modele.notifyObservers();
    }

    public void newturn(){
        this.move =4;
        this.modele.notifyObservers();
    }
    public ArrayList<Case> casedispo() {
        ArrayList<Case> list = new ArrayList<>();
        boolean have_alpiniste = false;
        for (player p : this.position.players) {
            if (p instanceof alpiniste) {
                have_alpiniste = true;
                break;
            }
        }
        if (this.position.getSable() >= 2 && !have_alpiniste) return list;
        for (Case c : this.position.get_4()) {
            if (!(c instanceof tempete) && c.getSable() < 2) list.add(c);
        }
        return list;
    }


    public void releveP(){
        if(this.position.getSable()==0 && this.move > 0){
            if(this.position.releve()){
                this.move -= 1;
            }
            this.modele.notifyObservers();
        }
    }
    public void shareWater(player a){
        if(a.position == this.position && this.water > 1){
            a.ajouteGourde(1);
            this.water -= 1;
            this.modele.notifyObservers();
        }
    }

    public boolean drink(){
        if((this.position.releve() && this.position instanceof tunnel) || this.position.estBouclier) return true;
        if(this.water > 1) {
            this.water --;
            this.modele.notifyObservers();
            return true;
        }else return false;
    }

    // add gourde
    public void ajouteGourde(int gourde){
        if(this.water + gourde<this.maxWater) this.water +=gourde;
        else this.water = this.maxWater;
        this.modele.notifyObservers();
    }

    //move to another case
    public void deplace(Case c){
        if(this.casedispo().contains(c) && this.move > 0){
            this.position.players.remove(this);
            this.position = c;
            this.position.players.add(this);
            this.move = this.move -1;
            this.modele.notifyObservers();
        }
    }

    //remove sand on the given case
    public void remove_sand(Case c){
        if((this.position.get_4().contains(c) || c==this.position) && this.move > 0){
            if(c.retireSable(1)) {
                this.move = this.move -1;
                this.modele.notifyObservers();
            }
        }
    }

    public void tire_tool(){
        if(this.position.hasEquipement && this.position.est_releve){
            this.tools.add(this.position.modele.toolsCartes.get(0));
            this.position.hasEquipement = false;
            this.position.modele.toolsCartes.remove(0);
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
                    this.modele.notifyObservers();
                    break;
                case 2:
                    if(c.sable<=1 && ! (c instanceof tempete)){
                        this.position = c;
                        tool.used = true;
                    }
                    this.modele.notifyObservers();
                    break;
                case 3:
                    this.position.estBouclier = true;
                    tool.used = true;
                    this.modele.notifyObservers();
                    break;
                case 4:
                    this.modele.notifyObservers();
                    break;
                case 5:
                    this.move +=2;
                    tool.used = true;
                    this.modele.notifyObservers();
                    break;
                case 6:
                    ArrayList<player> list;
                    list = this.position.players;
                    for(player n: list){
                        n.ajouteGourde(2);
                    }
                    tool.used = true;
                    this.modele.notifyObservers();
                    break;
            }

        }
    }

}

class archeologue extends player{

    public archeologue(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
    @Override
    public void remove_sand(Case c) {
        if((this.position.get_4().contains(c) || c==this.position)&& this.move > 0){
            if(c.retireSable(2)) this.move = this.move -1;
            else if(c.retireSable(1)) this.move = this.move -1;
            this.modele.notifyObservers();
        }
    }
}

class alpiniste extends player{
    public alpiniste(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
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
        this.modele.notifyObservers();
    }

}
class explorateur extends player{
    public explorateur(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
    @Override
    public ArrayList<Case> casedispo() {
        ArrayList<Case> list = new ArrayList<>();
        boolean have_alpiniste = false;
        for (player p : this.position.players) {
            if (p instanceof alpiniste) {
                have_alpiniste = true;
                break;
            }
        }
        if (this.position.getSable() >= 2 && !have_alpiniste) return list;
        for (Case c : this.position.get_All()) {
            if (!(c instanceof tempete) && c.getSable() < 2) list.add(c);
        }
        return list;
    }
}
class meteorologue extends player{
    public meteorologue(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
    public void voirCarte(){

    }
}
class navigateur extends player{
    public navigateur(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
    protected int n = 3;
    private void deplaceNormal(player a,Case c1){
        a.deplace(c1);
    }
    private void deplaceAlpiniste(alpiniste a,Case c1, player b){
        a.deplaceAvec(c1,b);
        this.modele.notifyObservers();
    }
    public void navigate(alpiniste a,Case c1,Case c2, Case c3, player p1, player p2,player p3){
        deplaceAlpiniste(a,c1,p1);
        deplaceAlpiniste(a,c2,p2);
        deplaceAlpiniste(a,c3,p3);
        this.modele.notifyObservers();
    }
    public void navigate(player a,Case c1,Case c2, Case c3){
        deplaceNormal(a,c1);
        deplaceNormal(a,c2);
        deplaceNormal(a,c3);
        this.modele.notifyObservers();
    }


}
class porteuse extends player{

    public porteuse(DModele modele, String name, Color c) {
        super(modele, name, c);
        this.maxWater = 5;
        this.water = 5;
    }
    @Override
    public void ajouteGourde(int gourde) {
        if(this.water + gourde<5) this.water +=gourde;
        else this.water = 5;
        this.modele.notifyObservers();
    }

    public void getWater(){
        if((this.position) instanceof oasis){
            this.water ++;
            this.move --;
        }
        this.modele.notifyObservers();
    }

    @Override
    public void shareWater( player a) {
        ArrayList<Case> list;
        list = this.position.get_4();
        if(list.contains(a.position) && this.water > 1){
            a.ajouteGourde(1);
            this.water -= 1;
        }
        this.modele.notifyObservers();
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
        this.modele.notifyObservers();
    }
}

class le_vent_souffle extends Carte_Tempete {
    private final int distance;
    private final int direction;

    public le_vent_souffle(DModele modele, int distance, int direction) {
        super(modele);
        this.distance = distance;
        this.direction = direction;
    }

    @Override
    public void effet() {
        int initial_tx = this.modele.CaseTempete.get_x();
        int initial_ty = this.modele.CaseTempete.get_y();
        int tx = initial_tx;
        int ty = initial_ty;
        switch (direction) {
            case 0: //left
                for (int i = 0; i < distance; i++) {
                    if (ty > 0) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx][ty - 1];
                        this.modele.cases[tx][ty].setY(ty);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setY(ty - 1);
                        this.modele.cases[tx][ty - 1] = tmp;
                        ty--;
                        this.modele.sableReste--;
                    }
                }
                break;
            case 1: //right
                for (int i = 0; i < distance; i++) {
                    if (ty < 4) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx][ty + 1];
                        this.modele.cases[tx][ty].setY(ty);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setY(ty + 1);
                        this.modele.cases[tx][ty + 1] = tmp;
                        ty++;
                        this.modele.sableReste--;
                    }
                }
                break;
            case 2: //front
                for (int i = 0; i < distance; i++) {
                    if (tx > 0) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx - 1][ty];
                        this.modele.cases[tx][ty].setX(tx);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setX(tx - 1);
                        this.modele.cases[tx - 1][ty] = tmp;
                        tx--;
                        this.modele.sableReste--;
                    }
                }
                break;
            case 3: //back
                for (int i = 0; i < distance; i++) {
                    if (tx < 4) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx + 1][ty];
                        this.modele.cases[tx][ty].setX(tx);
                        this.modele.cases[tx][ty].ajouteSable();
                        tmp.setX(tx + 1);
                        this.modele.cases[tx + 1][ty] = tmp;
                        tx++;
                        this.modele.sableReste--;
                    }
                }
                break;
        }
        this.modele.notifyObservers();
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



class DVue extends JFrame implements Observer, ActionListener {
    //JPanel cards;
    private DModele modele;
    private DControleur controleur;
    public DVue(DModele modele) {
        this.modele = modele;
        this.modele.addObserver(this);
        this.controleur = new DControleur(modele,this);

        JFrame frame = new JFrame();
        frame.setTitle("Le Desert Interdit");



        frame.setPreferredSize(new Dimension(1500,1000));
        frame.setLayout(null);

        VueGrille grille = new VueGrille(modele,controleur);
        VueJoueur joueur = new VueJoueur(modele,controleur);
        VueTempete tempete = new VueTempete(modele,controleur);
        VueStatus status = new VueStatus(modele,controleur);
        VueButtons buttons = new VueButtons(modele,controleur);

        frame.add(joueur);
        frame.add(grille);
        frame.add(tempete);
        frame.add(status);
        frame.add(buttons);


        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    @Override
    public void update() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle button clicks and call the appropriate method in the controller
        controleur.performAction(e.getActionCommand());
    }
}
class VueStatus extends JPanel implements Observer,ActionListener{
    private DModele modele;
    private DControleur controleur;
    private JTextField textT = new JTextField();
    private JTextField textS = new JTextField();
    private JTextArea piece1 = new JTextArea();
    private JTextArea piece2 = new JTextArea();

    public VueStatus(DModele modele,DControleur controleur){
        this.modele = modele;
        this.controleur = controleur;
        this.modele.addObserver(this);

        setBounds(0,0,240,160);
        setBorder(BorderFactory.createLineBorder(Color.BLUE,3));

        textT = new JTextField("Niveau tempete: " + this.modele.niveauTempete);
        textS = new JTextField("Total Sable: " + this.modele.sableReste);
        piece1 = new JTextArea("Helice: " + Boolean.toString(this.modele.helice)
                +"\nboite de vitesse: "+Boolean.toString(this.modele.boite_de_vitesses));
        piece2 = new JTextArea("Systeme de navigation: " + Boolean.toString(this.modele.systeme_de_navigation)
                +"\ncristal d'énergie: "+Boolean.toString(this.modele.cristal_d_energie));

        add(textT);
        add(textS);
        add(piece1);
        add(piece2);

    }
    @Override
    public void update() {
        textT.setText("Niveau tempete: " + this.modele.niveauTempete);
        textS.setText("Total Sable: " + this.modele.sableReste);
        piece1.setText("Helice: " + Boolean.toString(this.modele.helice)
                +"\nboite de vitesse: "+Boolean.toString(this.modele.boite_de_vitesses));
        piece2.setText("Systeme de navigation: " + Boolean.toString(this.modele.systeme_de_navigation)
                +"\ncristal d'énergie: "+Boolean.toString(this.modele.cristal_d_energie));
        repaint();
    }

    public void actionPerformed(ActionEvent e){
        controleur.performAction(e.getActionCommand());
    }
}


class VueIndicate extends JPanel implements Observer{
    private DModele modele;
    private DControleur controleur;
    private JTextArea text = new JTextArea();


    public VueIndicate(DModele modele,DControleur controleur){
        this.modele = modele;
        this.controleur = controleur;
        this.modele.addObserver(this);

        setBounds(1000,255,240,200);
        setBorder(BorderFactory.createLineBorder(Color.GRAY,3));

        text.setLineWrap(true);


        add(text);
    }

    @Override
    public void update() {
        repaint();
    }
}

class VueJoueur extends JPanel implements Observer,ActionListener{
    private DModele modele;
    private DControleur controleur;
    private ArrayList<JTextField> actionFields = new ArrayList<>();
    private ArrayList<JTextField> waterFields = new ArrayList<>();

    public VueJoueur(DModele modele,DControleur controleur){
        //Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE};
        this.modele = modele;
        this.controleur = controleur;
        modele.addObserver(this);
        setBounds(0,105,240,600);
        //Dimension d = new Dimension(240,600);
        //this.setPreferredSize(d);
        setLayout(null);

        for(int i = 0; i < modele.players.size();i++){
            String panelName = "player"+i;
            JPanel panel = addPlayer(i,modele.players.get(i).couleur,panelName,modele.players.get(i));
            add(panel);
        }

    }

    public JPanel addPlayer(int n, Color c,String s,player p){
        JPanel panel = new JPanel();
        panel.setName(s);
        panel.setBounds(0,n*120,240,120);
        panel.setLayout(new GridLayout(4,2));
        panel.setBorder(BorderFactory.createLineBorder(c,3));
        JTextField nameText = new JTextField(p.name);
        JTextField classText = new JTextField(p.getClass().getSimpleName());
        JTextField action = new JTextField("actions: " + p.move);
        JTextField water = new JTextField("eau: "+p.water +" (max:" + p.maxWater +")");
        actionFields.add(action);
        waterFields.add(water);

        JButton equip = new JButton("Equipment");
        equip.setActionCommand("equip"+n);
        equip.addActionListener(this);

        JButton donnerEau = new JButton("Donner Eau");
        donnerEau.setActionCommand("DonnerEau"+n);
        donnerEau.addActionListener(this);

        panel.add(nameText);
        panel.add(classText);
        panel.add(action);
        panel.add(water);
        panel.add(equip);
        panel.add(donnerEau);
        return panel;
    }

    public void actionPerformed(ActionEvent e){
        switch(e.getActionCommand()){
            case "equip0":
                JFrame equipment = new VueEquipment(this.modele,this.controleur,0);
                equipment.setVisible(true);
                break;
            case "equip1":
                JFrame equipment1 = new VueEquipment(this.modele,this.controleur,1);
                equipment1.setVisible(true);
                break;
            case "equip2":
                JFrame equipment2 = new VueEquipment(this.modele,this.controleur,2);
                equipment2.setVisible(true);
                break;
            case "equip3":
                JFrame equipment3 = new VueEquipment(this.modele,this.controleur,3);
                equipment3.setVisible(true);
                break;
            case "equip4":
                JFrame equipment4 = new VueEquipment(this.modele,this.controleur,4);
                equipment4.setVisible(true);
                break;
        }
        controleur.performAction(e.getActionCommand());
    }

    @Override
    public void update() {
        for (int i = 0; i < modele.players.size(); i++) {
            player p = modele.players.get(i);

            // Update the player data in the UI components
            actionFields.get(i).setText("actions: " + p.move);
            waterFields.get(i).setText("eau: " + p.water + " (max:" + p.maxWater + ")");
        }

        repaint();
    }
}
class VueTempete extends JPanel implements Observer,ActionListener{
    private DModele modele;
    private DControleur controleur;

    public VueTempete(DModele modele,DControleur controleur){
        this.modele = modele;
        this.controleur = controleur;

        setBounds(1000,0,240,60);
        setBorder(BorderFactory.createLineBorder(Color.RED,3));

        JTextField textT = new JTextField("Cartes Tempete");
        JButton tire = new JButton("TireCarte");
        tire.setActionCommand("TireCarte");
        tire.addActionListener(this);

        add(textT);
        add(tire);

    }
    @Override
    public void update() {

    }

    public void actionPerformed(ActionEvent e){
        controleur.performAction(e.getActionCommand());
    }

}
class VueEquipment extends JFrame implements Observer,ActionListener{
    private DModele modele;
    private DControleur controleur;
    private player p;
    private JTextField inputX;
    private JTextField inputY;

    public VueEquipment(DModele modele, DControleur controleur, int n){
        this.modele = modele;
        this.controleur = controleur;
        p = this.modele.players.get(n);

        setName("Equipments");
        setSize(400,300);
        setVisible(true);


        for(Equipement tool: p.tools){
            if(! tool.used) {
                JButton b = new JButton(tool.nom);
                b.setActionCommand(tool.nom);
                b.addActionListener(this);
                add(b);
            }
        }
        inputX = new JTextField(10);
        JButton submitButtonX = new JButton("Submit");
        submitButtonX.setActionCommand("SUBMIT_INPUTX");
        submitButtonX.addActionListener(this);

        add(inputX);
        add(submitButtonX);

        inputY = new JTextField(10);
        JButton submitButtonY = new JButton("Submit");
        submitButtonY.setActionCommand("SUBMIT_INPUTY");
        submitButtonY.addActionListener(this);

        add(inputY);
        add(submitButtonY);
    }

    @Override
    public void update() {

    }

    public void actionPerformed(ActionEvent e){
        String toolName = e.getActionCommand();
        String x = new String();
        String y = new String();
        Equipement selectedTool = null;
        for (Equipement tool : p.tools) {
            if (tool.nom.equals(toolName)) {
                selectedTool = tool;
                break;
            }
        }
        if ("SUBMIT_INPUTX".equals(e.getActionCommand())) {
            x = inputX.getText();
            inputX.setText(""); // Clear the input field
        }
        if ("SUBMIT_INPUTY".equals(e.getActionCommand())) {
            y = inputX.getText();
            inputX.setText(""); // Clear the input field
        }
        if (selectedTool != null&& x!=null) {
            controleur.useTool(selectedTool,x,y);
        }
    }
}

class VueButtons extends JPanel implements Observer,ActionListener{
    private DModele modele;
    private DControleur controleur;

    public VueButtons(DModele modele,DControleur controleur){
        this.modele = modele;
        this.controleur = controleur;

        setBounds(1000,65,240,180);
        setBorder(BorderFactory.createLineBorder(Color.RED,3));

        JButton exploration = new JButton("exploration");
        exploration.setActionCommand("exploration");
        exploration.addActionListener(this);

        JButton fintour = new JButton("Fin de Tour");
        fintour.setActionCommand("finTour");
        fintour.addActionListener(this);

        JButton pickPiece = new JButton("Obetenir un piece");
        pickPiece.setActionCommand("pickPiece");
        pickPiece.addActionListener(this);

        JButton Ability = new JButton("Utilisation des compétences");
        Ability.setActionCommand("ability");
        Ability.addActionListener(this);

        add(exploration);
        add(pickPiece);
        add(fintour);
        add(Ability);


    }
    @Override
    public void update() {

    }

    public void actionPerformed(ActionEvent e){
        controleur.performAction(e.getActionCommand());
    }

}

class VueGrille extends JPanel implements Observer,ActionListener, MouseListener {
    private DModele modele;
    private DControleur controleur;
    private final static int TAILLE = 150;

    public VueGrille(DModele modele,DControleur controleur) {
        this.modele = modele;
        this.controleur = controleur;
        modele.addObserver(this);
        //Dimension dim = new Dimension(TAILLE*DModele.LARGEUR,TAILLE*DModele.HAUTEUR);
        //this.setPreferredSize(dim);
        setBounds(240,0,TAILLE*DModele.LARGEUR,TAILLE*DModele.HAUTEUR);
        addMouseListener(this);
    }

    public void update() { repaint(); }

    public void paintComponent(Graphics g) {
        super.repaint();


        Font currentFont = g.getFont();
        Font newFont = currentFont.deriveFont(currentFont.getSize() * 1F);
        g.setFont(newFont);

        for(int i=0; i<DModele.LARGEUR; i++) {
            for(int j=0; j<DModele.HAUTEUR; j++) {
                paint(g, modele.getCase(i, j), j*TAILLE, i*TAILLE);
            }
        }
        for(int i = 0; i < this.modele.players.size();i++){
            player p = this.modele.players.get(i);
            g.setColor(p.couleur);
            g.fillRect(p.position.get_y()*TAILLE + 20*i,p.position.get_x()*TAILLE+45,20,30);
        }
    }
    private void paint(Graphics g, Case c, int x, int y) {

        if (c instanceof tempete) {
            g.setColor(Color.GRAY);
        } else {
            g.setColor(Color.ORANGE);
        }

        g.fillRect(x, y, TAILLE-30, TAILLE-30);
        g.setColor(Color.BLACK);
        if (c.est_releve || (c instanceof oasis)) g.drawString(c.toString(),x+5,y+20);
        if(c.getSable() != 0){
            g.drawString("sable: " + c.getSable(),x+5,y+35);
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


class DControleur {
    protected DModele modele;
    protected DVue vue;
    protected ArrayList<Color> couleurs= new ArrayList<>();

    protected int currentPlayer = 0;
    protected int nbCarteTempeteTire = 0;
    public DControleur(DModele modele,DVue vue) {
        this.modele = modele;
        this.vue = vue;
        couleurs.add(Color.RED);
        couleurs.add(Color.YELLOW);
        couleurs.add(Color.BLUE);
        couleurs.add(Color.GRAY);
        couleurs.add(Color.PINK);
        int numPlayers = 0;
        boolean validInput = false;
        boolean validPlayerCount = false;
        boolean validDifficult = false;

        do {
            try {
                String numPlayersStr = JOptionPane.showInputDialog(vue, "Saisir le nombre de joueurs (2-5):", "Nombre de joueurs", JOptionPane.QUESTION_MESSAGE);

                if (numPlayersStr != null && !numPlayersStr.trim().isEmpty()) {
                    numPlayers = Integer.parseInt(numPlayersStr);

                    if (numPlayers >= 2 && numPlayers <= 5) {
                        validPlayerCount = true;
                        this.modele.nbJoueur = numPlayers;
                    } else {
                        JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre entre 2 et 5.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre valid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre valid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } while (!validPlayerCount);

        for (int i = 0; i < numPlayers; i++) {
            String playerName = null;
            do {
                playerName = JOptionPane.showInputDialog(vue, "Saisir joueur " + (i + 1) + " nom:", "Nom de Joueur", JOptionPane.QUESTION_MESSAGE);

                if (playerName != null && !playerName.trim().isEmpty()) {
                    player p = modele.tirerCarteJoueur(playerName,couleurs.get(i));
                    validInput = true;
                    JOptionPane.showMessageDialog(vue, "Vous avez biocher: " + p.getClass().getName(), "Pioche",JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vue, "Veuillez saisir un nom valid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    validInput = false;
                }
            } while (!validInput);
        }
        do {
            try {
                String numDiff = JOptionPane.showInputDialog(vue, "Saisir le niveau de la difficulte (1-4):", "Difficulte", JOptionPane.QUESTION_MESSAGE);

                if (numDiff != null && !numDiff.trim().isEmpty()) {
                    int diff = Integer.parseInt(numDiff);

                    if (diff >= 1 && diff <= 4) {
                        validDifficult = true;
                        this.modele.difficulte = diff;
                        this.modele.niveauTempete = diff;
                    } else {
                        JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre entre 1 et 4.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre valid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(vue, "Veuillez saisir un nombre valid.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        } while (!validDifficult);
    }

    public void deplace(int x, int y){
        player p = this.modele.players.get(currentPlayer);
        p.deplace(this.modele.cases[x][y]);

    }

    public void removeSand(int x, int y){
        player p = this.modele.players.get(currentPlayer);
        p.remove_sand(this.modele.cases[x][y]);
    }
    public void useTool(Equipement tool,String x,String y){
        player p = this.modele.players.get(currentPlayer);
        if(Integer.parseInt(x)>=0 && Integer.parseInt(x)<=4
            &&  Integer.parseInt(y)>=0 && Integer.parseInt(y)<=4){
                p.use_tool(tool, modele.cases[Integer.parseInt(x)][Integer.parseInt(x)]);
        }
    }

    public void performAction(String action) {
        // Perform the action based on the given action command
        player p = this.modele.players.get(currentPlayer);
        switch (action) {
            case "TireCarte":
                if(p.move == 0 && nbCarteTempeteTire < this.modele.niveauTempete){
                    Carte_Tempete ct = this.modele.tireCarteTempete();
                    JOptionPane.showMessageDialog(vue, "Vous avez biocher: " + ct.getClass().getName(), "Pioche",JOptionPane.INFORMATION_MESSAGE);
                    this.nbCarteTempeteTire++;
                }
                break;
            case "finTour":
                if (p.move == 0 && nbCarteTempeteTire == this.modele.niveauTempete) {
                    nbCarteTempeteTire = 0;
                    currentPlayer = (currentPlayer + 1) % this.modele.nbJoueur;
                    this.modele.players.get(currentPlayer).newturn();
                    JOptionPane.showMessageDialog(vue, "Joueurs: " + this.modele.players.get(currentPlayer).name, "Tour", JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "exploration":
                p.releveP();
                p.tire_tool();
                break;

            default:
                System.out.println("Unknown action command: " + action);
        }
    }
}

