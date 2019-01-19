package rpc_style;

import java.util.Scanner;

/**
 * Generate SOAP requests as a client end, to manipulate a remote blockchain.
 * 
 * @author jinyili
 */
public class SOAPClient {
    /**
     * Main method
     * @param args
     */
    public static void main(String[] args) {
        project3task1server.BlockChainService_Service service = 
                new project3task1server.BlockChainService_Service();
        project3task1server.BlockChainService port = service.getBlockChainServicePort();
       
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
     * Web service api to validate the chain. 
     * @return 
     */ 
    private static boolean verifyChain() {
        project3task1server.BlockChainService_Service service = 
                new project3task1server.BlockChainService_Service();
        project3task1server.BlockChainService port = service.getBlockChainServicePort();
        return port.verifyChain();
    }

    /*
     * Web service api to view all blocks.
     * @return 
     */
    private static String viewBlocks() {
        project3task1server.BlockChainService_Service service = 
                new project3task1server.BlockChainService_Service();
        project3task1server.BlockChainService port = service.getBlockChainServicePort();
        return port.viewBlocks();
    }

    /*
     * Web service api to add a new Block.
     * @param payload 
     */
    private static void addBlock(java.lang.String payload) {
        project3task1server.BlockChainService_Service service = new project3task1server.BlockChainService_Service();
        project3task1server.BlockChainService port = service.getBlockChainServicePort();
        port.addBlock(payload);
    }   
}
