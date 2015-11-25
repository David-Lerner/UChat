package uchat.model;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client implements Runnable
{
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream streamIn = null;
    private DataOutputStream streamOut = null;
    private ClientThread client = null;

    public Client(String serverName, int serverPort)
    {  
        System.out.println("Establishing connection. Please wait ...");
        try
        {  
            socket = new Socket(serverName, serverPort);
            System.out.println("Connected: " + socket);
            start();
        }
        catch(UnknownHostException uhe)
        {  
            System.out.println("Host unknown: " + uhe.getMessage()); 
        }
        catch(IOException ioe)
        {  
            System.out.println("Unexpected exception: " + ioe.getMessage()); 
        }
    }
    
    @Override
    public void run()
    {  
        while (thread != null)
        {  
            try
            {
                Scanner in = new Scanner(System.in);
                streamOut.writeUTF(in.nextLine());
                //streamOut.writeUTF(streamIn.readLine());
                streamOut.flush();
            }
            catch(IOException ioe)
            {  
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }
    
    public void handle(String msg)
    {  
        if (msg.equals(".bye"))
        {  
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        }
        else
            System.out.println(msg);
    }
    
    public void start() throws IOException
    {  
        streamIn = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null)
        {  
            client = new ClientThread(this, socket);
            thread = new Thread(this);                   
            thread.start();
        }
    }
    
    public void stop()
    {  
        if (thread != null)
        {  
            thread.stop();  
            thread = null;
        }
        try
        {  
            if (streamIn != null)  
                streamIn.close();
            if (streamOut != null)  
                streamOut.close();
            if (socket != null)  
                socket.close();
        }
        catch(IOException ioe)
        {  
            System.out.println("Error closing ..."); 
        }
        client.close();  
        client.stop();
    }
    
    public static void main(String args[])
    {  
        Client client = null;
        if (args.length != 2)
        {
            //System.out.println("Usage: java ChatClient host port");
            client = new Client("0.0.0.0", 12345);
        }
        else
            client = new Client(args[0], Integer.parseInt(args[1]));
   }
}
