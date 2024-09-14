import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

// Clase que extiende Thread para manejar cada cliente en un hilo independiente
class ClientHandler extends Thread {
	private Socket clientSocket;

	public ClientHandler(Socket socket) {
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

		try (ServerSocket serverSocket = new ServerSocket(port)) {
			System.out.println("Servidor escuchando en el puerto " + port);

			// Bucle infinito para aceptar conexiones de múltiples clientes
			while (true) {
				Socket clientSocket = serverSocket.accept(); // Espera por la conexión de un cliente
				System.out.println("Cliente conectado: " + clientSocket.getRemoteSocketAddress());

				// Crear y arrancar un nuevo hilo para manejar al cliente
				ClientHandler clientHandler = new ClientHandler(clientSocket);
				clientHandler.start(); // Iniciar el hilo para el cliente
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
