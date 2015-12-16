package uchat.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Chat clientThread class, the client creates this to listen to messages from the server.
 * 
 * @author David Lerner
 * @version 1.0
 */
public class ClientThread extends Thread
{
  private Socket socket = null;
  private Client client = null;
  private ObjectInputStream streamIn = null;
  private boolean stop;

  public ClientThread(Client clientIn, Socket socketIn) {
      stop = false;
    client = clientIn;
    socket = socketIn;
    open();
    start();
  }

  protected void open() {
    try {
      streamIn = new ObjectInputStream(socket.getInputStream());
    } catch (IOException ioe) {
      client.addMessage(new Message("Error getting input stream: " + ioe,
          Message.messageType.ERROR));
      client.end();
    }
  }

  protected void close() {
    try {
      if (streamIn != null)
        streamIn.close();
    } catch (IOException ioe) {
      client.addMessage(new Message("Error closing input stream: " + ioe,
          Message.messageType.ERROR));
    }
  }

  @Override
  public void run() {
    while (!stop) {
      try {
        client.handle((Message) streamIn.readObject());
      } catch (IOException ioe) {
        client.addMessage(new Message("Listening error: " +
            ioe.getMessage(), Message.messageType.ERROR));
        client.end();
      } catch (ClassNotFoundException ex) {
        client.addMessage(new Message("Listening error: " +
            ex.getMessage(), Message.messageType.ERROR));
        client.end();
      }
    }
  }

  protected void end() {
      stop = true;
  }
}
