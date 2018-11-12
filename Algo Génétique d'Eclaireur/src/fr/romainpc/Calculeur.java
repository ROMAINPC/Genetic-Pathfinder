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

public class Calculeur extends Thread{
	
	private int nbIndividus;
	private int lonGen;
	private int nbParents;
	private double probaMutation;
	private boolean dureeDeVie;
	private boolean elitisme;
	
	public Calculeur(int n, int lG, int nbP, double pM, boolean dDV, boolean e){
		super();
		nbIndividus = n;
		lonGen = lG;
		nbParents = nbP;
		probaMutation = pM;
		dureeDeVie = dDV;
		elitisme = e;
	}
	
	public synchronized void run(){
		boolean partieTerminee = false;
		while(!partieTerminee){
			
			
			//lancement d'une phase d'animation:
			Main.affAnimation.play();
			Main.nbEnVie = nbIndividus;
			Main.indiceGene = 0;
			Main.boucle.start();
			
			try {
				this.wait();
				
			} catch (InterruptedException e) {
				System.out.println("Interruption, calculateur correctement fermé");
				partieTerminee = true;
				return;
			}
			
			
			
			//phase de CALCULS:
			
			Population pop = Main.getPop();
			
			//phase de SELECTION:
			//rang définit par sélection succesive des non dominés
			//sélection par roulette proportionnelle au rang
			//20 % des individus sont sélectionnés, en gardant un nombre pair
			double bestD = Math.abs(Math.sqrt( (Carte.getcA() - (pop.get(0).getLayoutX()-10)/20) * (Carte.getcA() - (pop.get(0).getLayoutX()-10)/20) + (Carte.getlA() - (pop.get(0).getLayoutY()-10)/20) * (Carte.getlA() - (pop.get(0).getLayoutY()-10)/20) ));
			int bestDDV = pop.get(0).getVie();
			ArrayList<IndividuCal> popCal = new ArrayList<IndividuCal>();
			for(int i = 0 ; i < pop.size() ; i++){
				double distance = Math.abs(Math.sqrt( (Carte.getcA() - (pop.get(i).getLayoutX()-10)/20) * (Carte.getcA() - (pop.get(i).getLayoutX()-10)/20) + (Carte.getlA() - (pop.get(i).getLayoutY()-10)/20) * (Carte.getlA() - (pop.get(i).getLayoutY()-10)/20) ));
				int dureeDeVie = pop.get(i).getVie();
				popCal.add(new IndividuCal(pop.get(i).getGenome(), pop.get(i).getLayoutX(), pop.get(i).getLayoutY(), dureeDeVie, distance));
				if(distance < bestD) bestD = distance;
				if(dureeDeVie < bestDDV) bestDDV = dureeDeVie;
			}
			
			Main.bestD = bestD;
			Main.bestDDV = bestDDV;
			Main.affBest.play();
			
			//attribution des dominances:
			for(int i = 0 ; i < popCal.size() ; i++){
				int nD = 0;
				for(int j = 0 ; j < popCal.size() ; j++){
					if(dureeDeVie){
						if(i != j &&
							(popCal.get(j).getDistance() < popCal.get(i).getDistance()) &&
							(popCal.get(j).getVie() < popCal.get(i).getVie()) )
								nD++;
					}else{
						if(i != j &&
						(popCal.get(j).getDistance() < popCal.get(i).getDistance()) )
							nD++;
						System.out.println(nD);
					}
				}
				popCal.get(i).setDom(nD);
				
			}
			
			//attribution des rangs:
			ArrayList<IndividuCal> popCalRang = new ArrayList<IndividuCal>();
			int rang = 1;
			int nbDom = 0;
			int RMax;
			while(!popCal.isEmpty()){
				boolean ya = false;
				for(int i = 0 ; i < popCal.size() ; i++){
					if(popCal.get(i).getDom() == nbDom){
						popCalRang.add(new IndividuCal(popCal.get(i).getGenome(), rang));
						ya = true;
						popCal.remove(i);
						i--;
					}
				}
				nbDom++;
				if(ya)
					rang++;
			}
			RMax = rang - 1;
			Main.affRang.play();
			
			IndividuCal[] listeParents = new IndividuCal[nbParents];
			Random r = new Random();
			
			
			
			if(!elitisme){
			
			//mise en place de la roulette:
			int totalRang = 0;
			for(int i = 0 ; i < popCalRang.size() ; i++)
				totalRang += (RMax - popCalRang.get(i).getRang() + 1);
			
			
			
			for(int i = 0 ; i < nbParents ; i++){
				//attribution des stats:
				//tableau cumulatif:
				double[] proba = new double[popCalRang.size()];
				double somme = (double)((RMax - popCalRang.get(0).getRang() + 1) / (double)totalRang);
				proba[0] =  somme;
				for(int j = 1 ; j < popCalRang.size() ; j++){
					somme += (double)((RMax - popCalRang.get(j).getRang() + 1) / (double)totalRang); 
					proba[j] = somme;
				}
				
				//tirage au sort:
				double tirage = r.nextDouble();
				int indice = 0;
				for(int j = 0 ; j < proba.length ; j++){
					if(proba[j] <= tirage){
						indice = j;
						
					}
				}
				
				
				totalRang -= (RMax - popCalRang.get(indice).getRang() +1);
				listeParents[i] = popCalRang.get(indice);
				popCalRang.remove(indice);
				
				
				
			}
			Main.affRoulette.play();
			
			
			}else{
				for(int i = 0 ; i < listeParents.length ; i++){
					listeParents[i] = popCalRang.get(i);
				}
				Main.affElit.play();
			}
			
			
			
			//phase de CROISEMENT:
			//pour chaque enfant à faire on tire aléatoireent 2 parents, l'enfant prendras un gène sur 2 de l'un puis de l'autre
			IndividuCal[] listeEnfants = new IndividuCal[(nbIndividus - nbParents)];
			for(int i = 0 ; i < listeEnfants.length ; i++){
				Direction[] gene1 =  listeParents[r.nextInt(listeParents.length)].getGenome();
				Direction[] gene2 =  listeParents[r.nextInt(listeParents.length)].getGenome();
				Direction[] newgene = new Direction[lonGen];
				for(int j = 0 ; j < lonGen ; j++){
					if(j%2 == 0)
						newgene[j] = gene1[j];
					else
						newgene[j] = gene2[j];
				}
				listeEnfants[i] = new IndividuCal(newgene);
			}
			Main.affCroisement.play();
			
			
			//phase de MUTATION:
			//chaque gène enfant à 5% de chances d'être muté
			for(int i = 0; i < listeEnfants.length ; i++){
				for(int j = 0 ; j < lonGen ; j++){
					if(r.nextDouble() <= probaMutation){
						int var = r.nextInt(8);
						Direction d = Direction.N;;
						switch (var){
							case 0: d = Direction.N; break;
							case 1: d = Direction.NE; break;
							case 2: d = Direction.E; break;
							case 3: d = Direction.SE; break;
							case 4: d = Direction.S; break;
							case 5: d = Direction.SW; break;
							case 6: d = Direction.W; break;
							case 7: d = Direction.NW; break;
						}
						listeEnfants[i].getGenome()[j] = d;
					}
				}
			}
			Main.affMut.play();
			
			//réinsertion dans la population, la vraie:
			pop.clear();
			for(int i = 0; i < listeEnfants.length ; i++){
				pop.add(new Individu(listeEnfants[i].getGenome(), Carte.getlD(), Carte.getcD()));
			}
			for(int i = 0; i < listeParents.length ; i++){
				pop.add(new Individu(listeParents[i].getGenome(), Carte.getlD(), Carte.getcD()));
			}
			Main.affPop.play();
			
			
		}
		
	}
}
