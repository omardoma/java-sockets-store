package client;

import sendables.ClientReply;
import sendables.Enquiry;
import sendables.ServerReply;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class SocketClient {
    private int id;
    private Socket socket;
    private ObjectInputStream clientInput;
    private ObjectOutputStream clientOutput;

    public SocketClient(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getClientInput() {
        return clientInput;
    }

    public ObjectOutputStream getClientOutput() {
        return clientOutput;
    }

    private void sendToServer(Object message) throws IOException {
        clientOutput.writeObject(message);
        clientOutput.flush();
    }

    private ServerReply getServerReply() throws IOException, ClassNotFoundException {
        return ((ServerReply) clientInput.readObject());
    }

    public void connectToServer(String serverAddress, int serverPort) throws IOException, ClassNotFoundException {
        socket = new Socket(serverAddress, serverPort);
        clientInput = new ObjectInputStream(socket.getInputStream());
        clientOutput = new ObjectOutputStream(socket.getOutputStream());
        initiate();
    }

    private void initiate() throws IOException, ClassNotFoundException {
        Scanner sc = new Scanner(System.in);
        String bookId, bookName;
        ServerReply reply;
        int chosenOption;
        while (true) {
            System.out.print("Book_ID: ");
            bookId = sc.nextLine();
            System.out.print("Book_Name: ");
            bookName = sc.nextLine();
            sendToServer(new Enquiry(bookId, bookName));
            reply = getServerReply();
            System.out.println(reply.getMessage());
            if (reply.isError()) {
                continue;
            }
            // Handle if the user entered an option String not an int
            while (true) {
                try {
                    chosenOption = sc.nextInt();
                    sendToServer(new ClientReply(chosenOption));
                    reply = getServerReply();
                    System.out.println(reply.getMessage());
                    // Skip line because nextInt doesn't when you press enter
                    sc.nextLine();
                    break;
                } catch (InputMismatchException e) {
                    System.out.println(reply.getMessage());
                    // Skip line because nextInt doesn't when you press enter
                    sc.nextLine();
                }
            }
        }
    }

    public static void main(String[] args) {
        SocketClient client = new SocketClient(new Random().nextInt());
        try {
            client.connectToServer("localhost", 6000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
