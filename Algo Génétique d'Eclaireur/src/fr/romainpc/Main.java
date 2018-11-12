/*******************************************************************************
 * Copyright (C) 2017 ROMAINPC_LECHAT
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package fr.romainpc;
	
import java.util.Optional;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;


public class Main extends Application {
	
	private static int nbIndividus;
	private static Group individus = new Group();
	
	
	private static int generation = 1;
	private static Population pop;
	private static int nbSelection;
	
	
	public static AnimationTimer boucle;
	public static int indiceGene; 
	public static int nbEnVie; 
	
	public static Timeline affAnimation;
	public static Timeline affRang;
	public static Timeline affRoulette;
	public static Timeline affElit;
	public static Timeline affCroisement;
	public static Timeline affMut;
	public static Timeline affPop;
	public static Timeline affBest;
	public static double bestD;
	public static int bestDDV;
	
	//boolean en cas de fichier non trouv�
	public static boolean toutBienPasse = true;
	public static boolean soluce = false;
	

	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			//g�n�ration fen�tre:
			
			
			Group root = new Group();
			Scene scene = new Scene(root,1200,800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setFill(Color.rgb(20, 20, 30));
			
			
			
			//groupes d'interface:
			VBox HUDinfos = new VBox();
			
			
			
			Group HUDmap = new Group();
			ScrollPane HUDmapSP = new ScrollPane(); HUDmapSP.setVbarPolicy(ScrollBarPolicy.ALWAYS); HUDmapSP.setHbarPolicy(ScrollBarPolicy.ALWAYS);
			HUDmapSP.minHeightProperty().bind(scene.heightProperty()); HUDmapSP.minWidthProperty().bind(scene.widthProperty().multiply(0.7));
			HUDmapSP.maxHeightProperty().bind(scene.heightProperty()); HUDmapSP.maxWidthProperty().bind(scene.widthProperty().multiply(0.7));
			
			HUDmapSP.setContent(HUDmap);
			
			
			HUDinfos.layoutXProperty().bind(scene.widthProperty().subtract(HUDinfos.widthProperty()));
			Console console = new Console();
			console.setVbarPolicy(ScrollBarPolicy.ALWAYS); console.setHbarPolicy(ScrollBarPolicy.ALWAYS);
			console.minHeightProperty().bind(scene.heightProperty().divide(1.3));console.maxHeightProperty().bind(scene.heightProperty().divide(2));
			console.minWidthProperty().bind(scene.widthProperty().multiply(0.3));console.maxWidthProperty().bind(scene.widthProperty().multiply(0.3));
			
			HUDinfos.getChildren().add(console);
			
			root.getChildren().addAll(HUDinfos, HUDmapSP);
			
			
			generation = 1;
			//autres �l�ments:
			Label numGen = new Label("G�n�ration 1");
			numGen.setTextFill(Color.RED);
			numGen.setFont(Font.font(42));
			HUDinfos.getChildren().add(numGen);
			HUDinfos.setAlignment(Pos.CENTER);
			
			Label nbVivant = new Label();
			nbVivant.setTextFill(Color.DEEPSKYBLUE);
			nbVivant.setFont(Font.font(32));
			HUDinfos.getChildren().add(nbVivant);
			HUDinfos.setAlignment(Pos.CENTER);
			
			primaryStage.getIcons().add(new Image(getClass().getResource("/icone.png").toExternalForm()));
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
			
			
			
			//choix du nb d'individus par g�n�ration:
			String nb = "";
			Optional<String> r = null;
			while(!isEntier(nb)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Veuillez s�lectionner le nombre d'individus par g�n�ration:\n"
				+ "N < 50 et N > 200 interdit");
			selN.setTitle("S�lection du nombre d'individus");
			r = selN.showAndWait();
			if(r.isPresent())nb = r.get();
			}
			nbIndividus = Integer.valueOf(r.get());
			console.afficher(nbIndividus + " Individus par g�n�ration", Color.rgb(0, 255, 0));
			nbVivant.setText("Encore en vie: " + nbIndividus);
			
			//choix du classement par dominance avec ou sans prise en compte du temps de vie:
			String nbChoix1 = "";
			Optional<String> rVie = null;
			while(!isChoix1(nbChoix1)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Veuillez indiquer si OUI (entrez 1) ou NON (entrez 0) vous prenez en compte\n" + "la dur�e de vie en plus de la distance � l'arriv�e pour le classement:\n"+"(faible dur�e de vie => meilleur au classement)");
			selN.setTitle("S�lection du syst�me de dominance");
			rVie = selN.showAndWait();
			if(rVie.isPresent())nbChoix1 = rVie.get();
			}
			boolean dureeDeVie;
			if(Integer.valueOf(rVie.get()) == 0){
				dureeDeVie = false;
				console.afficher("Dur�e de vie: NON", Color.rgb(0, 255, 0));
			}else{
				dureeDeVie = true;
				console.afficher("Dur�e de vie: OUI", Color.rgb(0, 255, 0));
			}
			
			//choix du mode de s�lection:
			String nbChoix2 = "";
			Optional<String> rSel = null;
			while(!isChoix1(nbChoix2)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Pour une s�lection par roulette proportionnelle entrez 0\n" + "Pour une s�lection par �litisme entrez 1");
			selN.setTitle("S�lection du syst�me de s�lection");
			rSel = selN.showAndWait();
			if(rSel.isPresent())nbChoix2 = rSel.get();
			}
			boolean elitisme;
			if(Integer.valueOf(rSel.get()) == 0){
				elitisme = false;
				console.afficher("S�lection par roulette", Color.rgb(0, 255, 0));
			}else{
				elitisme = true;
				console.afficher("S�lection par �litisme", Color.rgb(0, 255, 0));
			}
			
			//choix du nombre de parents:
			String nbPar = "";
			Optional<String> rPar = null;
			while(!isEntier2(nbPar)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez s�lectionner le pourcentage de parents:\n"
				+ "Valeurs conseill�es: 10 - 20 %			P < 5 et P > 70 interdit");
			selM.setTitle("S�lection du nombre de parents");
			rPar = selM.showAndWait();
			if(rPar.isPresent())nbPar = rPar.get();
			}
			int PrPar = Integer.valueOf(nbPar);
			
			
			//nombre � s�lectionner:
			nbSelection = (int)((double)nbIndividus / 100 * PrPar);
			console.afficher("Nombre d'individus parents: " + nbSelection, Color.rgb(0, 255, 0));
			
			
			//choix de la probabilit� de mutation
			String nbMut = "";
			Optional<String> r3 = null;
			while(!isDouble(nbMut)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez s�lectionner la probabilit� de mutation des g�nes:\n"
				+ "Valeurs conseill�es: 0.05 - 0.1			P < 0.01 et P > 0.5 interdit");
			selM.setTitle("S�lection de la probabilit� de mutation");
			r3 = selM.showAndWait();
			if(r3.isPresent())nbMut = r3.get();
			}
			double PMut = Double.valueOf(nbMut);
			console.afficher("Probabilt� de mutation: " + PMut, Color.rgb(0, 255, 0));
			
			
			
			
			
			//choix du fichier carte:
			String nomMap = "";
			while(nomMap.equals("")){
			TextInputDialog boite = new TextInputDialog();
			boite.initOwner(primaryStage);
			boite.setTitle("S�lection de la carte");
			boite.setHeaderText("Entrez le nom de la carte � exp�rimenter:");
			boite.setContentText("Placez ce fichier .txt valable � c�t� de l'ex�cutable de ce programme.");
			Optional<String> r2 = boite.showAndWait(); if(r2.isPresent())nomMap = r2.get();}
			
			
			
			
			//chargement de la carte:
			int[][] carte = Carte.recupCarte(nomMap);
			int lD = Carte.getlD();
			int cD = Carte.getcD();
			int lA = Carte.getlA();
			int cA = Carte.getcA();
			int nbLig = Carte.getnbLig();
			int nbCol = Carte.getnbCol();
			console.afficher("Carte "+nomMap+" charg�e.", Color.rgb(0, 255, 0));
			
			//affichage carte:
			HUDmap.getChildren().add(Carte.afficher());
			
			//longueur du g�nome:
			String nbGen = "";
			Optional<String> rG = null;
			while(!isDouble2(nbGen)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez s�lectionner le facteur F:\n"
				+ "la longueur des g�nomes est d�finie par (LargeurCarte + LongueurCarte) x F\n"
				+ "Valeurs conseill�es: 1.5			F < 1 et F > 30 interdit");
			selM.setTitle("S�lection de la longueur du g�nome");
			rG = selM.showAndWait();
			if(rG.isPresent())nbGen = rG.get();
			}
			double facteur = Double.valueOf(nbGen);
			int longueurGen = (int) ((double)(nbLig + nbCol) * facteur);
			console.afficher("Longueur du g�nome d�finie sur : " + longueurGen, Color.rgb(0, 255, 0));
			
			
			
			//cr�ation de la population initiale:
			
			HUDmap.getChildren().add(individus);
			pop = new Population(nbIndividus, longueurGen, lD, cD);
			console.afficher("Population initiale cr��e.", Color.rgb(255, 255, 0));
			
			
			//BOUCLE d'animations et de r�it�ration du processus:
			
			if(toutBienPasse){
			
				//Timers pour afficher dans la console:
				affAnimation = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Simulation de la g�n�ration " + generation, Color.DEEPSKYBLUE);
					console.afficher(". . .", Color.DEEPSKYBLUE);
				}));
				affBest = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Meilleure distance: " + bestD, Color.DEEPPINK);
					if(dureeDeVie)
						console.afficher("Meilleure dur�e de vie(=plus courte): " + bestDDV, Color.DEEPPINK);
				}));
				affRang = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Classement par nombre de dominants", Color.YELLOW);
				}));
				affRoulette = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher(nbSelection+" parents s�lectionn�s par Roulette", Color.YELLOW);
				}));
				affElit = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher(nbSelection+" parents s�lectionn�s par Elitisme", Color.YELLOW);
				}));
				affCroisement = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Croisement des " + nbSelection + " parents -> "+ (nbIndividus - nbSelection) + " enfants", Color.YELLOW);
				}));
				affMut = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Mutations", Color.YELLOW);
				}));
				affPop = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Nouvelle population", Color.YELLOW);
				}));
				
				
			Calculeur calculs = new Calculeur(nbIndividus, longueurGen, nbSelection, PMut, dureeDeVie, elitisme);
			
			//s�curit�:
			primaryStage.setOnCloseRequest(e -> calculs.interrupt());
			
			
			
			
			boucle = new AnimationTimer(){
				public void handle(long l){
					
					if(indiceGene == 0){
						individus.getChildren().clear();
						for(int i = 0 ; i < pop.size() ; i++)
							individus.getChildren().add(pop.get(i));
						nbVivant.setText("Encore en vie: " + nbEnVie);
					}
					
					
					
					for(int i = 0 ; i < nbIndividus ; i++){
						if(pop.get(i).isEnVie()){
							//plantage:
							if(carte[(int) (pop.get(i).getLayoutY() / 20)][(int) (pop.get(i).getLayoutX() / 20)] == 1){
								pop.get(i).planter();
								nbVivant.setText("Encore en vie: " + nbEnVie);
							}
						
							//d�tection arriv�e:
							if(carte[(int) (pop.get(i).getLayoutY() / 20)][(int) (pop.get(i).getLayoutX() / 20)] == 3){
								generation --;
								soluce = true;
								int x = cD ; int y = lD;
								for(int loop = 0 ; loop < longueurGen ; loop++){
									switch(pop.get(i).getGene(loop)){
									case N: y--; break;
									case NE: y--; x++; break;
									case E: x++; break;
									case SE: y++; x++; break;
									case S: y++; break;
									case SW: y++; x--; break;
									case W: x--; break;
									case NW: y--; x--; break;
									}
									if(x == cA && y == lA)
										break;
									HUDmap.getChildren().add(new Circle(x * 20 + 10, y * 20 + 10, 7, Color.ORANGERED));
									
								}
								console.afficher("<-----SOLUTION TROUVEE----->", Color.RED);
								console.afficher("Conclusion:", Color.DEEPPINK);
								console.afficher("Pour " + nbIndividus + " individus dont " + nbSelection + " parents(" + PrPar + "%),", Color.DEEPPINK);
								console.afficher((dureeDeVie ? "avec": "sans") + " dur�e de vie,", Color.DEEPPINK);
								console.afficher("avec s�lection par " + (elitisme ? "�litisme,": "roulette,"), Color.DEEPPINK);
								console.afficher("avec une probabilit� de mutation de: " + PMut + ",", Color.DEEPPINK);
								console.afficher("une longueur de g�nome de: " + longueurGen + ",", Color.DEEPPINK);
								console.afficher("et sur une carte de " + nbCol + " par " + nbLig + ",", Color.DEEPPINK);
								console.afficher((generation+1) + " g�n�rations auront �t� n�c�ssaires.", Color.DEEPPINK);
								
								this.stop();
								calculs.interrupt();
								break;
								
							}
						}
					}
					if(!soluce){
					//animation de d�placement pour 1 gene de toute la population:
					Timeline animation = new Timeline();
						for(int j = 0 ; j < pop.size() ; j++){
							if(pop.get(j).isEnVie()){
								pop.get(j).increaseVie();
								animation.getKeyFrames().addAll(new KeyFrame(Duration.millis(50),
										new KeyValue(pop.get(j).layoutXProperty(), pop.get(j).getLayoutX() + pop.get(j).getGene(indiceGene).getdX()),
										new KeyValue(pop.get(j).layoutYProperty(), pop.get(j).getLayoutY() + pop.get(j).getGene(indiceGene).getdY())
									));
								pop.get(j).setRotate(pop.get(j).getGene(indiceGene).getdA());
							}
							
							}
					
					//syst�me de fausse animaion pour d�clencher l'�couteur m�me s'ils sont touts plant�s
					if(nbEnVie <= 0){
						animation.getKeyFrames().add(new KeyFrame(Duration.millis(1)));
					}
						
					animation.play();
					
					
					
					this.stop();
					
					
					
					animation.setOnFinished(e ->{
						if(indiceGene < longueurGen - 1 && nbEnVie > 0){
							indiceGene++;
							this.start();
						}
						else{
							synchronized(calculs){
								calculs.notify();
								generation++;
								numGen.setText("G�n�ration " + generation);
							}
						}
					});
					

					}
				}
			};
			
			calculs.start();
			
			
			
			
			
			}
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private boolean isDouble2(String nbGen) {
		try{
			double var = Double.parseDouble(nbGen);
			if(var < 1.0 || var > 30.0) return false;
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	private boolean isEntier2(String nb) {
		try{
			int var = Integer.parseInt(nb);
			if(var < 5 || var > 70) return false;
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	private boolean isChoix1(String nb) {
		try{
			int var = Integer.parseInt(nb);
			if(var < 0 || var > 1) return false;
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	private boolean isDouble(String nbMut) {
		try{
			double var = Double.parseDouble(nbMut);
			if(var < 0.01 || var > 0.5) return false;
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}

	public static void main(String[] args) {
		launch(args);
		
	}
	
	private static boolean isEntier(String nb){
		try{
			int var = Integer.parseInt(nb);
			if(var < 50 || var > 200) return false;
			return true;
		}catch(NumberFormatException e){
			return false;
		}
	}
	
	public static Group getGrIndiv(){
		return individus;
	}

	public static Population getPop() {
		return pop;
	}
	
	
}
