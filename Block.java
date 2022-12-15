import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

    private int index;
    private LocalDate time;
    private ArrayList<Transaction> transactions;
    private String prevHash = "";
    private String hash;
    private int nonce;

    public Block(LocalDate time, ArrayList<Transaction> transactions, String prevHash) throws NoSuchAlgorithmException {
        this.time = time;
        this.transactions = transactions;
        this.prevHash = prevHash;
        this.hash = this.calculateHash();
        this.nonce = 0;
    }

    protected String calculateHash() throws NoSuchAlgorithmException {
        return createSHA(this.prevHash + this.time + this.transactions + this.nonce);
    }

    protected void mineBlock(int difficulty) throws NoSuchAlgorithmException {
        String prefixString = new String(new char[difficulty]).replace('\0', '0');

        while(!this.hash.substring(0, difficulty).equals(prefixString)){
            this.nonce++;
            this.hash = this.calculateHash();
        }

        System.out.println("BLOCK MINED: " + this.hash);
    }

    protected boolean hasValidTransactions() throws InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {
        for(Transaction tx : this.transactions){
           if(!tx.isValid()){
               return false;
           }
        }
        return true;
    }

    private String createSHA(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));


        StringBuffer buffer = new StringBuffer();
        for(byte b : messageDigest){
            buffer.append(String.format("%02x", b));
        }
        return buffer.toString();
    }

    public String getPrevHash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public List<Transaction> getTransactions(){
        return List.copyOf(transactions);
    }

    public String toString(){
        return "{ \n" + "'index'" + ": " + index + ",\n'time'" + ": " + time +
                ",\n'data'" + ": " + transactions + ",\n'prevHash" + ": " + prevHash
                + ",\n'hash'" + ": " + hash + "\n},";
    }
}
