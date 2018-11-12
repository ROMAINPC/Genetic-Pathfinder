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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;

public class Carte {
	private static int[][] carte;
	private static int nbLig;
	private static int nbCol;
	
	private static int lD, cD, lA, cA;
	
	public static int[][] recupCarte(String nomMap) {
		//chargement de la carte d'expérience
		//fichier .txt -> sous forme d'un tableau de nombres (sans espaces)
		//0=vide
		//1=mur
		//2=départ
		//3=arrivée
		//la première ligne du txt indique (séparé par un espace) le nb de lignes puis de colonnes
		//attention ne mettre qu'un seul départ et une seule arrivée ! !  !
		
		
		File file;
		Scanner scanner;
		try {
			file = new File(nomMap + ".txt");
			scanner = new Scanner(file);
			
			nbLig = scanner.nextInt();
			nbCol = scanner.nextInt();
			carte = new int[nbLig][nbCol];
			
			scanner.nextLine();
			
			for(int l = 0 ; l < nbLig ; l++){
				String lig = scanner.nextLine();
				for(int c = 0 ; c < nbCol ; c++){
					carte[l][c] = lig.charAt(c) - 48;
					if(lig.charAt(c) == '2'){
						lD = l; cD = c;
					}
					if(lig.charAt(c) == '3'){
						lA = l; cA = c;
					}
				}
			}
			
			
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Alert a = new Alert(Alert.AlertType.ERROR);
			a.setHeaderText("Fichier pas trouvé :(");
			a.show();
			Main.toutBienPasse =  false;
			a.setOnHidden(event -> Platform.exit());
		}
			
		
		return carte;
	}
	
	
	public static int getlD(){
		return lD;
	}
	public static int getcD(){
		return cD;
	}
	public static int getlA(){
		return lA;
	}
	public static int getcA(){
		return cA;
	}
	public static int getnbLig(){
		return nbLig;
	}
	public static int getnbCol(){
		return nbCol;
	}


	public static Group afficher() {
		
		Group group = new Group();
		
		//attention ailleurs faudra ajouter +10 en x et y pour savoir où est le centre ! ! ! 
		//			/!\
		for(int l = 0 ; l < nbLig ; l++){
			for(int c = 0 ; c < nbCol ; c++){
				switch(carte[l][c]){
					case 1:
						Rectangle r = new Rectangle(c*20,l*20,20,20);
						r.setFill(Color.rgb(120, 120, 120));
						r.setArcHeight(10); r.setArcWidth(10);
						group.getChildren().add(r);
						r.setEffect(new InnerShadow(2, 1, 1, Color.DARKBLUE));
					break;
					
					case 2:
						Rectangle r2 = new Rectangle(c*20,l*20,20,20);
						r2.setFill(Color.rgb(0, 255, 0));
						r2.setArcHeight(20); r2.setArcWidth(20);
						r2.setStroke(Color.ROYALBLUE);
						r2.setStrokeType(StrokeType.INSIDE);
						r2.setStrokeWidth(4);
						group.getChildren().add(r2);
					break;
					
					case 3:
						Rectangle r3 = new Rectangle(c*20,l*20,20,20);
						r3.setFill(Color.RED);
						r3.setArcHeight(20); r3.setArcWidth(20);
						r3.setStroke(Color.GREEN);
						r3.setStrokeType(StrokeType.INSIDE);
						r3.setStrokeWidth(4);
						group.getChildren().add(r3);
					break;
				}
			}
		}
		

		
		return group;
	}

}
