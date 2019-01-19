package messagestyleclient;

import java.util.Scanner;

/**
 * Generate SOAP requests as a client end, to manipulate a remote blockchain.
 * 
 * @author jinyili
 */
public class MessageStyleClient {

    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        messagestyleserver.BlockChainService_Service service = 
                new messagestyleserver.BlockChainService_Service();
        messagestyleserver.BlockChainService port = 
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
        messagestyleserver.BlockChainService_Service service = 
                new messagestyleserver.BlockChainService_Service();
        messagestyleserver.BlockChainService port = 
                service.getBlockChainServicePort();
        return port.request(message);
    }   
}
