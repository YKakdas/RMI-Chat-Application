package rmi.client;

import rmi.shared.RMIClientInterface;
import rmi.shared.RMIExceptionsUtil;
import rmi.shared.RMIServerInterface;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Scanner;

import static rmi.shared.RMIOperationsUtil.printProgramInstructions;

public class RMIClient extends UnicastRemoteObject implements RMIClientInterface {

    private String username;
    private boolean isBusy = false;

    private RMIServerInterface serverInterface;

    private String peerName = null;

    public RMIClient() throws RemoteException, NotBoundException {
        // Look through registry to find shared server interface
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", 2222);
        serverInterface = (RMIServerInterface) registry.lookup("ServerInterface");

        promptForUserName();
        printProgramInstructions();

        while (true) {
            listAllUsers();
            promptForStartingCommunication();
        }

    }

    @Override
    public String getUserName() throws RemoteException {
        return username;
    }

    @Override
    public boolean getIsBusy() throws RemoteException {
        return isBusy;
    }

    /**
     * Changes the status of the user. Either available or busy
     *
     * @param isBusy
     * @throws RemoteException
     */
    @Override
    public void setIsBusy(boolean isBusy) throws RemoteException {
        this.isBusy = isBusy;
    }

    /**
     * Server calls this one when user's peer sends a messagee
     *
     * @param message
     * @throws RemoteException
     */
    @Override
    public void getMessageFromPeer(String message) throws RemoteException {
        if (isBusy) {
            System.out.println(message);
        }
    }

    /**
     * If the user is not in a chat already, notify to let him/her a new user joined to the chat room. List the updated
     * users list
     *
     * @param username
     * @throws RemoteException
     */
    @Override
    public void notifyNewUserJoined(String username) throws RemoteException {
        if (!isBusy) {
            System.out.println("---------------------------------------------------->");
            System.out.println("New person joined our chat room. Welcome " + username + " !");
            System.out.println("Updated list of chat room:");
            listAllUsers();
        }
    }

    /**
     * If the user is not in a chat already, notify to let him/her a user left the chat room. List the updated
     * users list
     *
     * @param username
     * @throws RemoteException
     */
    @Override
    public void notifyUserLeft(String username) throws RemoteException {
        if (!isBusy) {
            System.out.println("---------------------------------------------------->");
            System.out.println("A person left our chat room. Farewell " + username + " :(");
            System.out.println("Updated list of chat room:");
            listAllUsers();
        } else {
            if (username.equalsIgnoreCase(peerName)) {
                peerName = null;
                System.out.println("Your peer has been disconnected. Returning home page...");
                listAllUsers();
            }
        }
    }

    /**
     * A user wants to peer with this user.
     *
     * @param username
     * @throws RemoteException
     */
    @Override
    public void notifyPeeredUp(String username) throws RemoteException {
        isBusy = true;
        peerName = username;
        System.out.println("--------------------------------------------------->");
        System.out.println("You have successfully peered up with user " + username);
        System.out.println("You may start chatting");
    }

    /**
     * Let this user know when a status of any other users has been changed.
     *
     * @param from
     * @param to
     * @throws RemoteException
     */
    @Override
    public void notifyStatusChanged(String from, String to) throws RemoteException {
        if (!isBusy) {
            System.out.println(from + " and " + to + " has been peered up. Updated list of chat room:");
            listAllUsers();
        }
    }

    /**
     * The peer terminated the communication. Notify this user and return him/her back to the lobby.
     *
     * @param from
     * @param to
     * @throws RemoteException
     */
    @Override
    public void peerReturnedHomePage(String from, String to) throws RemoteException {
        if (from.equalsIgnoreCase(username)) {
            listAllUsers();
            peerName = null;
        } else if (to.equalsIgnoreCase(username)) {
            System.out.println("Your peer has been terminated the communication. Returning to home page.");
            peerName = null;
            listAllUsers();
        } else {
            if (!isBusy) {
                System.out.println("Peer " + from + " and " + to + " has ended their conversation.");
                System.out.println("Updated list of chat room:");
                listAllUsers();
            }
        }
    }

    /**
     * Ask user to enter a username. It should be unique. Verify user's request by calling server
     *
     * @throws RemoteException
     */
    private void promptForUserName() throws RemoteException {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please enter your username.");
        boolean joinStatus = false;
        do {
            String username = sc.nextLine();
            this.username = username;
            joinStatus = serverInterface.joinToChatServer(username, this);
            if (!joinStatus) {
                System.out.println("The username has been taken already. Please choose another name.");
            } else {
                System.out.println("You have successfully joined to the chat room!");
            }
        } while (!joinStatus);

    }

    /**
     * Client logic from console. Decide what to do by parsing user's prompt
     *
     * @throws RemoteException
     */
    private void promptForStartingCommunication() throws RemoteException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            String text = sc.nextLine();
            System.out.println("---------------------------------------------------->");
            if (text.equalsIgnoreCase("$disconnect")) {
                serverInterface.disconnectFromChatServer(username);
                System.exit(0);
            } else if (text.equalsIgnoreCase("$return")) {
                if (!isBusy) {
                    System.out.println("You are already at home page, please read the possible options again.");
                } else {
                    isBusy = false;
                    serverInterface.returnToHomePage(username);
                }
                continue;
            } else if (text.startsWith("$")) {
                String tempUserName = text.substring(1);
                if (username.equalsIgnoreCase(tempUserName)) {
                    System.out.println("You cannot chat with yourself. Please choose a user from available users list");
                    continue;
                }
                boolean result = serverInterface.peerUpWith(username, tempUserName);
                if (result) {
                    System.out.println("--------------------------------------------------->");
                    System.out.println("You have successfully peered up with user " + tempUserName);
                    System.out.println("You may start chatting");
                    peerName = tempUserName;
                    continue;
                } else {
                    System.out.println("Peering was unsuccessful. Please make sure such a user exist and available");
                }
            } else {
                if (isBusy) {
                    serverInterface.sendMessageToPeer(peerName, text);
                }
            }
        }
    }

    /**
     * Lists available and busy users in the chat room.
     *
     * @throws RemoteException
     */
    private void listAllUsers() throws RemoteException {
        List<RMIClientInterface> availableUsers = serverInterface.getAllAvailableUsers();
        List<RMIClientInterface> busyUsers = serverInterface.getAllBusyUsers();

        availableUsers.removeIf(user -> RMIExceptionsUtil.getUserNameCheckException(user).equalsIgnoreCase(username));
        busyUsers.removeIf(user -> RMIExceptionsUtil.getUserNameCheckException(user).equalsIgnoreCase(username));

        if (availableUsers.size() > 0) {
            System.out.println("Available Users in the chat room:");
            availableUsers.stream().map(RMIExceptionsUtil::getUserNameCheckException).forEach(System.out::println);
        }

        if (busyUsers.size() > 0) {
            System.out.println("---------------------------------------------------->");
            System.out.println("Busy users at this moment:");
            busyUsers.stream().map(RMIExceptionsUtil::getUserNameCheckException).forEach(System.out::println);
        }

        if (availableUsers.size() == 0 && busyUsers.size() == 0) {
            System.out.println("You are alone alone my friend :(");
        }

    }

    public static void main(String[] args) throws NotBoundException, RemoteException {
        new RMIClient();
    }
}
