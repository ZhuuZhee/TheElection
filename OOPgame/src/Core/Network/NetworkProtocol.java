package Core.Network;

public enum NetworkProtocol {
    JOIN,
    JOIN_ACK,
    SYNC_STATE,
    UPDATE_PLAYER,
    START_GAME,
    HOST_LEFT,
    PING,
    PONG,
    END_TURN,
    USE_CARD,
    DESTROY_AND_SKIP_DRAW,
    NEGATIVE_HAND_STATS,
    VOTING,
    JUDGEMENT_SKILL,
    NOTIFICATION
}
