package uchat.model;

import java.io.*;
import java.net.Socket;

public class ServerThread extends Thread
{
  private Server server = null;
  private Socket socket = null;
  private int ID = - 1;
  /*private DataInputStream streamIn = null;
  private DataOutputStream streamOut = null;*/
  private ObjectInputStream streamIn = null;
  private ObjectOutputStream streamOut = null;
  private boolean banned;
  private String address;

  public ServerThread(Server serverIn, Socket socketIn) {
    super();
    server = serverIn;
    socket = socketIn;
    ID = socket.getPort();
    address = socket.getInetAddress().toString();
    banned = false;
  }

  /*public void send(String msg)
  {
      try
      {
          streamOut.writeUTF(msg);
          streamOut.flush();
      }
      catch(IOException ioe)
      {
          System.out.println(ID + " ERROR sending: " + ioe.getMessage());
          server.remove(ID);
          stop();
      }
  }*/
  protected void send(Message msg) {
    try {
      streamOut.writeObject(msg);
      streamOut.flush();
    } catch (IOException ioe) {
      //System.out.println(ID + " ERROR sending: " + ioe.getMessage());
      server.addMessage(new Message(ID + " ERROR sending: " +
          ioe.getMessage(), Message.messageType.ERROR));
      server.remove(ID);
      stop();
    }
  }

  protected int getID() {
    return ID;
  }

  @Override
    /*public void run()
    {  
        System.out.println("Server Thread " + ID + " running.");
        while (true)
        {  
            try
            {  
                server.handle(ID, streamIn.readUTF());
            }
            catch(IOException ioe)
            {  
                System.out.println(ID + " ERROR reading: " + ioe.getMessage());
                server.remove(ID);
                stop();
            }
        }
    }*/
  public void run() {
    //System.out.println("Server Thread " + ID + " running.");
    server.addMessage(new Message("Server Thread " + ID + " running.",
        Message.messageType.ALERT));
    while (true) {
      try {
        Message msg = (Message) streamIn.readObject();
        if (! banned || msg.getType() != Message.messageType.TEXT || msg.getText().equals(".bye"))
          server.handle(ID, msg);
        else {
//          server.addMessage(new Message(msg.getName() + " was blocked from posting.", Message.messageType.ALERT));
          server.handle(ID, new Message(socket.getInetAddress().toString(),
              Message.messageType.BAN));
        }
      } catch (IOException ioe) {
        //System.out.println(ID + " ERROR reading: " + ioe.getMessage());
        server.addMessage(new Message(ID + " ERROR reading: " +
            ioe.getMessage(), Message.messageType.ERROR));
        server.remove(ID);
        stop();
      } catch (ClassNotFoundException ex) {
        //System.out.println(ID + " ERROR reading: " + ex.getMessage());
        server.addMessage(new Message(ID + " ERROR reading: " +
            ex.getMessage(), Message.messageType.ERROR));
        server.remove(ID);
        stop();
      }
    }
  }

  /*public void open() throws IOException
  {
      streamIn = new DataInputStream(new
                      BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                      BufferedOutputStream(socket.getOutputStream()));
  }*/
  protected void open() throws IOException {
    streamIn = new ObjectInputStream(new
        BufferedInputStream(socket.getInputStream()));
    streamOut = new ObjectOutputStream(new
        BufferedOutputStream(socket.getOutputStream()));
    streamOut.flush();
  }

  public void close() throws IOException {
    if (socket != null)
      socket.close();
    if (streamIn != null)
      streamIn.close();
    if (streamOut != null)
      streamOut.close();
  }

  protected boolean isBanned() {
    return banned;
  }

  protected void setBanned(boolean banned) {
    this.banned = banned;
  }

}