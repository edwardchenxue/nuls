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
package io.nuls.db.dao.impl.mybatis;

import io.nuls.core.context.NulsContext;
import io.nuls.core.utils.spring.lite.annotation.Autowired;
import io.nuls.db.dao.*;
import io.nuls.db.entity.*;
import io.nuls.db.transactional.annotation.DbSession;
import io.nuls.db.transactional.annotation.PROPAGATION;

import java.util.List;

/**
 * @author vivi
 * @date 2017/12/23.
 */
@DbSession(transactional = PROPAGATION.NONE)
public class UtxoTransactionDaoImpl implements UtxoTransactionDataService {

    @Autowired
    private TransactionDataService txDao;
    @Autowired
    private TransactionLocalDataService txLocalDao;
    @Autowired
    private UtxoInputDataService inputDao;
    @Autowired
    private UtxoOutputDataService outputDao;
    @Autowired
    private BlockHeaderService blockHeaderDao;

    @Override
    public TransactionPo gettx(String hash) {
        return txDao.get(hash);
    }

    @Override
    public List<TransactionPo> getTxs(long blockHeight) {
        return txDao.getTxs(blockHeight);
    }

    @Override
    public List<TransactionPo> getTxs(long startHeight, long endHeight) {
        return txDao.getTxs(startHeight, endHeight);
    }

    @Override
    public List<TransactionPo> getTxs(String blockHash) {
        BlockHeaderPo header = blockHeaderDao.getHeader(blockHash);
        if (header == null) {
            return null;
        }
        return txDao.getTxs(header.getHeight());
    }

    @Override
    public List<TransactionPo> getTxs(String address, int type, Integer start, Integer limit) {
        return txDao.getTxs(address, type, start, limit);
    }

    @Override
    public TransactionLocalPo getLocaltx(String hash) {
        return txLocalDao.get(hash);
    }

    @Override
    public List<TransactionLocalPo> getLocalTxs(long blockHeight) {
        return txLocalDao.getTxs(blockHeight);
    }

    @Override
    public List<TransactionLocalPo> getLocalTxs(long startHeight, long endHeight) {
        return txLocalDao.getTxs(startHeight, endHeight);
    }

    @Override
    public List<TransactionLocalPo> getLocalTxs(String blockHash) {
        BlockHeaderPo header = blockHeaderDao.getHeader(blockHash);
        if (header == null) {
            return null;
        }
        return txLocalDao.getTxs(header.getHeight());
    }

    @Override
    public List<TransactionLocalPo> getLocalTxs(String address, int type, Integer start, Integer limit) {
        return txLocalDao.getTxs(address, type, start, limit);
    }

    @Override
    public List<UtxoInputPo> getTxInputs(String txHash) {
        return inputDao.getTxInputs(txHash);
    }

    @Override
    public List<UtxoOutputPo> getTxOutputs(String txHash) {
        return outputDao.getTxOutputs(txHash);
    }

    @Override
    public List<UtxoOutputPo> getAccountOutputs(String address, byte status) {
        return outputDao.getAccountOutputs(address, status);
    }

    @Override
    public List<UtxoOutputPo> getAccountUnSpend(String address) {
        return null;
    }

    @Override
    @DbSession
    public int save(TransactionPo po) {
        return txDao.save(po);
    }

    @Override
    @DbSession
    public int save(TransactionLocalPo localPo) {
        return txLocalDao.save(localPo);
    }

    @Override
    @DbSession
    public int saveTxList(List<TransactionPo> poList) {
        return txDao.save(poList);
    }

    @Override
    @DbSession
    public int saveLocalList(List<TransactionLocalPo> poList) {
        return txLocalDao.save(poList);
    }

}
