package bot;

public class Player {
	
	public enum PlayerTypes{
		Self,
		Opponent,
		None;

	}
	
	private static Player[] _players = null;
	
	public static void initalizePlayers(int ownId, int opponentId, int neutralId){
		_players = new Player[3];
		_players[0] = new Player(ownId, PlayerTypes.Self);
		_players[1] = new Player(opponentId, PlayerTypes.Opponent);
		_players[2] = new Player(neutralId, PlayerTypes.None);
	}
	
	public static Player getPlayer(PlayerTypes playerType){
		for (int i = 0; i < _players.length; i++){
			Player player = _players[i];
			if (player.getPlayerType() == playerType){
				return player;
			}
		}
		return null;
	}
	
	public static Player getPlayer(int id){
		for (int i = 0; i < _players.length; i++){
			Player player = _players[i];
			if (player.getId() == id){
				return player;
			}
		}
		return null;
	}
	
	public static Player getInvertedPlayer(Player player){
		if (player == null || player.getPlayerType() == PlayerTypes.None){
			throw new IllegalArgumentException("Illegal player");
		}
		PlayerTypes otherPlayerType = (player.getPlayerType() == PlayerTypes.Self) ? PlayerTypes.Opponent : PlayerTypes.Self;
		return getPlayer(otherPlayerType);
	}
	
	private int id;
	private PlayerTypes playerType;

	public int getId() {
		return id;
	}

	private void setId(int id) {
		this.id = id;
	}

	public PlayerTypes getPlayerType() {
		return playerType;
	}

	private void setPlayerType(PlayerTypes playerType) {
		this.playerType = playerType;
	}
	
	private Player(int id,  PlayerTypes playerType){
		setId(id);
		setPlayerType(playerType);
	}
	
	@Override
	public boolean equals(Object o){

        // If the object is compared with itself then return true  
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Player or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Player)) {
            return false;
        }
        
        // typecast o to Player so that we can compare data members 
        Player player = (Player) o;
		
        if (this.id == player.id &&
        		this.playerType == player.playerType){
        	return true;
        }
        return false;
	}
	
}
