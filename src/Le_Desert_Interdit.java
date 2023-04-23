import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;

public class Le_Desert_Interdit{
    public static void main(String[] args){
        EventQueue.invokeLater(() -> {
            DModele modele = new DModele();
            DVue vue = new DVue(modele);
        });
    }

}

interface Observer{
    void update();
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
    protected  int sableReste = 48;

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

    public boolean hasPiece(){
        return systeme_de_navigation || cristal_d_energie || boite_de_vitesses || helice;
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
    protected ArrayList<Equipement> tools = new ArrayList<>();
    public player(DModele modele, String name, Color c) {
        this.modele = modele;
        this.name = name;
        this.couleur = c;
    }
    abstract public String getDescription();
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
        if(this.position.est_releve && this.position instanceof tunnel){
            Case t1 = ((tunnel) this.position).getAutre1();
            Case t2 = ((tunnel) this.position).getAutre2();
            if(t1.est_releve && t1.getSable() < 2) list.add(t1);
            if(t2.est_releve && t2.getSable() < 2) list.add(t2);
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
        if(a.position == this.position && this.water > 1 && a.water<a.maxWater){
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
    public boolean deplace(Case c){
        if(this.casedispo().contains(c) && this.move > 0){
            this.position.players.remove(this);
            this.position = c;
            this.position.players.add(this);
            this.move = this.move -1;
            this.modele.notifyObservers();
            return true;
        }
        return false;
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

    public String tire_tool(){

        if(this.position.hasEquipement && this.position.est_releve){
            this.tools.add(this.position.modele.toolsCartes.get(0));
            String toolName = this.position.modele.toolsCartes.get(0).nom;
            this.position.hasEquipement = false;
            this.position.modele.toolsCartes.remove(0);
            return toolName;
        }
        return "Notool";
    }

    public void use_tool(Equipement tool, Case c){
        if(this.tools.contains(tool)){
            switch(tool.n_tool){
                case 1:
                    if(this.position.get_4().contains(c)){
                        while (c.retireSable(1)) {}
                        tool.used = true;
                        this.tools.remove(tool);
                    }
                    this.modele.notifyObservers();
                    break;
                case 2:
                    if(c.sable<=1 && ! (c instanceof tempete)){
                        this.position = c;
                        tool.used = true;
                        this.tools.remove(tool);
                    }
                    this.modele.notifyObservers();
                    break;
                case 3:
                    this.position.estBouclier = true;
                    tool.used = true;
                    this.tools.remove(tool);
                    this.modele.notifyObservers();
                    break;
                case 4:
                    this.modele.notifyObservers();
                    break;
                case 5:
                    this.move +=2;
                    tool.used = true;
                    this.tools.remove(tool);
                    this.modele.notifyObservers();
                    break;
                case 6:
                    ArrayList<player> list;
                    list = this.position.players;
                    for(player n: list){
                        n.ajouteGourde(2);
                    }
                    tool.used = true;
                    this.tools.remove(tool);
                    this.modele.notifyObservers();
                    break;
            }

        }
    }

    public void getPiece(){
        if(this.position.hasPiece() && this.move > 0){
            if(this.position.helice){
                this.modele.helice = true;
                this.position.helice = false;
            }else if(this.position.systeme_de_navigation){
                this.modele.systeme_de_navigation = true;
                this.position.systeme_de_navigation = false;
            }else if(this.position.boite_de_vitesses){
                this.modele.boite_de_vitesses = true;
                this.position.boite_de_vitesses = false;
            }else{
                this.modele.cristal_d_energie = true;
                this.position.cristal_d_energie = false;
            }
            this.move = this.move -1;
            this.modele.notifyObservers();
        }
    }

}

class archeologue extends player{
    public String description = "L’archéologue peut enlever 2 marqueurs\nSable sur la même tuile pour 1 action.";
    public String getDescription(){return this.description;}

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
    public String description = """
            L’alpiniste peut aller sur les tuiles bloquées
            (les tuiles ayant au moins 2 marqueurs Sable).
            Elle peut aussi emmener un autre joueur avec elle
            à chaque fois qu’elle se déplace. Tous les pions
            sur la tuile de l’alpiniste ne sont jamais enlisés
            et peuvent quitter la tuile de l’alpiniste même
            s’il y a 2 marqueurs Sable ou plus.""";
    public String getDescription(){return this.description;}
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


    public boolean deplaceAvec(Case c,player a) {
        if(this.position.players.contains(a) && this.casedispo().contains(c)) {
            a.position.players.remove(a);
            a.setCase(c);
            a.position.players.add(a);
            this.deplace(c);
            this.modele.notifyObservers();
            return true;
        }
        return false;
    }

}
class explorateur extends player{
    public String description = "L’explorateur peut se déplacer, enlever du sable\n" +
            "et utiliser les « Blasters » diagonalement.";
    public String getDescription(){return this.description;}
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
    public String description = """
            La météorologue peut dépenser des actions pour
            tirer, à la fin de son tour, moins de cartes Tempête
            (1 carte par action) que ne le nécessite le niveau actuel
            de la Tempête de sable. Elle peut aussi dépenser
            1 action pour regarder autant de cartes Tempête que
            son niveau actuel, puis en placer éventuellement
            une sous la pile. Les autres cartes sont remises
            sur le dessus de la pile dans l’ordre de son choix.""";
    public String getDescription(){return this.description;}
    public meteorologue(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
}
class navigateur extends player{
    public String description = """
            La navigatrice peut déplacer un autre joueur jusqu’à
            3 tuiles non bloquées par action, Tunnels inclus.
            Elle peut déplacer l’explorateur diagonalement
            et peut déplacer l’alpiniste sur les tuiles bloquées.
            Déplacée ainsi, l’alpiniste peut aussi utiliser son
            pouvoir et emmener un autre joueur (dont la navigatrice) !""";
    public String getDescription(){return this.description;}
    public navigateur(DModele modele, String name, Color c) {
        super(modele, name, c);
    }
}
class porteuse extends player{
    public String description = """
            La porteuse d’eau peut prendre 2 portions d’eau
            des tuiles « Point d’eau » déjà révélées pour 1 action.
            Elle peut aussi donner de l’eau aux joueurs sur les
            tuiles adjacentes gratuitement et à tout moment.
            Sa gourde commence avec 5 portions d’eau (au lieu de 4).""";
    public String getDescription(){return this.description;}

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
           this.ajouteGourde(2);
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
        boolean bv = false,h = false,cn = false,sn = false;
        if(this.modele.CaseTempete.hasPiece()){
            bv = modele.CaseTempete.boite_de_vitesses;
            modele.CaseTempete.boite_de_vitesses = false;
            h = modele.CaseTempete.helice;
            modele.CaseTempete.helice = false;
            cn = modele.CaseTempete.cristal_d_energie;
            modele.CaseTempete.cristal_d_energie = false;
            sn = modele.CaseTempete.systeme_de_navigation;
            modele.CaseTempete.systeme_de_navigation = false;
        }
        switch (direction) {
            case 0: //left
                for (int i = 0; i < distance; i++) {
                    if (ty > 0) {
                        Case tmp = this.modele.CaseTempete;
                        this.modele.cases[tx][ty] = this.modele.cases[tx][ty - 1];
                        this.modele.cases[tx][ty].setY(ty);
                        this.modele.cases[tx][ty].ajouteSable();
                        if(bv) {
                            this.modele.cases[tx][ty].boite_de_vitesses = true;
                            bv = false;
                        }
                        if(h) {
                            this.modele.cases[tx][ty].helice = true;
                            h = false;
                        }
                        if(cn){
                            this.modele.cases[tx][ty].cristal_d_energie = true;
                            cn = false;
                        }
                        if(sn){
                            this.modele.cases[tx][ty].systeme_de_navigation = true;
                            sn = false;
                        }
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
                        if(bv) {
                            this.modele.cases[tx][ty].boite_de_vitesses = true;
                            bv = false;
                        }
                        if(h) {
                            this.modele.cases[tx][ty].helice = true;
                            h = false;
                        }
                        if(cn){
                            this.modele.cases[tx][ty].cristal_d_energie = true;
                            cn = false;
                        }
                        if(sn){
                            this.modele.cases[tx][ty].systeme_de_navigation = true;
                            sn = false;
                        }
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
                        if(bv) {
                            this.modele.cases[tx][ty].boite_de_vitesses = true;
                            bv = false;
                        }
                        if(h) {
                            this.modele.cases[tx][ty].helice = true;
                            h = false;
                        }
                        if(cn){
                            this.modele.cases[tx][ty].cristal_d_energie = true;
                            cn = false;
                        }
                        if(sn){
                            this.modele.cases[tx][ty].systeme_de_navigation = true;
                            sn = false;
                        }
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
                        if(bv) {
                            this.modele.cases[tx][ty].boite_de_vitesses = true;
                            bv = false;
                        }
                        if(h) {
                            this.modele.cases[tx][ty].helice = true;
                            h = false;
                        }
                        if(cn){
                            this.modele.cases[tx][ty].cristal_d_energie = true;
                            cn = false;
                        }
                        if(sn){
                            this.modele.cases[tx][ty].systeme_de_navigation = true;
                            sn = false;
                        }
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
        VuePieces pieces = new VuePieces(modele,controleur);

        frame.add(pieces);
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
        piece1 = new JTextArea("Helice: " + this.modele.helice
                +"\nboite de vitesse: "+ this.modele.boite_de_vitesses);
        piece2 = new JTextArea("Systeme de navigation: " + this.modele.systeme_de_navigation
                +"\ncristal d'énergie: "+ this.modele.cristal_d_energie);

        add(textT);
        add(textS);
        add(piece1);
        add(piece2);

    }
    @Override
    public void update() {
        textT.setText("Niveau tempete: " + this.modele.niveauTempete);
        textS.setText("Total Sable: " + this.modele.sableReste);
        piece1.setText("Helice: " + this.modele.helice
                +"\nboite de vitesse: "+ this.modele.boite_de_vitesses);
        piece2.setText("Systeme de navigation: " + this.modele.systeme_de_navigation
                +"\ncristal d'énergie: "+ this.modele.cristal_d_energie);
        repaint();
    }

    public void actionPerformed(ActionEvent e){
        controleur.performAction(e.getActionCommand());
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
        setBounds(0,165,240,600);
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

class VuePieces extends JPanel {
    private DModele modele;
    private DControleur controleur;
    private int shapeSize = 20;

    public VuePieces(DModele modele, DControleur controleur){
        this.modele = modele;
        this.controleur = controleur;

        setBounds(1000,250,240,400);
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the shapes and their labels
        g.setColor(Color.BLUE);
        g.fillOval(10, 10, shapeSize, shapeSize);
        g.setColor(Color.BLACK);
        g.drawString("Systeme de navigation", 40, 25);

        g.setColor(Color.GREEN);
        g.fillRect(10, 40, shapeSize, shapeSize);
        g.setColor(Color.BLACK);
        g.drawString("Cristal d'energie", 40, 55);

        g.setColor(Color.RED);
        int[] xPoints = {10, 10 + (shapeSize / 2), 10 + shapeSize};
        int[] yPoints = {70, 90, 70};
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(Color.BLACK);
        g.drawString("Helice", 40, 75);

        g.setColor(Color.YELLOW);
        int[] xPointsStar = {10, 30, 20};
        int[] yPointsStar = {100, 100, 120};
        g.fillPolygon(xPointsStar, yPointsStar, 3);
        int[] xPointsStar2 = {10, 30, 20};
        int[] yPointsStar2 = {120, 120, 100};
        g.fillPolygon(xPointsStar2, yPointsStar2, 3);
        g.setColor(Color.BLACK);
        g.drawString("Boite de vitesses", 40, 115);
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
        setBounds(245,0,TAILLE*DModele.LARGEUR,TAILLE*DModele.HAUTEUR);
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

        int shapeSize = 10;

        if (c.systeme_de_navigation) {
            // Draw a circle
            g.setColor(Color.BLUE);
            g.fillOval(x + 5, y + 5, shapeSize, shapeSize);
        }
        if (c.cristal_d_energie) {
            // Draw a square
            g.setColor(Color.GREEN);
            g.fillRect(x + TAILLE - 30 - 5 - shapeSize, y + 5, shapeSize, shapeSize);
        }
        if (c.helice) {
            // Draw a triangle
            g.setColor(Color.RED);
            int[] xPoints = {x + 5, x + 5 + (shapeSize / 2), x + 5 + shapeSize};
            int[] yPoints = {y + TAILLE - 30 - 5 - shapeSize, y + TAILLE - 30 - 5, y + TAILLE - 30 - 5 - shapeSize};
            g.fillPolygon(xPoints, yPoints, 3);
        }
        if (c.boite_de_vitesses) {
            // Draw a star
            g.setColor(Color.YELLOW);
            int[] xPoints = {x + TAILLE - 30 - 5 - shapeSize, x + TAILLE - 30 - 5, x + TAILLE - 30 - 5 - (shapeSize / 2)};
            int[] yPoints = {y + TAILLE - 30 - 5 - shapeSize, y + TAILLE - 30 - 5 - shapeSize, y + TAILLE - 30 - 5};
            g.fillPolygon(xPoints, yPoints, 3);
            int[] xPoints2 = {x + TAILLE - 30 - 5 - shapeSize, x + TAILLE - 30 - 5, x + TAILLE - 30 - 5 - (shapeSize / 2)};
            int[] yPoints2 = {y + TAILLE - 30 - 5, y + TAILLE - 30 - 5 - shapeSize, y + TAILLE - 30 - 5 - (shapeSize / 2)};
            g.fillPolygon(xPoints2, yPoints2, 3);
        }

        g.setColor(Color.BLACK);
        if (c.est_releve || (c instanceof oasis)) g.drawString(c.toString(), x + 5, y + 20);
        if (c.getSable() != 0) {
            g.drawString("sable: " + c.getSable(), x + 5, y + 35);
        }
        if(c.estBouclier){
            g.drawString("Bouclier", x+100,y+5);
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
                    JOptionPane.showMessageDialog(vue, "Vous avez biocher: " + p.getClass().getName()+"\n\n"+p.getDescription(), "Pioche",JOptionPane.INFORMATION_MESSAGE);
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
        this.modele.win();
        if (modele.estGagne) {
            JOptionPane.showMessageDialog(vue, "Félicitations! Vous avez gagné!", "Victoire", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (modele.estPerdu) {
            JOptionPane.showMessageDialog(vue, "Dommage! Vous avez perdu!", "Défaite", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public void removeSand(int x, int y){
        player p = this.modele.players.get(currentPlayer);
        p.remove_sand(this.modele.cases[x][y]);
    }


    public void performAction(String action) {
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
                String s = p.tire_tool();
                if(s!="Notool"){
                    JOptionPane.showMessageDialog(vue,"Player" + p.name+" Obtenir "+s,"Equipment",JOptionPane.INFORMATION_MESSAGE);
                }
                break;
            case "DonnerEau0":
                player objet0 = this.modele.players.get(0);
                if(objet0 != p) p.shareWater(objet0);
                break;
            case "DonnerEau1":
                player objet1 = this.modele.players.get(1);
                if(objet1 != p) p.shareWater(objet1);
                break;
            case "DonnerEau2":
                player objet2 = this.modele.players.get(2);
                if(objet2 != p) p.shareWater(objet2);
                break;
            case "DonnerEau3":
                player objet3 = this.modele.players.get(3);
                if(objet3 != p) p.shareWater(objet3);
                break;
            case "DonnerEau4":
                player objet4 = this.modele.players.get(4);
                if(objet4 != p) p.shareWater(objet4);
                break;
            case "equip0":
                player p0 = this.modele.players.get(0);
                String e0;
                String coord0;
                String alltool0 = "";
                for(Equipement tool : p0.tools){
                    alltool0 = alltool0 + tool.nom +" ";
                }

                e0 = JOptionPane.showInputDialog(vue, "tool possesed: "+alltool0);
                Equipement selectedTool0 = null;
                for (Equipement tool : p.tools) {
                    if (tool.nom.equals(e0)) {
                        selectedTool0 = tool;
                        break;
                    }
                }
                if(selectedTool0!=null) {
                    if(selectedTool0.nom == "bouclier" || selectedTool0.nom =="boost" || selectedTool0.nom =="reserveEau")
                        p0.use_tool(selectedTool0, this.modele.getCase(0, 0));
                    else{
                        int X0, Y0;
                        do {
                            coord0 = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates0 = coord0.split(",");

                            X0 = Integer.parseInt(coordinates0[0].trim());
                            Y0 = Integer.parseInt(coordinates0[1].trim());
                        } while (X0 < 0 || X0 > 4 || Y0 < 0 || Y0 > 4);

                        if (selectedTool0.nom == "terrascope") {
                            String equipment_status;
                            if (this.modele.getCase(X0, Y0).hasEquipement) equipment_status = "has equipment";
                            else equipment_status = "no equipment";
                            JOptionPane.showMessageDialog(vue, this.modele.getCase(X0, Y0).getClass().getSimpleName() + " " + equipment_status, "terrascope", JOptionPane.INFORMATION_MESSAGE);
                        } else p0.use_tool(selectedTool0, this.modele.getCase(X0, Y0));
                    }
                }
                break;
            case "equip1":
                player p1 = this.modele.players.get(1);
                String e1;
                String coord1;
                String alltool1 = new String();
                for(Equipement tool : p1.tools){
                    alltool1 = alltool1 + tool.nom +" ";
                }

                e1 = JOptionPane.showInputDialog(vue, "tool possesed: "+alltool1);
                Equipement selectedTool1 = null;
                for (Equipement tool : p.tools) {
                    if (tool.nom.equals(e1)) {
                        selectedTool1 = tool;
                        break;
                    }
                }
                if(selectedTool1!=null) {
                    if(selectedTool1.nom == "bouclier" || selectedTool1.nom =="boost" || selectedTool1.nom =="reserveEau")
                        p1.use_tool(selectedTool1, this.modele.getCase(0, 0));
                    else {
                        int X1, Y1;
                        do {
                            coord1 = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates1 = coord1.split(",");

                            X1 = Integer.parseInt(coordinates1[0].trim());
                            Y1 = Integer.parseInt(coordinates1[1].trim());
                        } while (X1 < 0 || X1 > 4 || Y1 < 0 || Y1 > 4);

                        if (selectedTool1.nom == "terrascope") {
                            String equipment_status;
                            if (this.modele.getCase(X1, Y1).hasEquipement) equipment_status = "has equipment";
                            else equipment_status = "no equipment";
                            JOptionPane.showMessageDialog(vue, this.modele.getCase(X1, Y1).getClass().getSimpleName() + " " + equipment_status, "terrascope", JOptionPane.INFORMATION_MESSAGE);
                        }
                        p1.use_tool(selectedTool1, this.modele.getCase(X1, Y1));
                    }
                }
                break;
            case "equip2":
                player p2 = this.modele.players.get(2);
                String e2;
                String coord2;
                String alltool2 = "";
                for(Equipement tool : p2.tools){
                    alltool2 = alltool2 + tool.nom +" ";
                }

                e2 = JOptionPane.showInputDialog(vue, "tool possesed: "+alltool2);
                Equipement selectedTool2 = null;
                for (Equipement tool : p.tools) {
                    if (tool.nom.equals(e2)) {
                        selectedTool2 = tool;
                        break;
                    }
                }
                if(selectedTool2!=null) {
                    if(selectedTool2.nom == "bouclier" || selectedTool2.nom =="boost" || selectedTool2.nom =="reserveEau")
                        p2.use_tool(selectedTool2, this.modele.getCase(0, 0));
                    else {
                        int X2, Y2;
                        do {
                            coord2 = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates2 = coord2.split(",");

                            X2 = Integer.parseInt(coordinates2[0].trim());
                            Y2 = Integer.parseInt(coordinates2[1].trim());
                        } while (X2 < 0 || X2 > 4 || Y2 < 0 || Y2 > 4);

                        if (selectedTool2.nom == "terrascope") {
                            String equipment_status;
                            if (this.modele.getCase(X2, Y2).hasEquipement) equipment_status = "has equipment";
                            else equipment_status = "no equipment";
                            JOptionPane.showMessageDialog(vue, this.modele.getCase(X2, Y2).getClass().getSimpleName() + " " + equipment_status, "terrascope", JOptionPane.INFORMATION_MESSAGE);
                        }
                        p2.use_tool(selectedTool2, this.modele.getCase(X2, Y2));
                    }
                }
                break;

            case "equip3":
                player p3 = this.modele.players.get(3);
                String e3;
                String coord3;
                String alltool3 = "";
                for(Equipement tool : p3.tools){
                    alltool3 = alltool3 + tool.nom +" ";
                }

                e3 = JOptionPane.showInputDialog(vue, "tool possesed: "+alltool3);
                Equipement selectedTool3 = null;
                for (Equipement tool : p.tools) {
                    if (tool.nom.equals(e3)) {
                        selectedTool3 = tool;
                        break;
                    }
                }
                if(selectedTool3!=null) {
                    if(selectedTool3.nom == "bouclier" || selectedTool3.nom =="boost" || selectedTool3.nom =="reserveEau")
                        p3.use_tool(selectedTool3, this.modele.getCase(0, 0));
                    else {
                        int X3, Y3;
                        do {
                            coord3 = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates3 = coord3.split(",");

                            X3 = Integer.parseInt(coordinates3[0].trim());
                            Y3 = Integer.parseInt(coordinates3[1].trim());
                        } while (X3 < 0 || X3 > 4 || Y3 < 0 || Y3 > 4);

                        if (selectedTool3.nom == "terrascope") {
                            String equipment_status;
                            if (this.modele.getCase(X3, Y3).hasEquipement) equipment_status = "has equipment";
                            else equipment_status = "no equipment";
                            JOptionPane.showMessageDialog(vue, this.modele.getCase(X3, Y3).getClass().getSimpleName() + " " + equipment_status, "terrascope", JOptionPane.INFORMATION_MESSAGE);
                        }
                        p3.use_tool(selectedTool3, this.modele.getCase(X3, Y3));
                    }
                }
                break;

            case "equip4":
                player p4 = this.modele.players.get(4);
                String e4;
                String coord4;
                String alltool4 = "";
                for(Equipement tool : p4.tools){
                    alltool4 = alltool4 + tool.nom +" ";
                }

                e4 = JOptionPane.showInputDialog(vue, "tool possesed: "+alltool4);
                Equipement selectedTool4 = null;
                for (Equipement tool : p.tools) {
                    if (tool.nom.equals(e4)) {
                        selectedTool4 = tool;
                        break;
                    }
                }
                if(selectedTool4!=null) {
                    if(selectedTool4.nom == "bouclier" || selectedTool4.nom =="boost" || selectedTool4.nom =="reserveEau")
                        p4.use_tool(selectedTool4, this.modele.getCase(0, 0));
                    else {
                        int X4, Y4;
                        do {
                            coord4 = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates4 = coord4.split(",");

                            X4 = Integer.parseInt(coordinates4[0].trim());
                            Y4 = Integer.parseInt(coordinates4[1].trim());
                        } while (X4 < 0 || X4 > 4 || Y4 < 0 || Y4 > 4);

                        if (selectedTool4.nom == "terrascope") {
                            String equipment_status;
                            if (this.modele.getCase(X4, Y4).hasEquipement) equipment_status = "has equipment";
                            else equipment_status = "no equipment";
                            JOptionPane.showMessageDialog(vue, this.modele.getCase(X4, Y4).getClass().getSimpleName() + " " + equipment_status, "terrascope", JOptionPane.INFORMATION_MESSAGE);
                        }
                        p4.use_tool(selectedTool4, this.modele.getCase(X4, Y4));
                    }
                }
                break;
            case "pickPiece":
                p.getPiece();
                break;
            case "equipTire":
                p.tire_tool();
                break;
            case "ability":
                if(p.move > 0) {
                    if (p instanceof porteuse) ((porteuse) p).getWater();
                    if (p instanceof alpiniste) {
                        int targetPlayerId;
                        String playerIdInput;
                        do {
                            playerIdInput = JOptionPane.showInputDialog(vue, "Entrez l'ID du joueur à déplacer avec l'Alpiniste (entre 0 et " + (this.modele.nbJoueur - 1) + "):");
                            targetPlayerId = Integer.parseInt(playerIdInput);
                        } while (targetPlayerId < 0 || targetPlayerId >= this.modele.nbJoueur);

                        int targetX, targetY;
                        String coordinateInput;
                        do {
                            coordinateInput = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                            String[] coordinates = coordinateInput.split(",");

                            targetX = Integer.parseInt(coordinates[0].trim());
                            targetY = Integer.parseInt(coordinates[1].trim());
                        } while (targetX < 0 || targetX > 4 || targetY < 0 || targetY > 4);

                        player targetPlayer = modele.players.get(targetPlayerId);
                        Case targetCase = modele.cases[targetX][targetY];

                        ((alpiniste) p).deplaceAvec(targetCase, targetPlayer);
                    }
                    if (p instanceof meteorologue) {
                        String abilityInput;
                        int abilityUse;
                        do {
                            abilityInput = JOptionPane.showInputDialog(vue, """
                                    Saisissez le numéro de la compétence que vous souhaitez utiliser：
                                    1.
                                    Dépenser des actions pour tirer, à la fin de son tour, moins de cartes Tempête
                                    (1 carte par action) que ne le nécessite le niveau actuel de la Tempête de sable.
                                    2.
                                    dépenser 1 action pour regarder autant de cartes Tempête
                                    que son niveau actuel, puis en placer éventuellement
                                    une sous la pile. Les autres cartes sont remises
                                    sur le dessus de la pile dans l’ordre de son choix"""
                            );
                            abilityUse = Integer.parseInt(abilityInput);
                        } while (!(abilityUse == 1 || abilityUse == 2));
                        if (abilityUse == 1) {
                            nbCarteTempeteTire++;
                            p.move--;
                        }else {
                            p.move--;
                            ArrayList<Carte_Tempete> drawnCards = new ArrayList<>();
                            for (int i = 0; i < this.modele.niveauTempete; i++) {
                                if (this.modele.cartes.size() > i) {
                                    drawnCards.add(this.modele.cartes.remove(i));
                                }
                            }

                            String drawnCardsString = drawnCards.stream()
                                    .map(card -> card.getClass().getSimpleName())
                                    .collect(Collectors.joining(", "));

                            String bottomCardInput;
                            int bottomCardIndex;
                            do {
                                bottomCardInput = JOptionPane.showInputDialog(vue, "Cartes tirées: " + drawnCardsString +
                                        "\nEntrez l'index de la carte à placer sous la pile (entre 1 et " + drawnCards.size() + ", ou 0 pour ne pas en placer):");
                                bottomCardIndex = Integer.parseInt(bottomCardInput);
                            } while (bottomCardIndex < 0 || bottomCardIndex > drawnCards.size());

                            Carte_Tempete bottomCard = null;
                            if (bottomCardIndex != 0) {
                                bottomCard = drawnCards.remove(bottomCardIndex - 1);
                            }
                            if(!drawnCards.isEmpty()) {
                                drawnCardsString = drawnCards.stream()
                                        .map(card -> card.getClass().getSimpleName())
                                        .collect(Collectors.joining(", "));

                                String reorderedCardsInput = JOptionPane.showInputDialog(vue, "Cartes restantes: " + drawnCardsString +
                                        "\nEntrez l'ordre des cartes restantes, séparées par des virgules:");

                                String[] reorderedCardIndexes = reorderedCardsInput.split(",");
                                ArrayList<Carte_Tempete> reorderedCards = new ArrayList<>();

                                for (String indexStr : reorderedCardIndexes) {
                                    int cardIndex = Integer.parseInt(indexStr.trim()) - 1;
                                    reorderedCards.add(drawnCards.get(cardIndex));
                                }

                                for (int i = reorderedCards.size() - 1; i >= 0; i--) {
                                    this.modele.cartes.add(0, reorderedCards.get(i));
                                }
                            }
                            if (bottomCard != null) {
                                this.modele.cartes.add(bottomCard);
                            }
                        }

                    }
                    if (p instanceof navigateur){
                        p.move--;
                        int targetPlayerId;
                        String playerIdInput;
                        do {
                            playerIdInput = JOptionPane.showInputDialog(vue, "Entrez l'ID du joueur à naviguer (entre 0 et " + (this.modele.nbJoueur - 1) + "):");
                            targetPlayerId = Integer.parseInt(playerIdInput);
                        } while (targetPlayerId < 0 || targetPlayerId >= this.modele.nbJoueur);
                        int n = 3;
                        int targetX, targetY;
                        String coordinateInput;
                        player targetPlayer = modele.players.get(targetPlayerId);
                        targetPlayer.move+=3;
                        boolean useAlpinistAbility = false;
                        while(n > 0) {
                            do {
                                coordinateInput = JOptionPane.showInputDialog(vue, "Entrez les coordonnées cibles (x, y) séparées par une virgule (entre (0,0) et (4,4)):");
                                String[] coordinates = coordinateInput.split(",");

                                targetX = Integer.parseInt(coordinates[0].trim());
                                targetY = Integer.parseInt(coordinates[1].trim());
                            } while (targetX < 0 || targetX > 4 || targetY < 0 || targetY > 4);

                            Case targetCase = modele.cases[targetX][targetY];
                            if (targetPlayer instanceof alpiniste) {
                                int choice = JOptionPane.showOptionDialog(vue, "Voulez-vous utiliser l'aptitude de l'Alpiniste pour déplacer quelqu'un avec lui?",
                                        "Choix d'aptitude", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                                useAlpinistAbility = (choice == JOptionPane.YES_OPTION);
                            }

                            if (useAlpinistAbility) {
                                // Handle Alpinist's ability to move with someone else
                                int otherPlayerId;
                                do {
                                    playerIdInput = JOptionPane.showInputDialog(vue, "Entrez l'ID du joueur à déplacer avec l'Alpiniste (entre 0 et " + (this.modele.nbJoueur - 1) + "):");
                                    otherPlayerId = Integer.parseInt(playerIdInput);
                                } while (otherPlayerId < 0 || otherPlayerId >= this.modele.nbJoueur || otherPlayerId == targetPlayerId);

                                player otherPlayer = modele.players.get(otherPlayerId);
                                if (((alpiniste) targetPlayer).deplaceAvec(targetCase, otherPlayer)) {
                                    n--;
                                }
                            } else {
                                // Normal move
                                if (targetPlayer.deplace(targetCase)) {
                                    n--;
                                }
                            }
                        }
                    }
                }
                break;
            default:
                System.out.println("Unknown action command: " + action);
        }
        this.modele.win();
        if (modele.estGagne) {
            JOptionPane.showMessageDialog(vue, "Félicitations! Vous avez gagné!", "Victoire", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        } else if (modele.estPerdu) {
            JOptionPane.showMessageDialog(vue, "Dommage! Vous avez perdu!", "Défaite", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}

