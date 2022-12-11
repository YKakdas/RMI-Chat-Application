package rmi.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * Defines the shared methods for server
 */
public interface RMIServerInterface extends Remote {
    /**
     * When a new client wants to join the chat room, registers by sending username and its shared instance
     *
     * @param username
     * @param clientInterface
     * @return
     * @throws RemoteException
     */
    boolean joinToChatServer(String username, RMIClientInterface clientInterface) throws RemoteException;

    /**
     * When a client wants to disconnect, sends that request to server
     *
     * @param username
     * @throws RemoteException
     */
    void disconnectFromChatServer(String username) throws RemoteException;

    /**
     * When a client wants to terminate its ongoing chat and return to homepage, will request it from the server.
     *
     * @param username
     * @throws RemoteException
     */
    void returnToHomePage(String username) throws RemoteException;

    /**
     * When a client wants to start a chat with another client, sends request by providing usernames
     *
     * @param from
     * @param to
     * @return
     * @throws RemoteException
     */
    boolean peerUpWith(String from, String to) throws RemoteException;

    /**
     * Returns list of all available users
     *
     * @return
     * @throws RemoteException
     */
    List<RMIClientInterface> getAllAvailableUsers() throws RemoteException;

    /**
     * Returns list of all busy users
     *
     * @return
     * @throws RemoteException
     */
    List<RMIClientInterface> getAllBusyUsers() throws RemoteException;

    /**
     * Delivers a message from one peer to another
     *
     * @param peerUserName
     * @param message
     * @throws RemoteException
     */
    void sendMessageToPeer(String peerUserName, String message) throws RemoteException;

}
