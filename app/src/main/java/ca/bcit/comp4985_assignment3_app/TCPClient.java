package ca.bcit.comp4985_assignment3_app;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    private Socket client;
    private DataOutputStream outputToServer;
    private DataInputStream inputFromServer;
    public TCPClient(String ip, int port) throws IOException{
        try {
            client = new Socket(ip,port);
            outputToServer = new DataOutputStream (client.getOutputStream());
            inputFromServer = new DataInputStream (client.getInputStream());
        }catch(IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void disconnect() {
        try {
            outputToServer.close();
            inputFromServer.close();
            client.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean sendData(String data) throws IOException{
        // Send client string to server
        try {
            outputToServer.writeUTF(data);
        }catch(IOException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    public String readData() throws IOException{
        // Send client string to server
        String data = "";
        try {
            data = inputFromServer.readUTF();
        }catch(IOException e) {
            e.printStackTrace();
            throw e;
        }
        return data;
    }


}
