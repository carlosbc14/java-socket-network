package client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client {
	public static void main(String[] args) {
		String host = "localhost"; // Dirección del servidor
		int port = 1234; // Puerto del servidor

		try {
			// Cargar el almacén de claves del cliente
			KeyStore trustStore = KeyStore.getInstance("JKS");
			trustStore.load(new FileInputStream("client/clienttruststore.jks"), "123456".toCharArray());

			// Inicializar TrustManagerFactory con el almacén de claves
			TrustManagerFactory trustManagerFactory = TrustManagerFactory
					.getInstance(TrustManagerFactory.getDefaultAlgorithm());
			trustManagerFactory.init(trustStore);

			// Configurar SSLContext con TrustManager
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManagerFactory.getTrustManagers(), null);

			// Crear SSLSocketFactory a partir del SSLContext
			SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
			SSLSocket sslSocket = (SSLSocket) sslSocketFactory.createSocket(host, port);

			System.out.println("Conectado al servidor");

			// Crear streams para enviar y recibir datos
			BufferedReader in = new BufferedReader(new InputStreamReader(sslSocket.getInputStream()));
			PrintWriter out = new PrintWriter(sslSocket.getOutputStream(), true);
			BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

			// Enviar mensajes al servidor
			String messageToSend;
			while ((messageToSend = userInput.readLine()) != null) {
				out.println(messageToSend); // Enviar mensaje al servidor

				if ("exit".equalsIgnoreCase(messageToSend)) { // Si el usuario escribe "exit", cerrar la conexión
					System.out.println("Desconectándose del servidor...");
					break;
				}

				System.out.println("Servidor responde: " + in.readLine()); // Leer respuesta del servidor
			}

			// Cerrar la conexión y liberar recursos
			in.close();
			out.close();
			sslSocket.close();
			System.out.println("Conexión cerrada");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
