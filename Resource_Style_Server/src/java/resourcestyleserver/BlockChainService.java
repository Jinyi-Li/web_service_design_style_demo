package soapserver;

import java.math.BigInteger;
import java.sql.Timestamp;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;

import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handle SOAP requests as the server end, to manipulate a blockchain.
 * 
 * @author jinyili
 */
@WebService(serviceName = "BlockChainService")
public class BlockChainService {

    /**
     * Web service operation
     * @return 
     */
    @WebMethod(operationName = "verifyChain")
    public boolean verifyChain() {          
        return isChainValid();
    }

    /**
     * Web service operation
     * @return 
     */
    @WebMethod(operationName = "viewBlocks")
    public String viewBlocks() {
        return toString();        
    }

    /**
     * Web service operation.
     * @param payload
     */
    @WebMethod(operationName = "addBlock")
    public void addBlock(@WebParam(name = "payload") String payload) {
        boolean isValidClient = verifyClient(twoParts);
        if(!isValidClient){
            return;
        }                
        addNewBlock(payload);
    }
}
