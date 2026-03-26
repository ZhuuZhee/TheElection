package Core.Network;

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
    public static JSONObject createEndTurnPacket(){
        JSONObject packet = new JSONObject();
        packet.put("actionType", NetworkProtocol.END_TURN.name());
        return packet;
    }
}