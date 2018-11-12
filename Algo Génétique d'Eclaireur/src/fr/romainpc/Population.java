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

import java.util.ArrayList;
import java.util.Random;

public class Population extends ArrayList<Individu>{
	
	
	
	//constructeur initial:
	public Population(int N, int lonGen, int lD, int cD){
		
		
		
		for(int a = 0 ; a < N ; a++){
			
			//première population donc envoit d'un génome aléatoire:
			Direction[] genome = new Direction[lonGen];
			Random r = new Random();
			for(int i = 0 ; i < lonGen ; i++){
				int var = r.nextInt(8);
				switch (var){
					case 0: genome[i] = Direction.N; break;
					case 1: genome[i] = Direction.NE; break;
					case 2: genome[i] = Direction.E; break;
					case 3: genome[i] = Direction.SE; break;
					case 4: genome[i] = Direction.S; break;
					case 5: genome[i] = Direction.SW; break;
					case 6: genome[i] = Direction.W; break;
					case 7: genome[i] = Direction.NW; break;
				}
				
			}
			
			this.add(new Individu(genome, lD, cD));
		}
		
		
		
	}

}
