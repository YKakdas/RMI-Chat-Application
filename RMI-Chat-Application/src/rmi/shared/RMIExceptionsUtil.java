package rmi.shared;

import java.rmi.RemoteException;

/**
 * This util class is a wrapper for the RemoteException. Handles try-catch here to make the code readable
 */
public class RMIExceptionsUtil {

    /**
     * Tries to get the username of the client
     */
    public static String getUserNameCheckException(RMIClientInterface user) {
        try {
            return user.getUserName();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to get the status of the client
     */
    public static boolean getStatusCheckException(RMIClientInterface user) {
        try {
            return user.getIsBusy();
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to set the status of the client
     */
    public static void setStatusCheckException(RMIClientInterface user, boolean isBusy) {
        try {
            user.setIsBusy(isBusy);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to notify other users when a new user joins
     */
    public static void notifyNewUserCheckException(RMIClientInterface user, String username) {
        try {
            user.notifyNewUserJoined(username);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to notify other clients when two users peered up
     *
     * @param user
     * @param from
     * @param to
     */
    public static void notifyStatusChangedCheckException(RMIClientInterface user, String from, String to) {
        try {
            user.notifyStatusChanged(from, to);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to notify users when a client disconnects
     *
     * @param user
     * @param username
     */
    public static void notifyDisconnectionCheckException(RMIClientInterface user, String username) {
        try {
            user.notifyUserLeft(username);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to notify other users when a peer terminates the chat
     *
     * @param user
     * @param from
     * @param to
     */
    public static void notifyPeerReturnedHomePageCheckException(RMIClientInterface user, String from, String to) {
        try {
            user.peerReturnedHomePage(from, to);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }
}
