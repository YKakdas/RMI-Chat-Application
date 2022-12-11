package rmi.server;

import rmi.shared.RMIClientInterface;
import rmi.shared.RMIExceptionsUtil;
import rmi.shared.RMIServerInterface;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.stream.Collectors;

import static rmi.shared.RMIExceptionsUtil.*;
import static rmi.shared.RMIOperationsUtil.findPeerUsername;
import static rmi.shared.RMIOperationsUtil.findUserFromName;

public class RMIServer extends UnicastRemoteObject implements RMIServerInterface {
    private List<RMIClientInterface> connectedClients = new ArrayList<>();
    private HashMap<String, String> peers = new HashMap<>();

    public RMIServer() throws RemoteException, AlreadyBoundException {
        // Create a registry, bind server interface
        LocateRegistry.createRegistry(2222);
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2222);
        registry.bind("ServerInterface", this);
    }

    /**
     * A user wants to join the chat room. Check whether the username is valid or not. If valid, notify all other
     * user that there is a new user.
     *
     * @param username
     * @param clientInterface
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean joinToChatServer(String username, RMIClientInterface clientInterface) throws RemoteException {
        Optional<RMIClientInterface> optional = connectedClients
                .stream()
                .filter(client -> RMIExceptionsUtil.getUserNameCheckException(client).equalsIgnoreCase(username))
                .findAny();

        if (optional.isEmpty()) {
            ArrayList<RMIClientInterface> temp = new ArrayList<>(connectedClients);
            connectedClients.add(clientInterface);
            temp.forEach(client ->
                    RMIExceptionsUtil.notifyNewUserCheckException(client, RMIExceptionsUtil.getUserNameCheckException(clientInterface)));
        }
        return optional.isEmpty();
    }

    /**
     * A user wants to send a mesage to peer.
     *
     * @param peerUserName
     * @param message
     * @throws RemoteException
     */
    @Override
    public void sendMessageToPeer(String peerUserName, String message) throws RemoteException {
        String other = findPeerUsername(peerUserName, peers);
        if (other != null) {
            String appendedMessage = new Date() + " - " + other + ": " + message;
            RMIClientInterface firstUser = findUserFromName(peerUserName, connectedClients);
            RMIClientInterface secondUser = findUserFromName(other, connectedClients);
            if (firstUser != null && secondUser != null) {
                firstUser.getMessageFromPeer(appendedMessage);
                secondUser.getMessageFromPeer(appendedMessage);
            }
        }

    }

    /**
     * A user wants to disconnect from the chat room. Notify other users after disconnecting the user.
     *
     * @param name
     * @throws RemoteException
     */
    @Override
    public void disconnectFromChatServer(String name) throws RemoteException {
        connectedClients.removeIf(user -> RMIExceptionsUtil.getUserNameCheckException(user).equalsIgnoreCase(name));
        String peerName = findPeerUsername(name, peers);
        if (peerName != null) {
            RMIClientInterface client = findUserFromName(peerName, connectedClients);
            if (client != null) {
                setStatusCheckException(client, false);
            }
        }
        connectedClients.forEach(user -> RMIExceptionsUtil.notifyDisconnectionCheckException(user, name));
    }

    /**
     * A user wants to terminate the chat and return to the home page.
     *
     * @param username
     * @throws RemoteException
     */
    @Override
    public void returnToHomePage(String username) throws RemoteException {
        String peerUsername = findPeerUsername(username, peers);

        if (peerUsername != null) {
            connectedClients
                    .stream()
                    .filter(user -> getUserNameCheckException(user).equalsIgnoreCase(username) ||
                            getUserNameCheckException(user).equalsIgnoreCase(peerUsername))
                    .forEach(user -> setStatusCheckException(user, false));
            connectedClients.forEach(user -> notifyPeerReturnedHomePageCheckException(user, username, peerUsername));
        }

        if (peers.containsKey(username)) {
            peers.remove(username);
        } else {
            peers.remove(peerUsername);
        }
    }

    /**
     * A user wants to peer up with another user. Check if user exists and available. Let the peer know it.
     *
     * @param from
     * @param to
     * @return
     * @throws RemoteException
     */
    @Override
    public boolean peerUpWith(String from, String to) throws RemoteException {
        RMIClientInterface fromUser = findUserFromName(from, connectedClients);
        RMIClientInterface toUser = findUserFromName(to, connectedClients);

        if (toUser != null) {
            if (toUser.getIsBusy()) {
                return false;
            } else {
                peers.put(from, to);
                toUser.notifyPeeredUp(from);
                if (fromUser != null) {
                    fromUser.setIsBusy(true);
                }
                connectedClients
                        .stream()
                        .filter(user -> !getUserNameCheckException(user).equalsIgnoreCase(from)
                                && !getUserNameCheckException(user).equalsIgnoreCase(to))
                        .forEach(user -> notifyStatusChangedCheckException(user, from, to));
                return true;
            }

        }
        return false;
    }

    /**
     * Returns all available users in the chat room.
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<RMIClientInterface> getAllAvailableUsers() throws RemoteException {
        return connectedClients
                .stream()
                .filter(client -> !getStatusCheckException(client))
                .collect(Collectors.toList());
    }

    /**
     * Returns all users that are already in a chat with someone.
     *
     * @return
     * @throws RemoteException
     */
    @Override
    public List<RMIClientInterface> getAllBusyUsers() throws RemoteException {
        return connectedClients
                .stream()
                .filter(RMIExceptionsUtil::getStatusCheckException)
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws AlreadyBoundException, RemoteException {
        new RMIServer();
    }
}
