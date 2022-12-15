import java.io.UnsupportedEncodingException;
import java.security.*;
import java.time.LocalDate;
import java.util.*;

public class BlockChain {

    private List<Block> chain = new ArrayList<Block>();
    private final int difficulty;
    private ArrayList<Transaction> pendingTransactions;
    private int miningReward;

    public BlockChain() throws NoSuchAlgorithmException {
        Block genesisBlock = this.createGenesisBlock();
        this.chain.add(genesisBlock);
        this.difficulty = 2;
        this.pendingTransactions = new ArrayList<>();
        this.miningReward = 100;
    }

    private Block createGenesisBlock() throws NoSuchAlgorithmException {
        Transaction genesis = new Transaction("genesis", "genesis", 0);
        ArrayList<Transaction> genesisList = new ArrayList<>();
        genesisList.add(genesis);
        return new Block(LocalDate.of(22,12,07), genesisList, "0000");
    }

    private Block getLatestBlock(){
        return this.chain.get(this.chain.size() - 1);
    }

    /*
    protected void addBlock(Block newBlock) throws NoSuchAlgorithmException {
        newBlock.setPrevHash(this.getLatestBlock().getHash());
        newBlock.mineBlock(difficulty);
        this.chain.add(newBlock);
    }

     */

    protected boolean isChainValid() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException, UnsupportedEncodingException, SignatureException, InvalidKeyException, NoSuchProviderException {
        for(int i = 1; i < chain.size(); i++){
            Block current = this.chain.get(i);
            Block previous = this.chain.get(i - 1);

            if(!current.hasValidTransactions()){
                return false;
            }

            if(!current.getHash().equals(current.calculateHash())){
                return false;
            }

            if(!current.getPrevHash().equals(previous.calculateHash())){
                return false;
            }
        }
        return true;
    }

    //if miner calls this method, it will pass along its address so if successful,
    //transfer reward to wallet
    protected void minePendingTransactions(String miningRewardAddress) throws NoSuchAlgorithmException {
        Transaction rewardTx = new Transaction(null, miningRewardAddress, this.miningReward);
        this.pendingTransactions.add(rewardTx);

        Block block = new Block(LocalDate.now(), this.pendingTransactions, this.getLatestBlock().getHash());
        block.mineBlock(this.difficulty);

        System.out.println("Block successfully mined");
        this.chain.add(block);
    }

    protected void addTransaction(Transaction t) throws InvalidAlgorithmParameterException, UnsupportedEncodingException, NoSuchAlgorithmException, SignatureException, InvalidKeyException, NoSuchProviderException {

        if(t.getFrom() == null || t.getTo() == null){
            throw new Error("Transaction must include from and to address");
        }

        if(!t.isValid()){
            throw new Error("Cannot add invalid transaction to chain");
        }

        this.pendingTransactions.add(t);
    }

    public int getBalance(String address){
        int balance = 0;

        for(Block block : this.chain){
            for(Transaction trans : block.getTransactions()){
                if(Objects.equals(trans.getFrom(), address)){
                    balance -= trans.getAmount();
                }

                if(Objects.equals(trans.getTo(), address)){
                    balance += trans.getAmount();
                }
            }
        }

        return balance;
    }

    public List<Block> getList(){
        return List.copyOf(chain);
    }
}
