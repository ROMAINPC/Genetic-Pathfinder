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

public enum Direction {
	N(0, -20, -90),
	NE(20, -20, -45),
	E(20, 0, 0),
	SE(20, 20, 45),
	S(0, 20, 90),
	SW(-20, 20, 135),
	W(-20, 0, 180),
	NW(-20, -20, -135);
	
	
	private int dX;
	private int dY;
	private int dA;
	
	private Direction(int x, int y, int a){
		this.dX = x;
		this.dY = y;
		this.dA = a;
	}
	
	public int getdX() {
		return dX;
	}
	
	public int getdY() {
		return dY;
	}
	
	public int getdA(){
		return dA;
	}
}
