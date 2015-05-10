package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import files.AuthenticationSystem;

public class Authenticator implements Runnable {

	private Socket socketSimple;
	private Socket socketFile;
	
	public Authenticator(Socket socketSimple, Socket socketObject) {
		this.socketSimple = socketSimple;
		this.socketFile = socketObject;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("SERVEUR : Authentification du client " + socketSimple.getInetAddress());
			AuthenticationSystem authSystem = new AuthenticationSystem();
			ObjectOutputStream printer = new ObjectOutputStream(socketSimple.getOutputStream());
			ObjectInputStream buffer = new ObjectInputStream(socketSimple.getInputStream());
			
			printer.writeObject("#AUTH");
			try {
				String passwordReceived = (String) buffer.readObject();
				boolean passwordOK = authSystem.verifyPassword(passwordReceived);
				System.out.println("SERVEUR : Mot de passe re�u du client " + socketSimple.getInetAddress().getHostAddress() + " : " + passwordReceived);
				Server server = Server.getInstance();
				if (passwordOK) {
					System.out.println("SERVEUR : Authentification OK...");
					Client newClient = new Client(socketSimple, socketFile, server);
					newClient.setBuffers(printer, buffer);
					
					sendReadingQueueToRemote(newClient); 
					server.updateClientList(newClient); 
					new Thread(newClient).start(); //D�marrage du traitement client
				} else
					System.out.println("SERVEUR : Authentification FAILED...");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Erreur lors de la r�ception du mot de passe venant du client !");
			} catch (ClassNotFoundException e) {}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Erreur lors de la cr�ation de flux avec le client !");
		}
	}
	
	public void sendReadingQueueToRemote(Client c) 
	{
		c.sendSerializable("#RQ"); //Constante pour reading queue
		String rep = (String) c.readSerializable();
		if (rep.equals("#OK")) {
			if (c.sendSerializable(Server.getInstance().getReadingQueue())) {
				System.out.println("SERVEUR : Envoi de la reading queue OK...");
			}
		} else
			System.out.println("SERVEUR : Erreur lors de l'envoi de la reading queue");
	}

}
