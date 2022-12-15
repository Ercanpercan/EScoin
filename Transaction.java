import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Arrays;
import java.util.Objects;

public class Transaction {

    private final String from;
    private final String to;
    private int amount;
    private Signature signature;
    private Signature sg;
    byte[] sig;

    private KeyPair signKey;

    public Transaction(String from, String to, int amount){
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    protected void signTransaction(KeyPair signingKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException {
        this.signKey = signingKey;
        if(!Objects.equals(toHex(signingKey.getPublic().getEncoded()), this.from)){
            throw new Error("You cannot sign transactions for other wallets!");
        }

        this.signature = Signature.getInstance("SHA256withECDSA", "SunEC");
        this.signature.initSign(signKey.getPrivate());
        //this.signature.update(this.calculateHash().getBytes(StandardCharsets.UTF_8));
        sig = this.signature.sign();


    }
//todo: Varför säger den invalid transaction??????
    public boolean isValid() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, SignatureException, InvalidAlgorithmParameterException {
        if(this.from == null) return true;

        if(this.signature == null || this.signature.toString().trim().equals("")){
            throw new Error("No signature in this transaction");
        }

        this.sg = Signature.getInstance("SHA256withECDSA","SunEC");
        this.sg.initVerify(signKey.getPublic());
        //sg.update(this.calculateHash().getBytes(StandardCharsets.UTF_8));
        return this.sg.verify(sig);
    }

    private String calculateHash() throws NoSuchAlgorithmException {
        return createSHA(this.from + this.to + this.amount);
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

    private String toHex(byte[] bytes){
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public int getAmount() {
        return amount;
    }
}

/* Fungerar vvv
protected void signTransaction(KeyPair signingKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, SignatureException, UnsupportedEncodingException {

        if(!Objects.equals(toHex(signingKey.getPublic().getEncoded()), this.from)){
            throw new Error("You cannot sign transactions for other wallets!");
        }

        //String hashTx = this.calculateHash();
        this.signature = Signature.getInstance("SHA256withECDSA","SunEC");
    }

    public boolean isValid() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, SignatureException, InvalidAlgorithmParameterException {
        KeyGenerator kg = new KeyGenerator();
        KeyPair signingKey = kg.getKey();
        if(this.from == null) return true;

        if(this.signature == null || this.signature.toString().trim().equals("")){
            System.out.println(this.signature);
            throw new Error("No signature in this transaction");
        }

        this.sg = Signature.getInstance("SHA256withECDSA","SunEC");
        this.signature.initSign(signingKey.getPrivate());
        sg.initVerify(signingKey.getPublic());

        byte[] sig;
        sig = this.signature.sign();

        return sg.verify(sig);
    }
* */
