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


import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Translate;

public class Individu extends Polygon{
	
	private Direction[] genome;
	private Translate translate = new Translate();
	private boolean enVie;
	private int tempsDeVie;
	
	public Individu(Direction[] gen, int lD, int cD){
		
		this.getTransforms().add(translate);
		
		//génôme:
		genome = gen;
		
		
		//forme
		this.getPoints().addAll(new Double[]{
		      8.0, 0.0,
		      -8.0, 6.0,
		      -6.0, 0.0,
		      -8.0, -6.0
		});
		
		//couleur:
		Color couleur = Color.color(Math.random(), Math.random(), Math.random());
		this.setFill(couleur);
		
		
		//position initiale:
//		translate.setX(lD * 20 + 10);
//		translate.setY(cD * 20 + 10);
		this.setLayoutX(cD * 20 + 10);
		this.setLayoutY(lD * 20 + 10);
		
		
		enVie = true;
		tempsDeVie = 0;
		
		
		
		
	}
	
	public Direction getGene(int i){
		return genome[i];
	}
	public Direction[] getGenome(){
		return genome;
	}

	public void planter() {
		Main.nbEnVie--;
		enVie = false;
	}
	
	public boolean isEnVie(){
		return enVie;
	}
	
	public void reanimer(){
		enVie = true;
	}

	public void increaseVie() {
		tempsDeVie++;
		
	}

	public int getVie() {
		return tempsDeVie;
	}
}
