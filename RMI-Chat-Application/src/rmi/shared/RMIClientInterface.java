package rmi.shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Defines the shared methods for clients
 */
public interface RMIClientInterface extends Remote {

    /**
     * Gets username of a client
     *
     * @return username
     * @throws RemoteException
     */
    String getUserName() throws RemoteException;

    /**
     * Gets status of a client. True if the client is peered up with some other client and already in a conversation
     *
     * @return boolean
     * @throws RemoteException
     */
    boolean getIsBusy() throws RemoteException;

    /**
     * Changes status of a client. It is called when two clients start chatting or end it.
     *
     * @param isBusy
     * @throws RemoteException
     */
    void setIsBusy(boolean isBusy) throws RemoteException;

    /**
     * Notifies other clients(if they are not busy) when a new chatter joins to chat room.
     *
     * @param username
     * @throws RemoteException
     */
    void notifyNewUserJoined(String username) throws RemoteException;

    /**
     * Notifies other clients(if they are not busy) when a chatter leaves the chat room.
     *
     * @param username
     * @throws RemoteException
     */
    void notifyUserLeft(String username) throws RemoteException;

    /**
     * When a client wants to start private chat with other user, if that user is available, will be notified that
     * they peered up.
     *
     * @param username
     * @throws RemoteException
     */
    void notifyPeeredUp(String username) throws RemoteException;

    /**
     * Notifies other clients(if they are not busy) when two chatters peered up.
     *
     * @param from
     * @param to
     * @throws RemoteException
     */
    void notifyStatusChanged(String from, String to) throws RemoteException;

    /**
     * Notifies other clients(if they are not busy) when two chatters finish the conversation and they got back to
     * chat room.
     *
     * @param from
     * @param to
     * @throws RemoteException
     */
    void peerReturnedHomePage(String from, String to) throws RemoteException;

    /**
     * When a peer wants to send message, finds its peer and deliver the message to there
     *
     * @param message
     * @throws RemoteException
     */
    void getMessageFromPeer(String message) throws RemoteException;
}
