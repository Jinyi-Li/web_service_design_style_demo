package resourcestyleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.DatatypeConverter;

/**
 * Generate SOAP requests as a client end, to manipulate a remote blockchain.
 * 
 * @author jinyili
 */
public class ResourceStyleClient {
    
    /**
     * Establish connection with server to send request and retrieve response.
     * @param url
     * @return 
     */    
    private static String request(String option, String message){
        String base = "http://localhost:8080/ResourceStyleServer/BlockChainService/";        
        HttpURLConnection connection = null;            
        try {            
            URL url = new URL(base + option);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "text/html");
            if(option.equals("addBlock")){
                connection.setRequestMethod("POST");                                
                String params = "message=" + message;
                byte[] paramsBytes = params.getBytes("UTF-8");
                connection.setRequestProperty("Content-Length", String.valueOf(paramsBytes.length));
                connection.setDoOutput(true);
                connection.getOutputStream().write(paramsBytes);                
            }else{
                connection.setRequestMethod("GET"); 
            }                                               
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String res;
            if((res = reader.readLine()) != null){
                return res;
            }
            connection.disconnect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return "";                        
    }

    /*
     * Dispatch user-input options to different actions.
     * @param option
     * @param scanner
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static int dispatchOptions( String option, Scanner scanner) {
        Timestamp start, end;
        String query;            

        switch(option){
            case "1":
                System.out.print("Enter difficulty > ");
                int difficulty = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter transaction");
                String data = scanner.nextLine();
                start = getTime();                              
                String payload = data + "," + difficulty;                                           
                // send out plain payload with the encrypted payload
                String message = payload + "#" + encryptMessage(payload);                                
                request("addBlock", message);
                end = getTime();
                break;
            case "2":                    
                start = getTime();
                boolean isValid = Boolean.valueOf(request("verifyChain", null));
                end = getTime();
                System.out.println(
                        "Verifying entire chain\n" +
                        "Chain verification: " + isValid);
                break;
            case "3":                                        
                System.out.println("View the Blockchain\n" +
                        request("viewBlocks", null));
                break;
            default:
                break;
            case "4":
                return 1;
        }           
        return 0;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String option;

        while(true){
            displayMenu();
            option = scanner.nextLine();
            int res = dispatchOptions(option, scanner);
            if(res == 1){
                break;
            }
        } 
    }   
    
}
