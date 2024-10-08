package server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

// Clase que extiende Thread para manejar cada cliente en un hilo independiente
class ClientHandler extends Thread {
	private SSLSocket clientSocket;

	public ClientHandler(SSLSocket socket) {
		this.clientSocket = socket;
	}

	@Override
	public void run() {
		try {
			// Crear streams para enviar y recibir datos
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

			// Comunicación con el cliente
			String messageFromClient;
			while ((messageFromClient = in.readLine()) != null) {
				if ("exit".equalsIgnoreCase(messageFromClient)) { // Si el cliente envía "exit", cerrar la conexión
					System.out.println("Cliente (" + clientSocket.getRemoteSocketAddress() + ") se ha desconectado.");
					break;
				}

				System.out.println("Cliente (" + clientSocket.getRemoteSocketAddress() + ") dice: " + messageFromClient);
				out.println("Servidor ha recibido: " + messageFromClient);
			}

			// Cerrar la conexión
			in.close();
			out.close();
			clientSocket.close();
			System.out.println("Conexión cerrada para el cliente: " + clientSocket.getRemoteSocketAddress());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

public class Server {
	public static void main(String[] args) {
		int port = 1234; // Puerto donde escuchará el servidor

		try {
			// Cargar el almacén de claves
			KeyStore keyStore = KeyStore.getInstance("JKS");
			keyStore.load(new FileInputStream("server/serverkeystore.jks"), "123456".toCharArray());

			// Inicializar KeyManagerFactory con el almacén de claves
			KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			keyManagerFactory.init(keyStore, "123456".toCharArray());

			// Configurar SSLContext con KeyManager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

			// Crear SSLServerSocketFactory a partir del SSLContext
			SSLServerSocketFactory sslServerSocketFactory = sslContext.getServerSocketFactory();
			SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(port);

			System.out.println("Servidor escuchando en el puerto " + port);

			// Bucle infinito para aceptar conexiones de múltiples clientes
			while (true) {
				SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept(); // Espera por la conexión de un cliente
				System.out.println("Cliente conectado: " + sslSocket.getRemoteSocketAddress());

				// Crear y arrancar un nuevo hilo para manejar al cliente
				ClientHandler clientHandler = new ClientHandler(sslSocket);
				clientHandler.start(); // Iniciar el hilo para el cliente
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
