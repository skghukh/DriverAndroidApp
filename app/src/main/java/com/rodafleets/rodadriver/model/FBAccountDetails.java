package com.rodafleets.rodadriver.model;

/**
 * Created by sverma4 on 1/7/18.
 */

public class FBAccountDetails {

    private String owner;
    private String bank;
    private String branch;
    private String accountNumber;
    private String ifscCode;

    public FBAccountDetails() {
        //default constructor
    }

    public FBAccountDetails(String owner, String bank, String branch, String accNumber, String ifscCode) {
        this.owner = owner;
        this.bank = bank;
        this.branch = branch;
        this.accountNumber = accNumber;
        this.ifscCode = ifscCode;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }
}
