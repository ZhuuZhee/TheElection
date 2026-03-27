package Core.Network;

import Core.Player.Player;
import org.json.JSONObject;

public class PacketBuilder{

    public static JSONObject createPacket(NetworkProtocol protocol){
        JSONObject packet = new JSONObject();
        packet.put("type", protocol.name());
        return packet;
    }
    public static JSONObject createJoinPacket(String playerName) {
        JSONObject joinPacket = createPacket(NetworkProtocol.JOIN);
        joinPacket.put("playerName", playerName);
        return joinPacket;
    }
    ///String playerName,int coin,String color,String profileImagePath,String[] cityOwn
    public static JSONObject createPlayerDataPacket(String playerId,String playerName,int coin,String color,String profileImagePath){
        JSONObject packet = createPacket(NetworkProtocol.UPDATE_PLAYER);
        packet.put("playerId", playerId);
        packet.put("playerName", playerName);
        packet.put("coin", coin);
        packet.put("color", color);
        packet.put("profileImagePath", profileImagePath);
        return packet;
    }

    public static JSONObject createEndTurnPacket(){
        JSONObject packet = createPacket(NetworkProtocol.END_TURN);;
        return packet;
    }

    public static JSONObject createPongPacket(){
        JSONObject packet = createPacket(NetworkProtocol.PONG);;
        return packet;
    }
    public static JSONObject createPingPacket(){
        JSONObject packet = createPacket(NetworkProtocol.PING);;
        return packet;
    }

    public static JSONObject createStartPacket(){
        JSONObject startReq = createPacket(NetworkProtocol.START_GAME);
        return startReq;
    }
}