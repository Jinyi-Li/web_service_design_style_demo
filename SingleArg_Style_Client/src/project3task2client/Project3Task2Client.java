package project3task2client;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
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
public class Project3Task2Client {
        
    /*
     * RSA components for private and public keys. 
     */
    private static final BigInteger e = new BigInteger("65537");
    private static final BigInteger d = new BigInteger("339177647280468990599683753475404338964037287357290649"
            + "639740920420195763493261892674937712727426153831055473238029100"
            + "340967145378283022484846784794546119352371446685199413453480215"
            + "164979267671668216248690393620864946715883011485526549108913");
    private static final BigInteger n = new BigInteger("268852025517901502623747873143657162103121815451557296"
            + "872758837706559866377091251333301800665424865065625091311087483"
            + "660777796686710629019261833666084998095639973296736997628150027"
            + "0286450313199586861977623503348237855579434471251977653662553");
    
    /*
     * Encrypt request content with client's RSA private key.
     * @param clear
     * @return 
     */
    private static BigInteger encryptWithPrivKey(BigInteger clear){
        return clear.modPow(d, n);
    }
    
    /**
     * the current system time.
     * @return
     */
    private static Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }
    
    /*
     * Helper method. calculete hash value of a given message string.
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static String calculateHash(String message) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            digester.update(message.getBytes());
            byte[] digestedMsg = digester.digest();
            return DatatypeConverter.printHexBinary(digestedMsg);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Project3Task2Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*
     * Encrypt message with SHA-256 and sign it with client's private key. 
     * @param message
     * @return 
     */
    private static String encryptMessage(String message){
        try {
            // get hash value
            String hashInHex = calculateHash(message);                      
            byte[] hashInBin = hashInHex.getBytes("UTF-8");
            // get signed value
            BigInteger clear = new BigInteger(hashInBin);
            BigInteger crypo = encryptWithPrivKey(clear);                   
            return crypo.toString();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Project3Task2Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /*
     * Display main menu.
     */
    private static void displayMenu(){
        String menu =                
                "1. Add a transaction to the blockchain.\n" +
                "2. Verify the blockchain.\n" +
                "3. View the blockchain.\n" +                
                "4. Exit\n";
        System.out.print(menu);
    }
    
    /*
     * Dispatch user-input options to different actions.
     * @param option
     * @param scanner
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static int dispatchOptions(project3task2server.BlockChainService port, 
            String option, Scanner scanner) {
        Timestamp start, end;
        String xmlString;
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
                xmlString = "<payload><option>addBlock</option><message>" + message + "</message></payload>";                   
                request(xmlString);                  
                end = getTime();
                System.out.println("Total execution time to add this block " +
                        "was "+ (end.getTime() - start.getTime()) +" milliseconds");
                break;
            case "2":
                xmlString = "<payload><option>verifyChain</option></payload>"; 
                start = getTime();                                                
                boolean isValid = Boolean.valueOf(request(xmlString));
                end = getTime();
                System.out.println(
                        "Verifying entire chain\n" +
                        "Chain verification: " + isValid + "\n" +
                        "Total execution time required to verify the chain " +
                        "was " + (end.getTime() - start.getTime()) + " milliseconds");
                break;
            case "3":
                xmlString = "<payload><option>viewBlocks</option></payload>"; 
                System.out.println("View the Blockchain\n" +
                        request(xmlString));
                break;
            case "4":
                return 1;
        }
        return 0;
    }

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        project3task2server.BlockChainService_Service service = 
                new project3task2server.BlockChainService_Service();
        project3task2server.BlockChainService port = 
                service.getBlockChainServicePort();
       
        Scanner scanner = new Scanner(System.in);
        String option;

        while(true){
            displayMenu();
            option = scanner.nextLine();
            int res = dispatchOptions(port, option, scanner);
            if(res == 1){
                break;
            }
        } 
    }

    /*
     * Web service api to send message to server.
     * @param message
     * @return 
     */
    private static String request(java.lang.String message) {
        project3task2server.BlockChainService_Service service = 
                new project3task2server.BlockChainService_Service();
        project3task2server.BlockChainService port = 
                service.getBlockChainServicePort();
        return port.request(message);
    }   
}
