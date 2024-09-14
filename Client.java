import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static void main(String[] args) {
		String host = "localhost"; // Dirección del servidor
		int port = 1234; // Puerto del servidor

		try (Socket socket = new Socket(host, port)) {
			System.out.println("Conectado al servidor");

			// Crear streams para enviar y recibir datos
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
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
			socket.close();
			System.out.println("Conexión cerrada");

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
