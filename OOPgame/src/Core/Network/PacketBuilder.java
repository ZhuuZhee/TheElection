package Core.Network;

import Core.Player.Player;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

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
    public static JSONObject createPlayerDataPacket(String playerId,String playerName,int coin,String color,String profileImagePath, String arcanaCardName){
        JSONObject packet = createPacket(NetworkProtocol.UPDATE_PLAYER);
        packet.put("playerId", playerId);
        packet.put("playerName", playerName);
        packet.put("coin", coin);
        packet.put("color", color);
        packet.put("profileImagePath", profileImagePath);
        packet.put("arcanaCard", arcanaCardName);
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

    public static JSONObject createStartPacket(){
        JSONObject startReq = createPacket(NetworkProtocol.START_GAME);
        return startReq;
    }

    public static JSONObject createUseCardPacket(JSONObject cityJson) {
        JSONObject packet = createPacket(NetworkProtocol.USE_CARD);
        packet.put("city", cityJson);
        return packet;
    }

    public static JSONObject createDestroyHandPacket(String fromPlayerId) {
        JSONObject packet = createPacket(NetworkProtocol.DESTROY_AND_SKIP_DRAW);
        packet.put("fromPlayerId", fromPlayerId);
        return packet;
    }

    public static JSONObject createNegativeHandStatsPacket(String fromPlayerId) {
        JSONObject packet = createPacket(NetworkProtocol.NEGATIVE_HAND_STATS);
        packet.put("fromPlayerId", fromPlayerId);
        return packet;
    }

    public static JSONObject createJudgementSkillPacket(String fromPlayerId) {
        JSONObject packet = createPacket(NetworkProtocol.JUDGEMENT_SKILL);
        packet.put("fromPlayerId", fromPlayerId);
        return packet;
    }

    public static JSONObject createKickPacket(){
        return createPacket(NetworkProtocol.HOST_LEFT);
    }
    public static JSONObject createVotingPacket(HashMap<String,Float> playerScores)
    {
        JSONObject packet = createPacket(NetworkProtocol.VOTING);
        JSONArray playerScoreJSON = new JSONArray();
        for(String pId : playerScores.keySet()) {
            float pScore = playerScores.getOrDefault(pId,0f);
            JSONObject pScoreJSON = new JSONObject();
            pScoreJSON.put(pId,pScore);
            playerScoreJSON.put(pScoreJSON);
        }
        packet.put("players score", playerScoreJSON);
        return packet;
    }
}