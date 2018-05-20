package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
    private ServerSocket serverSocket;
    private ArrayList<SocketServerThread> socketThreads;
    private int maxThreads;
    private boolean alive;
    private Map<String, ArrayList<String>> records;

    public SocketServer(int maxThreads) {
        alive = true;
        socketThreads = new ArrayList<>();
        records = new HashMap<>();
        this.maxThreads = maxThreads;
        fillBookRecords();
    }

    public Map<String, ArrayList<String>> getRecords() {
        return records;
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public ArrayList<SocketServerThread> getSocketThreads() {
        return socketThreads;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public boolean isAlive() {
        return alive;
    }

    private void fillBookRecords() {
        ArrayList<String> tmp = new ArrayList<>(Arrays.asList(new String[]{"Java Complete Ref.", "3rd", "10", "15$", "3%"}));
        records.put("CR2018", tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"Data Base- SQL", "1st", "30", "10$", "4%"}));
        records.put("RDBMS06", tmp);
        tmp = new ArrayList<>(Arrays.asList(new String[]{"Object Oriented Programming", "2nd", "20", "7$", "10"}));
        records.put("OOPS", tmp);
    }

    private synchronized void listenToClients() throws IOException {
        SocketServerThread newThread;
        Socket socket;
        // Always listen to new client socket connections
        while (alive) {
            System.out.println("Waiting for a client to connect");
            socket = serverSocket.accept();
            if (!alive) {
                break;
            }
            if (socketThreads.size() < maxThreads) {
                newThread = new SocketServerThread(socket, socketThreads, records);
                socketThreads.add(newThread);
                new Thread(newThread).start();
                System.out.println("Client connected to server successfully from: " + socket.getInetAddress().getHostAddress());
            } else {
                System.out.println("Maximum number of threads achieved, could not add another socket connection");
                socket.close();
            }
        }
        // Kill running socket threads before killing the server
        for (SocketServerThread currentThread : socketThreads) {
            currentThread.killThread();
        }
    }

    public void listen(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        System.out.println("Server is listening on port " + port + "\n");
        listenToClients();
    }

    public void killServer() throws IOException {
        alive = false;
        // Simulate a new client socket connection to exit the blocking code of line 42
        // and break out of the infinite loop
        new Socket(serverSocket.getInetAddress(), serverSocket.getLocalPort()).close();
        serverSocket.close();
    }

    public static void main(String[] args) {
        try {
            new SocketServer(20).listen(6000);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
