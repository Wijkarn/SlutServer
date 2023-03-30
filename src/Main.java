import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        System.out.println("Server is ready.");

        //Init stuff
        ServerSocket serverSocket;
        Socket socket;
        InputStreamReader inputSR;
        OutputStreamWriter outputSW;
        BufferedReader bReader;
        BufferedWriter bWriter;

        // Start the server

        try {
            // Check if socket is free. Abort otherwise
            serverSocket = new ServerSocket(42069);
            //System.out.println(serverSocket.getInetAddress());
            //System.out.println(serverSocket.getLocalSocketAddress());
        }
        catch (IOException e) {
            System.out.println(e);
            return;
        }

        try {
            // Waiting on specific socket for traffic
            socket = serverSocket.accept();

            // Init Reader and Writer and connect them to socket
            inputSR = new InputStreamReader(socket.getInputStream());
            outputSW = new OutputStreamWriter(socket.getOutputStream());

            bReader = new BufferedReader(inputSR);
            bWriter = new BufferedWriter(outputSW);

            while (true) {
                // Get clients message and send it to openUpData()
                // Returns a complete JSONObjekt which is sent back to the client
                String message = bReader.readLine();
                String returnData = openUpData(message);

                System.out.println("Message Received and sent back");

                // Send acknowledgement or answer back
                bWriter.write(returnData);
                bWriter.newLine();
                bWriter.flush();

                // shut down if message is "quit"
                if (message.equalsIgnoreCase("quit")) break;
            }
            // Close connections
            socket.close();
            inputSR.close();
            outputSW.close();
            bReader.close();
            bWriter.close();

        } catch (IOException e) {
            System.out.println(e);
        } catch (ParseException e) {
            System.out.println(e);
        } finally {
            System.out.println("Server Quits.");
        }
    }

    static String openUpData(String message) throws ParseException, IOException {
        //System.out.println(message);
        // Builds upp JSONObject based on incomming string
        JSONParser parser = new JSONParser();
        JSONObject jsonOb = (JSONObject) parser.parse(message);

        // Reads URL and HTTP-method to know what the client wants
        //String url = jsonOb.get("httpURL").toString();
        String method = jsonOb.get("httpMethod").toString();

        // Splits the String to get clients message
        //String[] urls = url.split("/");

        //System.out.println(urls[0]);

        // Check if GET or POST
        if (method.equals("get")) {
            // Get data about people

            // Create JSONReturn Object
            JSONObject jsonReturn = new JSONObject();

            // Gets data from JSON file
            jsonReturn.put("data", parser.parse(new FileReader("data/data.json")).toString());

            // Include HTTP status code
            jsonReturn.put("httpStatusCode", 200);

            //Return
            return jsonReturn.toJSONString();
        }

        return "message Recieved";
    }
}