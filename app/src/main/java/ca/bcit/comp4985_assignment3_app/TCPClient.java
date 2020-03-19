package ca.bcit.comp4985_assignment3_app;
/*------------------------------------------------------------------------------------------------------------------
-- SOURCE FILE: 	TCPClient.java - Wrapper class that contains methods to connect, send, and read from
--                                   a TCP Server
--
-- PROGRAM: 		SendLocationUpdates
--
-- FUNCTIONS:       TCPClient(String ip, int port)
--                  disconnect()
--                  sendData(String data)
--                  readData()
--
--
-- DATE: 			March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 		Victor Phan
--
-- PROGRAMMER: 		Victor Phan
--
-- NOTES:
--                  Wrapper class that connects to a TCP Server and has methods to read, write, and
--                  disconnect from the server.
--
--------------------------------------------------------------------------------------------------------------------*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TCPClient {
    private Socket client;
    private DataOutputStream outputToServer;
    private DataInputStream inputFromServer;

    /*-----------------------------------------------------------------------------------------------------------------
-- Constructor:	TCPClient
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	TCPClient(String ip, int port)
--
-- NOTES:
--              Connects to the specified server using ip and port.
--
-------------------------------------------------------------------------------------------------------------------*/
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

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	disconnect
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	disconnect()
--
-- NOTES:
--              Disconnects from the server.
--
-------------------------------------------------------------------------------------------------------------------*/
    public void disconnect() {
        try {
            outputToServer.close();
            inputFromServer.close();
            client.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	sendData
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	sendData(String data)
--
-- NOTES:
--              Sends a String to the server.
--
-------------------------------------------------------------------------------------------------------------------*/
    public boolean sendData(String data) throws IOException{
        // Send client string to server
        try {
            outputToServer.writeBytes(data);
            outputToServer.flush();
        }catch(IOException e) {
            e.printStackTrace();
            throw e;
        }
        return true;
    }

    /*-----------------------------------------------------------------------------------------------------------------
-- Function:	readData
--
-- DATE:		March 16, 2020
--
-- REVISIONS:
--
-- DESIGNER: 	Victor Phan
--
-- PROGRAMMER: 	Victor Phan
--
-- Interface:	readData()
--
-- NOTES:
--              Reads a string from the Server.
--
-------------------------------------------------------------------------------------------------------------------*/
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
