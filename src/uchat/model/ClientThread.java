package uchat.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientThread extends Thread
{
  private Socket socket = null;
  private Client client = null;
  /*private DataInputStream streamIn = null;*/
  private ObjectInputStream streamIn = null;

  public ClientThread(Client clientIn, Socket socketIn) {
    client = clientIn;
    socket = socketIn;
    open();
    start();
  }

  /*public void open()
  {
      try
      {
          streamIn  = new DataInputStream(socket.getInputStream());
      }
      catch(IOException ioe)
      {
          System.out.println("Error getting input stream: " + ioe);
          client.stop();
      }
  }*/
  protected void open() {
    try {
      streamIn = new ObjectInputStream(socket.getInputStream());
    } catch (IOException ioe) {
      //System.out.println("Error getting input stream: " + ioe);
      client.addMessage(new Message("Error getting input stream: " + ioe,
          Message.messageType.ERROR));
      client.stop();
    }
  }

  public void close() {
    try {
      if (streamIn != null)
        streamIn.close();
    } catch (IOException ioe) {
      //System.out.println("Error closing input stream: " + ioe);
      client.addMessage(new Message("Error closing input stream: " + ioe,
          Message.messageType.ERROR));
    }
  }

  @Override
    /*public void run()
    {  
        while (true)
        {  
            try
            {  
                client.handle(streamIn.readUTF());
            }
            catch(IOException ioe)
            {  
                System.out.println("Listening error: " + ioe.getMessage());
                client.stop();
            }
        }
    }*/
  public void run() {
    while (true) {
      try {
        client.handle((Message) streamIn.readObject());
      } catch (IOException ioe) {
        //System.out.println("Listening error: " + ioe.getMessage());
        client.addMessage(new Message("Listening error: " +
            ioe.getMessage(), Message.messageType.ERROR));
        client.stop();
      } catch (ClassNotFoundException ex) {
        //System.out.println("Listening error: " + ex.getMessage());
        client.addMessage(new Message("Listening error: " +
            ex.getMessage(), Message.messageType.ERROR));
        client.stop();
      }
    }
  }

}
