package io.nuls.contract.vm.program.impl;

import java.math.BigInteger;

public class ProgramInvoke {

    /**
     * 合约地址
     * 创建合约时候需要传入生成的新地址
     */
    private byte[] contractAddress;

    /**
     * 交易发起者地址
     */
    private byte[] sender;

    /**
     * 交易发起者配置的gas价格
     */
    private long price;

    /**
     * 交易发起者提供的gas
     */
    private long gasLimit;

    /**
     * 交易附带的货币量
     */
    private BigInteger value;

    /**
     * 当前块编号
     */
    private long number;

    /**
     * 合约创建时候，传入代码
     */
    private byte[] data;

    /**
     * 调用方法名
     */
    private String methodName;

    /**
     * 调用方法签名
     */
    private String methodDesc;

    /**
     * 调用方法参数
     */
    private String[][] args;

    /**
     * 是否估计Gas
     */
    private boolean estimateGas;

    public byte[] getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(byte[] contractAddress) {
        this.contractAddress = contractAddress;
    }

    public byte[] getSender() {
        return sender;
    }

    public void setSender(byte[] sender) {
        this.sender = sender;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public long getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(long gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public long getNumber() {
        return number;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public String[][] getArgs() {
        return args;
    }

    public void setArgs(String[][] args) {
        this.args = args;
    }

    public boolean isEstimateGas() {
        return estimateGas;
    }

    public void setEstimateGas(boolean estimateGas) {
        this.estimateGas = estimateGas;
    }

}