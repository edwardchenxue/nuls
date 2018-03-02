/**
 * MIT License
 *
 * Copyright (c) 2017-2018 nuls.io
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.ledger.entity;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.NulsSignData;
import io.nuls.core.chain.entity.Transaction;
import io.nuls.core.crypto.VarInt;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.crypto.Utils;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;
import io.nuls.core.utils.str.StringUtils;

import java.io.IOException;

/**
 * @author win10
 * @date 2017/10/30
 */
public class UtxoInput extends BaseNulsData {

    private NulsDigestData txHash;

    private int index;

    private NulsDigestData fromHash;

    private int fromIndex;

    private NulsSignData sign;

    private UtxoOutput from;

    private Transaction parent;

    // key = fromHash + "-" + fromIndex, a key that will not be serialized, only used for caching
    private String key;

    public UtxoInput() {

    }

    public UtxoInput(NulsDigestData txHash) {
        this();
        this.txHash = txHash;
    }

    public UtxoInput(NulsDigestData txHash, UtxoOutput output) {
        this();
        this.txHash = txHash;
        this.from = output;
    }

    public UtxoInput(NulsDigestData txHash, UtxoOutput from, Transaction parent) {
        this(txHash, from);
        this.parent = parent;
    }

    @Override
    public int size() {
        int size = 0;
        size += Utils.sizeOfSerialize(txHash);
        size += VarInt.sizeOf(index);
        size += VarInt.sizeOf(fromIndex);
        size += Utils.sizeOfSerialize(sign);
        return size;
    }

    @Override
    public void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeNulsData(txHash);
        stream.writeVarInt(index);
        stream.writeVarInt(fromIndex);
        stream.writeNulsData(sign);
    }

    @Override
    public void parse(NulsByteBuffer byteBuffer) throws NulsException {
        txHash = byteBuffer.readHash();
        index = (int) byteBuffer.readVarInt();
        fromIndex = (int) byteBuffer.readVarInt();
        sign = byteBuffer.readSign();
    }

    public NulsDigestData getTxHash() {
        if (txHash == null && parent != null) {
            this.txHash = parent.getHash();
        }
        return txHash;
    }

    public void setTxHash(NulsDigestData txHash) {
        this.txHash = txHash;
    }

    public NulsSignData getSign() {
        return sign;
    }

    public void setSign(NulsSignData sign) {
        this.sign = sign;
    }

    public UtxoOutput getFrom() {
        return from;
    }

    public void setFrom(UtxoOutput from) {
        this.from = from;
    }

    public Transaction getParent() {
        return parent;
    }

    public void setParent(Transaction parent) {
        this.parent = parent;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(int fromIndex) {
        this.fromIndex = fromIndex;
    }

    public String getKey() {
        if (StringUtils.isBlank(key)) {
            key = fromHash.getDigestHex() + "-" + fromIndex;
        }
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public NulsDigestData getFromHash() {
        return fromHash;
    }

    public void setFromHash(NulsDigestData fromHash) {
        this.fromHash = fromHash;
    }
}
