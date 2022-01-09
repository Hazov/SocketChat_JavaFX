package src.server;

import src.server.auth.AuthService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Vector;

public class Server {

    private Vector<ClientHandler> clients = new Vector<>();
    private Vector<Integer> ids = new Vector<>();

    private int PORT = 7777;
    private ServerSocket server;
    private Socket socket;
    private ClientHandler clientHandler;

    File file;
    private RandomAccessFile toHTML;

    public Server(){
        try {
            file = new File("HTML_LOG.html");
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            toHTML = new RandomAccessFile(file,"rw");
            AuthService.connect();
            server = new ServerSocket(PORT);
            while(true){
                socket = server.accept();
                clientHandler = new ClientHandler(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert server != null;
                server.close();
                AuthService.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void subscribe(ClientHandler client) throws IOException {
            clients.add(client);
            ids.addElement(client.getId());
    }

    public void unsubscribe(ClientHandler client) throws IOException {
        clients.remove(client);
        ids.removeElement(client.getId());
    }

    public boolean isSubscribed(ClientHandler client){
        return ids.contains(client.getId());
    }
    public boolean isSubscribed(int id){
        return ids.contains(id);
    }
    public ClientHandler getClientByNick(String nick){
        for (ClientHandler c : clients) {
            if(c.getNickname().equals(nick)){
                return c;
            }
        }
        return null;
    }

    public void broadcastMsg(ClientHandler currentClient, String msg){
            for (ClientHandler client : clients) {
                if(client != currentClient) {
                    try {
                        if(!AuthService.isBlack(currentClient.getId(), client.getId())) {
                            client.sendMsg(currentClient.getNickname(), msg);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else{
                    client.sendSelfMsg(currentClient.getNickname(), msg);
                }
            }
    }

    public void broadcastMsg(ClientHandler currentClient, ClientHandler receiver, String msg){
        receiver.sendMsg(currentClient.getNickname(), msg);
        currentClient.sendMsg(currentClient.getNickname(), msg);
    }

    public Vector<Integer> getIds(){
        return ids;
    }
}
