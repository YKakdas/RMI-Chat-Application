package rmi.shared;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static rmi.shared.RMIExceptionsUtil.getUserNameCheckException;

public class RMIOperationsUtil {

    public static String findPeerUsername(String username, HashMap<String, String> peers) {
        String peer = null;
        if (peers.containsKey(username)) {
            peer = peers.get(username);
        } else {
            Optional<Map.Entry<String, String>> first =
                    peers.entrySet().stream().filter(entry -> entry.getValue().equalsIgnoreCase(username)).findFirst();
            if (first.isPresent()) {
                peer = first.get().getKey();
            }
        }

        return peer;
    }

    public static RMIClientInterface findUserFromName(String username, List<RMIClientInterface> connectedClients) {
        Optional<RMIClientInterface> optional = connectedClients
                .stream()
                .filter(user -> getUserNameCheckException(user).equalsIgnoreCase(username))
                .findAny();
        return optional.orElse(null);
    }

    public static void printProgramInstructions() {
        String description = """
                You will have four options to choose throughout the program.
                (1) Now, you are at the home page. You may see all users whether they are busy or available for chatting.
                You will also be notified for newcomers and people who leave the chat if you stay here.
                (2) You may start a chat with those who are not busy by prompting command of "$username". You will
                not be allowed to start a chat with busy ones. Please select a name from available users list.
                (3) Whether you are in the middle of a chat or staying in the home page, you may leave the chat room
                for good by prompting command of "$disconnect".
                (4) You may terminate chatting with your peer and return back to home page any time
                by prompting command of "$return"
                NOTE: Please do not use dollar sign($) for messaging since it is a reserve character for commands.
                """;
        System.out.println(description);
    }

}
