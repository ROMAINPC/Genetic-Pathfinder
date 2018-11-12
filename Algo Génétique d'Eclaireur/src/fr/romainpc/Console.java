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

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Console extends ScrollPane{
	
	private VBox group;
	
	public Console(){
		
		group = new VBox();
		group.setPadding(new Insets(10,10,10,10));
		this.setContent(group);
		
		//messages de d�but:
		this.afficher("Programme par ROMAINPC LECHAT", Color.ORANGE);
		
		
		this.afficher(">Infos sur les calculs:<", Color.rgb(0, 255, 0));
		
		this.afficher("=> S�lection:", Color.rgb(0, 255, 0));
		this.afficher("Crit�res: distance � l'arriv�e (et temps de vie).", Color.rgb(0, 255, 0));
		this.afficher("Attribution en rangs par s�lection successive des non-domin�s.", Color.rgb(0, 255, 0));
		this.afficher("S�lection par roulette proportionnelle au rang OU par �litisme", Color.rgb(0, 255, 0));
		
		this.afficher("=> Croisements:", Color.rgb(0, 255, 0));
		this.afficher("Pour chaque enfant, 2 parents sont tir�s au sort", Color.rgb(0, 255, 0));
		this.afficher("L'enfant prends 1 g�ne sur 2 des parents", Color.rgb(0, 255, 0));
		
		this.afficher("=> Mutations:", Color.rgb(0, 255, 0));
		this.afficher("Chaque g�ne d'enfant � une probabilit� de mutation", Color.rgb(0, 255, 0));
		
		
		
		this.afficher("<--------D�BUT DE L'EXP�RIENCE-------->", Color.RED);
		
		
		
		
		
	}

	public void afficher(String txt, Color paint) {
		Label label = new Label(txt);
		label.setFont(Font.font("Comic Sans MS", 12));
		label.setTextFill(paint);
		
		this.setVvalue(this.getVvalue()+42);
		
		group.getChildren().add(label);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
