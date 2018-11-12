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

public class IndividuCal{
	
	private double X;
	private double Y;
	private int vie;
	private Direction[] genes;
	private int rang;
	private int dominants;
	private double distance;
	
	public IndividuCal(Direction[] gen, double x, double y, int v, double d){
		X = x;
		Y = y;
		genes = gen;
		vie = v;
		distance = d;
	}
	
	public IndividuCal(Direction[] gen, int r){
		genes = gen;
		rang = r;
	}
	public IndividuCal(Direction[] gen){
		genes = gen;
	}
	

	public void setX(int i) {
		X = i;
	}
	public double getX() {
		return X;
	}
	
	public void setY(int i) {
		Y = i;
	}
	public double getY() {
		return Y;
	}
	
	public int getVie(){
		return vie;
	}
	
	public int getRang(){
		return rang;
	}
	
	
	public int getDom(){
		return dominants;
	}
	
	public void setDom(int d){
		dominants = d;
	}
	
	
	public double getDistance(){
		return distance;
	}

	public Direction[] getGenome() {
		return genes;
	}
	
}
