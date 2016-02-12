// // Copyright 2016 theaigames.com (developers@theaigames.com)

//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at

//        http://www.apache.org/licenses/LICENSE-2.0

//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//  
//    For the full copyright and license information, please view the LICENSE
//    file that was distributed with this source code.

package bot;

import bot.mcst.IAction;
import bot.memory.IReusable;

/**
 * Move class
 * 
 * Stores a move.
 * 
 * @author Jim van Eeden <jim@starapple.nl>, Joost de Meij <joost@starapple.nl>
 */

public class Move implements IAction, IReusable{
	private int x, y;
	private Player player;
	
	
	
	public Move() {
	}
	
	public Move(int x, int y, Player player) {
		this.setX(x);
		this.setY(y);
		this.player = player;
	}
	
	public void initalize(int x, int y, Player player){
		reset();
		this.x = x;
		this.y = y;
		this.player = player;
		
	}
	
	public void reset(){
		x = 0;
		y = 0;
		player = null;
	}
	
	private void setX(int x){
		if (x < 0 || x > 8) throw new IllegalArgumentException("x not in allowed range: 0 - 8");
		this.x = x;
	}
	public int getX() { return x; }
	
	private void setY(int y){
		if (y < 0 || y > 8) throw new IllegalArgumentException("y not in allowed range: 0 - 8");
		this.y = y;
	}
	public int getY() { return y; }
	
	public Player getPlayer() { return player;}

	
}
