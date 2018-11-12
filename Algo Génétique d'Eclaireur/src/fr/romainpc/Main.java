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
	
	//boolean en cas de fichier non trouvé
	public static boolean toutBienPasse = true;
	public static boolean soluce = false;
	

	
	@Override
	public void start(Stage primaryStage) {
		try {
			
			
			//génération fenêtre:
			
			
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
			//autres éléments:
			Label numGen = new Label("Génération 1");
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
			
			
			
			
			
			//choix du nb d'individus par génération:
			String nb = "";
			Optional<String> r = null;
			while(!isEntier(nb)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Veuillez sélectionner le nombre d'individus par génération:\n"
				+ "N < 50 et N > 200 interdit");
			selN.setTitle("Sélection du nombre d'individus");
			r = selN.showAndWait();
			if(r.isPresent())nb = r.get();
			}
			nbIndividus = Integer.valueOf(r.get());
			console.afficher(nbIndividus + " Individus par génération", Color.rgb(0, 255, 0));
			nbVivant.setText("Encore en vie: " + nbIndividus);
			
			//choix du classement par dominance avec ou sans prise en compte du temps de vie:
			String nbChoix1 = "";
			Optional<String> rVie = null;
			while(!isChoix1(nbChoix1)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Veuillez indiquer si OUI (entrez 1) ou NON (entrez 0) vous prenez en compte\n" + "la durée de vie en plus de la distance à l'arrivée pour le classement:\n"+"(faible durée de vie => meilleur au classement)");
			selN.setTitle("Sélection du système de dominance");
			rVie = selN.showAndWait();
			if(rVie.isPresent())nbChoix1 = rVie.get();
			}
			boolean dureeDeVie;
			if(Integer.valueOf(rVie.get()) == 0){
				dureeDeVie = false;
				console.afficher("Durée de vie: NON", Color.rgb(0, 255, 0));
			}else{
				dureeDeVie = true;
				console.afficher("Durée de vie: OUI", Color.rgb(0, 255, 0));
			}
			
			//choix du mode de sélection:
			String nbChoix2 = "";
			Optional<String> rSel = null;
			while(!isChoix1(nbChoix2)){
			TextInputDialog selN = new TextInputDialog();
			selN.initOwner(primaryStage);
			selN.setHeaderText(
				"Pour une sélection par roulette proportionnelle entrez 0\n" + "Pour une sélection par élitisme entrez 1");
			selN.setTitle("Sélection du système de sélection");
			rSel = selN.showAndWait();
			if(rSel.isPresent())nbChoix2 = rSel.get();
			}
			boolean elitisme;
			if(Integer.valueOf(rSel.get()) == 0){
				elitisme = false;
				console.afficher("Sélection par roulette", Color.rgb(0, 255, 0));
			}else{
				elitisme = true;
				console.afficher("Sélection par élitisme", Color.rgb(0, 255, 0));
			}
			
			//choix du nombre de parents:
			String nbPar = "";
			Optional<String> rPar = null;
			while(!isEntier2(nbPar)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez sélectionner le pourcentage de parents:\n"
				+ "Valeurs conseillées: 10 - 20 %			P < 5 et P > 70 interdit");
			selM.setTitle("Sélection du nombre de parents");
			rPar = selM.showAndWait();
			if(rPar.isPresent())nbPar = rPar.get();
			}
			int PrPar = Integer.valueOf(nbPar);
			
			
			//nombre à sélectionner:
			nbSelection = (int)((double)nbIndividus / 100 * PrPar);
			console.afficher("Nombre d'individus parents: " + nbSelection, Color.rgb(0, 255, 0));
			
			
			//choix de la probabilité de mutation
			String nbMut = "";
			Optional<String> r3 = null;
			while(!isDouble(nbMut)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez sélectionner la probabilité de mutation des gènes:\n"
				+ "Valeurs conseillées: 0.05 - 0.1			P < 0.01 et P > 0.5 interdit");
			selM.setTitle("Sélection de la probabilité de mutation");
			r3 = selM.showAndWait();
			if(r3.isPresent())nbMut = r3.get();
			}
			double PMut = Double.valueOf(nbMut);
			console.afficher("Probabilté de mutation: " + PMut, Color.rgb(0, 255, 0));
			
			
			
			
			
			//choix du fichier carte:
			String nomMap = "";
			while(nomMap.equals("")){
			TextInputDialog boite = new TextInputDialog();
			boite.initOwner(primaryStage);
			boite.setTitle("Sélection de la carte");
			boite.setHeaderText("Entrez le nom de la carte à expérimenter:");
			boite.setContentText("Placez ce fichier .txt valable à côté de l'exécutable de ce programme.");
			Optional<String> r2 = boite.showAndWait(); if(r2.isPresent())nomMap = r2.get();}
			
			
			
			
			//chargement de la carte:
			int[][] carte = Carte.recupCarte(nomMap);
			int lD = Carte.getlD();
			int cD = Carte.getcD();
			int lA = Carte.getlA();
			int cA = Carte.getcA();
			int nbLig = Carte.getnbLig();
			int nbCol = Carte.getnbCol();
			console.afficher("Carte "+nomMap+" chargée.", Color.rgb(0, 255, 0));
			
			//affichage carte:
			HUDmap.getChildren().add(Carte.afficher());
			
			//longueur du génome:
			String nbGen = "";
			Optional<String> rG = null;
			while(!isDouble2(nbGen)){
			TextInputDialog selM = new TextInputDialog();
			selM.initOwner(primaryStage);
			selM.setHeaderText(
				"Veuillez sélectionner le facteur F:\n"
				+ "la longueur des génomes est définie par (LargeurCarte + LongueurCarte) x F\n"
				+ "Valeurs conseillées: 1.5			F < 1 et F > 30 interdit");
			selM.setTitle("Sélection de la longueur du génome");
			rG = selM.showAndWait();
			if(rG.isPresent())nbGen = rG.get();
			}
			double facteur = Double.valueOf(nbGen);
			int longueurGen = (int) ((double)(nbLig + nbCol) * facteur);
			console.afficher("Longueur du génome définie sur : " + longueurGen, Color.rgb(0, 255, 0));
			
			
			
			//création de la population initiale:
			
			HUDmap.getChildren().add(individus);
			pop = new Population(nbIndividus, longueurGen, lD, cD);
			console.afficher("Population initiale créée.", Color.rgb(255, 255, 0));
			
			
			//BOUCLE d'animations et de réitération du processus:
			
			if(toutBienPasse){
			
				//Timers pour afficher dans la console:
				affAnimation = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Simulation de la génération " + generation, Color.DEEPSKYBLUE);
					console.afficher(". . .", Color.DEEPSKYBLUE);
				}));
				affBest = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Meilleure distance: " + bestD, Color.DEEPPINK);
					if(dureeDeVie)
						console.afficher("Meilleure durée de vie(=plus courte): " + bestDDV, Color.DEEPPINK);
				}));
				affRang = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher("Classement par nombre de dominants", Color.YELLOW);
				}));
				affRoulette = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher(nbSelection+" parents sélectionnés par Roulette", Color.YELLOW);
				}));
				affElit = new Timeline(new KeyFrame(Duration.millis(10), e -> {
					console.afficher(nbSelection+" parents sélectionnés par Elitisme", Color.YELLOW);
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
			
			//sécurité:
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
						
							//détection arrivée:
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
								console.afficher((dureeDeVie ? "avec": "sans") + " durée de vie,", Color.DEEPPINK);
								console.afficher("avec sélection par " + (elitisme ? "élitisme,": "roulette,"), Color.DEEPPINK);
								console.afficher("avec une probabilité de mutation de: " + PMut + ",", Color.DEEPPINK);
								console.afficher("une longueur de génome de: " + longueurGen + ",", Color.DEEPPINK);
								console.afficher("et sur une carte de " + nbCol + " par " + nbLig + ",", Color.DEEPPINK);
								console.afficher((generation+1) + " générations auront été nécéssaires.", Color.DEEPPINK);
								
								this.stop();
								calculs.interrupt();
								break;
								
							}
						}
					}
					if(!soluce){
					//animation de déplacement pour 1 gene de toute la population:
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
					
					//système de fausse animaion pour déclencher l'écouteur même s'ils sont touts plantés
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
								numGen.setText("Génération " + generation);
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
