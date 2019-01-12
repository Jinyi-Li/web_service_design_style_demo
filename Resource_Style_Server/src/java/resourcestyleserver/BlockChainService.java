package resourcestyleserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

/**
 * Handle SOAP requests as the server end, to manipulate a blockchain.
 * 
 * @author jinyili
 */
@WebServlet(name = "BlockChainService", urlPatterns = {"/BlockChainService"})
public class BlockChainService extends HttpServlet {

    /* Private member fields of a BlockChain object. */

    /* An ArrayList to hold Blocks. */
    private ArrayList<Block> blocks;
    /* SHA256 hash of the most recently added Block. */
    private String mostRecentAddedHash;
    /* RSA components for private and public keys. */
    private static final BigInteger e = new BigInteger("65537");
    private static final BigInteger n = new BigInteger("268852025517901502623747873143657162103121815451557296"
            + "872758837706559866377091251333301800665424865065625091311087483"
            + "660777796686710629019261833666084998095639973296736997628150027"
            + "0286450313199586861977623503348237855579434471251977653662553");
    
    /*
     * Decrypt request content with client's RSA public key.
     * @param crypo
     * @return 
     */
    private static BigInteger decryptWithPubKey(BigInteger crypo){
        return crypo.modPow(e, n);
    }
    
    @Override
    public void init(ServletConfig config) throws ServletException{
        super.init(config);
        blocks = new ArrayList<>();
        mostRecentAddedHash = null;
        addNewBlock(new Block(0, this.getTime(), "Genesis", 2));
    }
    
    /**
     * A new Block is being added to the BlockChain.
     * @param newBlock
     */
    protected void addNewBlock(Block newBlock){        
        newBlock.setPreviousHash(mostRecentAddedHash);          
        try{
            newBlock.proofOfWork();
        }catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }                
        blocks.add(newBlock);
        mostRecentAddedHash = newBlock.getCurrentHash();
        String proof = newBlock.getIndex() + newBlock.getTimestamp().toString() +
                    newBlock.getData() + newBlock.getPreviousHash() + (newBlock.getNonce())
                    + newBlock.getDifficulty();
    }
    
    /**
     * If the chain only contains one block, the genesis block at position 0,
     * this routine computes the hash of the block and checks that the hash has
     * the requisite number of leftmost 0's (proof of work) as specified in the
     * difficulty field.
     * @return
     */
    protected boolean isChainValid() {
        int size = getChainSize();
        if(size == 0){
            return true;
        }

        Block block;
        String previousHash = null;
        String hashPointer;

        for(int i = 0; i < size; i++){
            block = blocks.get(i);
            // verify proof of work
            String prefix = getProofOfWorkPrefix(block.getDifficulty());
            String proof = block.getIndex() + block.getTimestamp().toString() +
                    block.getData() + block.getPreviousHash() + block.getNonce()
                    + block.getDifficulty();
            String computedHash = "";           
            try{
                MessageDigest digester = MessageDigest.getInstance("SHA-256");
                digester.update(proof.getBytes());
                byte[] digestedMsg = digester.digest();                
                computedHash = DatatypeConverter.printHexBinary(digestedMsg);
            }catch(NoSuchAlgorithmException e){
                e.printStackTrace();
            }
            if(!computedHash.startsWith(prefix)){
                System.out.println("..Improper hash on node " + block.getIndex() +
                            " Does not begin with " + prefix);
                return false;
            }
            // verify hash pointer
            if(size == 1) {
                return computedHash.equals(mostRecentAddedHash);
            }
            // genesis block doesnt have previous hash
            if(block.getIndex() == 0) {
                previousHash = block.getCurrentHash();
            } else {
                hashPointer = block.getPreviousHash();
                if(!hashPointer.equals(previousHash)){                    
                    return false;
                }
                previousHash = block.getCurrentHash();
            }
        }
        return true;
    }
    
    /*
     * Helper method. calculete hash value of a given message string.
     * @param message
     * @return
     * @throws NoSuchAlgorithmException
     */
    private String calculateHash(String message) {
        try {
            MessageDigest digester = MessageDigest.getInstance("SHA-256");
            digester.update(message.getBytes());
            byte[] digestedMsg = digester.digest();
            return DatatypeConverter.printHexBinary(digestedMsg);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(BlockChainService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /*
     * Helper method to get verification prefix.
     * @param difficulty
     * @return
     */
    private String getProofOfWorkPrefix(int difficulty){
        StringBuilder prefix = new StringBuilder();
        for(int i = 0; i < difficulty; i++){
            prefix.append("0");
        }
        if(prefix.length() == 0){
            return null;
        }
        return prefix.toString();
    }
    
    /* 
     * Verify client identity with its RSA public key.
     * @param twoParts
     * @return 
     */
    private boolean verifyClient(String[] twoParts){                
        // decrypt payload using public key
        String encryptedPayload = twoParts[1];
        BigInteger crypoR = new BigInteger(encryptedPayload);        
        BigInteger desClear = decryptWithPubKey(crypoR);
        String clearR = new String(desClear.toByteArray());
        // compare the decrypted payload with the unencrypted raw payload
        String plainPayload = twoParts[0];                 
        String hex2 = calculateHash(plainPayload);                
        return hex2.equals(clearR);
    }
    
    /**
     * This method returns a string of the whole chain.
     * @return
     */
    @Override
    public String toString(){
        ArrayList<String> values = new ArrayList<>();
        blocks.stream().forEach(e -> values.add(e.toString()));        
        String res = "{\"ds_chain\":[" + String.join(",", values) + 
                "],\"chainHash\":\"" + mostRecentAddedHash + "\"}";              
        return res;
    }
    
    /**
     * the size of the chain in blocks.
     * @return
     */
    protected int getChainSize(){
        return blocks.size();
    }
    
    /**
     * the current system time.
     * @return
     */
    protected Timestamp getTime(){
        return new Timestamp(System.currentTimeMillis());
    }
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        if(path.equals("/Project3Task3Server/BlockChainService/verifyChain")){            
            writer.println("" + isChainValid());                                 
        }
        if(path.equals("/Project3Task3Server/BlockChainService/viewBlocks")){
            writer.println(toString());           
        }
        writer.close();   
        response.setStatus(200);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String[] twoParts = request.getParameter("message").split("#");
        // verify client identity
        boolean isValidClient = verifyClient(twoParts);
        if(isValidClient){
            String data = twoParts[0].split(",")[0];
            int difficulty = Integer.parseInt(twoParts[0].split(",")[1]);
            addNewBlock(new Block(getChainSize(), getTime(), data, difficulty));
        }        
        response.setStatus(200);
    }
}
