package Core.Network;

import Core.Player.Player;
import org.json.JSONObject;

public class PacketBuilder{

    public static JSONObject createPacket(NetworkProtocol protocol){
        JSONObject packet = new JSONObject();
        packet.put("actionType", protocol.name());
        return packet;
    }
    public static JSONObject createJoinPacket(String playerName) {
        JSONObject joinPacket = createPacket(NetworkProtocol.JOIN);
        joinPacket.put("playerName", playerName);
        return joinPacket;
    }
    public static JSONObject createPlayerDataPacket(String playerName,int coin,String color,String profileImagePath,String[] cityOwn){
        JSONObject packet = new JSONObject();
        packet.put("actionType", NetworkProtocol.UPDATE_PLAYER.name());
        packet.put("playerName", playerName);
        packet.put("coin", coin);
        packet.put("color", color);
        packet.put("profileImagePath", profileImagePath);
        packet.put("cityOwn", cityOwn);
        return packet;
    }
    public static JSONObject createPlayerDataPacket(Player player) {
        JSONObject packet = player.toJSON();
        packet.put("actionType", NetworkProtocol.UPDATE_PLAYER.name());
        return packet;
    }
    public static JSONObject createEndTurnPacket(){
        JSONObject packet = new JSONObject();
        packet.put("actionType", NetworkProtocol.END_TURN.name());
        return packet;
    }
}