package uchat.model;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server implements Runnable
{
    private ServerThread clients[] = new ServerThread[50];
    private ServerSocket server = null;
    private Thread thread = null;
    private int clientCount = 0;
    private ArrayList<Message> messageBuffer;
    private ArrayList<Message> messageLog;

    public Server(int port)
    {  
        messageBuffer = new ArrayList<>();
        messageLog = new ArrayList<>();
        try
        {  
            //System.out.println("Binding to port " + port + ", please wait  ...");
            addMessage(new Message("Binding to port " + port + ", please wait  ...", 
                    Message.messageType.ALERT));
            server = new ServerSocket(port);  
            //System.out.println("Server started: " + server);
            addMessage(new Message("Server started: " + server, 
                    Message.messageType.ALERT));
            start(); 
        }
        catch(IOException ioe)
        {  
            //System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
            addMessage(new Message("Can not bind to port " + port + ": " + ioe.getMessage(), 
                    Message.messageType.ERROR));
        }
    }

    protected synchronized void addMessage(Message msg)
    {
        messageBuffer.add(msg);
    }
    
    public synchronized ArrayList<Message> getMessages()
    {
        ArrayList<Message> temp = messageBuffer;
        messageBuffer = new ArrayList<>();
        return temp;
    }
    
    @Override
    public void run()
    {  
        while (thread != null)
        {  
            try
            {  
                //System.out.println("Waiting for a client ..."); 
                addThread(server.accept()); 
            }
            catch(IOException ioe)
            {  
                //System.out.println("Server accept error: " + ioe);
                addMessage(new Message("Server accept error: " + ioe.getMessage(), 
                    Message.messageType.ERROR));
                stop(); 
            }
        }
    }
    
    public void start()  
    {
        if (thread == null)
        {  
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
    }
    
    private int findClient(int ID)
    {  
        for (int i = 0; i < clientCount; i++)
            if (clients[i].getID() == ID)
                return i;
        return -1;
    }
   
    /*public synchronized void handle(int ID, String input)
    {  
        if (input.equals(".bye"))
        {  
            clients[findClient(ID)].send(".bye");
            remove(ID); 
        }
        else
        {
            for (int i = 0; i < clientCount; i++)
                clients[i].send(ID + ": " + input);
        }
    }*/
    protected synchronized void handle(int ID, Message msg)
    {  
        if (msg.getText().equals(".bye") || msg.getType() == Message.messageType.EXIT)
        {  
            clients[findClient(ID)].send(new Message("", Message.messageType.EXIT));
            remove(ID);
            for (int i = 0; i < clientCount; i++)
                clients[i].send(new Message(msg.getName() + " has left the chat.", Message.messageType.ALERT));
        }
        else if (msg.getType() == Message.messageType.CONNECT)
        {
            for (Message m : messageLog)
            {
                clients[findClient(ID)].send(m);
            }
            for (int i = 0; i < clientCount; i++)
            {
                if (i != findClient(ID))
                    clients[i].send(new Message(msg.getName() + 
                        " has joined the chat.", Message.messageType.ALERT));
            }
        }
        else
        {
            messageLog.add(msg);
            for (int i = 0; i < clientCount; i++)
                clients[i].send(msg);
        }
   }
    
   protected synchronized void remove(int ID)
   {  
        int pos = findClient(ID);
        if (pos >= 0)
        {  
            ServerThread toTerminate = clients[pos];
            //System.out.println("Removing client thread " + ID + " at " + pos);
            addMessage(new Message("Removing client thread " + ID + " at " + pos, 
                    Message.messageType.ALERT));
            if (pos < clientCount-1)
                for (int i = pos+1; i < clientCount; i++)
                    clients[i-1] = clients[i];
            clientCount--;
            try
            {  
                toTerminate.close(); 
            }
            catch(IOException ioe)
            {  
                //System.out.println("Error closing thread: " + ioe);
                addMessage(new Message("Error closing thread: " + ioe, 
                    Message.messageType.ERROR));
            }
            toTerminate.stop();
        }
    }
   
    private void addThread(Socket socket)
    {  
        if (clientCount < clients.length)
        {  
            //System.out.println("Client accepted: " + socket);
            addMessage(new Message("Client accepted: " + socket, 
                    Message.messageType.ALERT));
            clients[clientCount] = new ServerThread(this, socket);
            try
            {  
                clients[clientCount].open(); 
                clients[clientCount].start();  
                clientCount++; 
            }
            catch(IOException ioe)
            {  
                //System.out.println("Error opening thread: " + ioe); 
                addMessage(new Message("Error opening thread: " + ioe.getMessage(), 
                        Message.messageType.ERROR));
            } 
        }
        else
        {
            //System.out.println("Client refused: maximum " + clients.length + " reached.");
            addMessage(new Message("Client refused: maximum " + clients.length + " reached.", 
                    Message.messageType.ALERT));
        }
    }
    
}
