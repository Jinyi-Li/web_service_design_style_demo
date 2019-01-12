package project3task2server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import javax.xml.bind.DatatypeConverter;

import org.json.*;


/**
 * This class represents a simple Block in a blockchain.
 * Each Block object contains necessary information of index, timestamp, data,
 * previousHash, nonce and difficulty.
 */
public class Block {
    /* Private member fields of a Block object. */
    private int index;
    private Timestamp timestamp;
    private String data;
    private String previousHash;
    private BigInteger nonce;
    private int difficulty;
    private String currentHash;

    /**
     * Constructor of Block.
     */
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        nonce = new BigInteger("0");
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    /**
     * Set block index.
     *
     * @param index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Set block timestamp.
     *
     * @param timestamp
     */
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Set block data.
     *
     * @param data
     */
    public void setData(String data) {
        this.data = data;
    }

    /**
     * Set hash value that the previous block contains.
     *
     * @param previousHash
     */
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    /**
     * Set block difficulty.
     *
     * @param difficulty
     */
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Get block difficulty.
     * @return difficulty
     */
    public int getDifficulty() {
        return difficulty;
    }

    /**
     * Get block index.
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Get block nonce.
     * @return nonce
     */
    public BigInteger getNonce() {
        return nonce;
    }

    /**
     * Get block timestamp
     * @return timestamp
     */
    public Timestamp getTimestamp() {
        return timestamp;
    }

    /**
     * Get block data.
     * @return data
     */
    public String getData() {
        return data;
    }

    /**
     * Get hash value that the previous block contains.
     * @return hash value in hex format
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Get hash value of the current block.
     * @return hash value in hex format
     */
    public String getCurrentHash() {
        return currentHash;
    }

    /**
     * This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
     *
     * @return hash value in hex format.
     * @throws NoSuchAlgorithmException if SHA-256 not found.
     */
    public String calculateHash() throws NoSuchAlgorithmException {
        MessageDigest digester = MessageDigest.getInstance("SHA-256");
        String blockContent = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        digester.update(blockContent.getBytes());
        byte[] digestedMsg = digester.digest();
        return DatatypeConverter.printHexBinary(digestedMsg);
    }

    public void setCurrentHash(String currentHash) {
        this.currentHash = currentHash;
    }

    /**
     * The proof of work methods finds a good hash.
     *
     * @return hash value in hex format.
     * @throws NoSuchAlgorithmException
     */
    public String proofOfWork() throws NoSuchAlgorithmException {
        StringBuilder winPatternSB = new StringBuilder();
        String res = "";

        // create winning pattern based on difficulty
        for (int i = 0; i < difficulty; i++) {
            winPatternSB.append("0");
        }
        String winPattern = winPatternSB.toString();

        // find proof-of-work result
        do{
            nonce = nonce.add(BigInteger.ONE);
            res = calculateHash();
        }while( !res.startsWith(winPattern) );
        currentHash = res;
        return res;
    }

    @Override
    public String toString() {
        JSONObject res = new JSONObject();
        res.put("index", index);
        res.put("time stamp", timestamp);
        res.put("Tx", data);
        res.put("PrevHash", (previousHash == null? "": previousHash));
        res.put("nonce", nonce);
        res.put("difficulty", difficulty);

        return res.toString();
    }
}
