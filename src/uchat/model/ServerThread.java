package uchat.model;

import java.io.*;
import java.net.Socket;

/**
 * Chat serverThread class, the sever creates one of these for each connected client 
 * to handle simultaneous communications.
 * 
 * @author David Lerner
 * @version 1.0
 */
public class ServerThread extends Thread
{
  private Server server = null;
  private Socket socket = null;
  private int ID = - 1;
  private ObjectInputStream streamIn = null;
  private ObjectOutputStream streamOut = null;
  private boolean banned;
  private String address;
  private boolean stop;

  public ServerThread(Server serverIn, Socket socketIn) {
    super();
    server = serverIn;
    socket = socketIn;
    ID = socket.getPort();
    address = socket.getInetAddress().toString();
    banned = false;
    stop = false;
  }

  protected void send(Message msg) {
    try {
      streamOut.writeObject(msg);
      streamOut.flush();
    } catch (IOException ioe) {
      server.addMessage(new Message(ID + " ERROR sending: " +
          ioe.getMessage(), Message.messageType.ERROR));
      server.remove(ID);
      end();
    }
  }

  protected int getID() {
    return ID;
  }

  @Override
  public void run() {
    //System.out.println("Server Thread " + ID + " running.");
    server.addMessage(new Message("Server Commands: \nEnter '.list' to view all users currently in the chat room. "
            + "\nEnter '.ban id' where id is a user ID number obtained from '.list', "
            + "\nthis will ban that user's ip address from posting to the chat room.",
        Message.messageType.ALERT));
    while (!stop) {
      try {
        Message msg = (Message) streamIn.readObject();
        if (! banned || msg.getType() != Message.messageType.TEXT || msg.getText().equals(".bye"))
          server.handle(ID, msg);
        else {
          //server.addMessage(new Message(msg.getName() + " was blocked from posting.", Message.messageType.ALERT));
          server.handle(ID, new Message(socket.getInetAddress().toString(),
              Message.messageType.BAN));
        }
      } catch (IOException ioe) {
        server.addMessage(new Message(ID + " ERROR reading: " +
            ioe.getMessage(), Message.messageType.ERROR));
        server.remove(ID);
        end();
      } catch (ClassNotFoundException ex) {
        server.addMessage(new Message(ID + " ERROR reading: " +
            ex.getMessage(), Message.messageType.ERROR));
        server.remove(ID);
        end();
      }
    }
  }

  protected void open() throws IOException {
    streamIn = new ObjectInputStream(new
        BufferedInputStream(socket.getInputStream()));
    streamOut = new ObjectOutputStream(new
        BufferedOutputStream(socket.getOutputStream()));
    streamOut.flush();
  }

  protected void close() throws IOException {
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

  protected void end() {
      stop = true;
  }
}