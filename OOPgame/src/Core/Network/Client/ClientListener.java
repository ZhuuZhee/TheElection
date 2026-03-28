package Core.Network.Client;

public interface ClientListener {
    void onJoinAcknowledge();
    void onSyncGameState();
    void onStartGame();
    void onHostLeft();
    void onUpdatePlayer();
    void onEndTurn();
    void onStartTurn();
}
