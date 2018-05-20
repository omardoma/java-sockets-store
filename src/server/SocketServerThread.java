package server;

import sendables.ClientReply;
import sendables.Enquiry;
import sendables.ServerReply;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

public class SocketServerThread implements Runnable {
    private Socket socket;
    private ObjectInputStream serverInput;
    private ObjectOutputStream serverOutput;
    private ArrayList<SocketServerThread> socketThreads;
    private Map<String, ArrayList<String>> records;
    private Enquiry currentEnquiry;
    private boolean alive;

    public SocketServerThread(Socket socket, ArrayList<SocketServerThread> socketThreads, Map<String, ArrayList<String>> records) throws IOException {
        this.socket = socket;
        this.socketThreads = socketThreads;
        this.records = records;
        alive = true;
        serverOutput = new ObjectOutputStream(this.socket.getOutputStream());
        serverInput = new ObjectInputStream(this.socket.getInputStream());
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getServerInput() {
        return serverInput;
    }

    public ObjectOutputStream getServerOutput() {
        return serverOutput;
    }

    public ArrayList<SocketServerThread> getSocketThreads() {
        return socketThreads;
    }

    public Map<String, ArrayList<String>> getRecords() {
        return records;
    }

    public boolean isAlive() {
        return alive;
    }

    public Enquiry getCurrentEnquiry() {
        return currentEnquiry;
    }

    private void sendToClient(Object message) throws IOException {
        serverOutput.writeObject(message);
        serverOutput.flush();
    }

    private void cleanUp() throws IOException {
        serverOutput.close();
        serverInput.close();
        socket.close();

        // Make sure no other thread is accessing the shared ArrayList before accessing it
        synchronized (socketThreads) {
            socketThreads.remove(this);
        }
    }

    private void checkEnquiry(Enquiry enquiry) throws IOException {
        currentEnquiry = enquiry;
        if (records.containsKey(enquiry.getBookId())) {
            if (records.get(enquiry.getBookId()).contains(enquiry.getBookName())) {
                sendToClient(new ServerReply("Please select any one option\n" + "1: Book Edition information\n" + "2: No. of copies available "));
            } else {
                sendToClient(new ServerReply("The name you have entered is incorrect, please try again", true));
            }
        } else {
            sendToClient(new ServerReply("The ID you have entered is incorrect, please try again", true));
        }
    }

    private void respond(int option) throws IOException {
        if (option == 1) {
            sendToClient(new ServerReply("Book Edition: " + records.get(currentEnquiry.getBookId()).get(1)));
        } else if (option == 2) {
            sendToClient(new ServerReply("Number of copies available is: " + records.get(currentEnquiry.getBookId()).get(2) + "\n" + "Price is : " + records.get(currentEnquiry.getBookId()).get(3)));
        } else {
            sendToClient(new ServerReply("Invalid input please try again", true));
        }
    }

    @Override
    public void run() {
        while (alive) {
            try {
                Object incomingMessage = serverInput.readObject();
                if (!alive) {
                    break;
                }
                // Here handle the logic of getting the options and sending it to him
                if (incomingMessage instanceof Enquiry) {
                    checkEnquiry((Enquiry) incomingMessage);
                } else {
                    // Here handle the logic of sending the books
                    respond(((ClientReply) incomingMessage).getOptionNumber());
                }
            } catch (IOException | ClassNotFoundException e) {
                killThread();
            }
        }
        try {
            cleanUp();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void killThread() {
        alive = false;
    }
}
