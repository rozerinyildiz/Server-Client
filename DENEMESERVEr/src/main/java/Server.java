import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class Server
{
    private static HttpURLConnection connection;

    //initialize socket and input stream
    private Socket          socket   = null;
    private ServerSocket    server   = null;
    private DataInputStream in       =  null;

    // constructor with port
    public Server(int port)
    {
        // starts server and waits for a connection
        try
        {
            server = new ServerSocket(port);
            System.out.println("Server started");

            System.out.println("Waiting for a client ...");

            socket = server.accept();
            System.out.println("Client accepted");

            // takes input from the client socket
            in = new DataInputStream(
                    new BufferedInputStream(socket.getInputStream()));

            String line = "";

            // reads message from client until "Stop" is sent
            while (!line.equals("Stop"))
            {
                try
                {

                    line = in.readUTF();

                    String[] words=line.split(",");

                    //System.out.println(line);
                    // Create a neat value object to hold the URL
                    URL url = new URL("/deviceLocations");
                    // Open a connection(?) on the URL(??) and cast the response(???)
                    connection = (HttpURLConnection) url.openConnection();
                    // Now it's "open", we can set the request method, headers etc.
                    connection.setRequestProperty("accept", "application/json");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    OutputStream os = connection.getOutputStream();


                    if(words[0].equals("1")){
                        String str = "{\n\"imei\": \"" +words[1]+ "\",\n"+
                                "\"latitude\": \"" +words[2]+ "\",\n"+
                                "\"longitude\": \"" + words[3]+"\" \n}";
                        byte[] input = str.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int status = connection.getResponseCode();
                    System.out.println(status);

                }
                catch(IOException i)
                {
                    System.out.println(i);
                }
            }
            System.out.println("Closing connection");

            // close connection
            socket.close();
            in.close();
        }
        catch(IOException i)
        {
            System.out.println(i);
        }
    }

    public static void main(String args[])
    {
        Server server = new Server( 443);
    }
}
