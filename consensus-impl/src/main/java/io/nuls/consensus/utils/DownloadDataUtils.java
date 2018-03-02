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
package io.nuls.consensus.utils;

import io.nuls.consensus.cache.manager.block.BlockCacheManager;
import io.nuls.consensus.cache.manager.tx.ReceivedTxCacheManager;
import io.nuls.consensus.entity.GetSmallBlockParam;
import io.nuls.consensus.entity.GetTxGroupParam;
import io.nuls.consensus.event.GetSmallBlockRequest;
import io.nuls.consensus.event.GetTxGroupRequest;
import io.nuls.consensus.thread.DataDownloadThread;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.chain.entity.SmallBlock;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.context.NulsContext;
import io.nuls.core.thread.manager.TaskManager;
import io.nuls.core.utils.str.StringUtils;
import io.nuls.event.bus.service.intf.EventBroadcaster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Niels
 * @date 2018/1/12
 */
public class DownloadDataUtils {

    private static final DownloadDataUtils INSTANCE = new DownloadDataUtils();

    private DownloadDataUtils() {
        TaskManager.createAndRunThread(NulsConstant.MODULE_ID_CONSENSUS, "data-download-consensu", new DataDownloadThread());
    }

    public static DownloadDataUtils getInstance() {
        return INSTANCE;
    }

    private EventBroadcaster eventBroadcaster = NulsContext.getServiceBean(EventBroadcaster.class);
    private ReceivedTxCacheManager txCacheManager = ReceivedTxCacheManager.getInstance();
    private   BlockCacheManager blockCacheManager;

    private final Map<String, Long> smbRequest = new HashMap<>();
    private final Map<String, Long> tgRequest = new HashMap<>();
    private final Map<String, Integer> smbRequestCount = new HashMap<>();
    private final Map<String, Integer> tgRequestCount = new HashMap<>();

    public void requestSmallBlock(NulsDigestData blockHash, String nodeId) {
        GetSmallBlockRequest request = new GetSmallBlockRequest();
        GetSmallBlockParam param = new GetSmallBlockParam();
        param.setBlockHash(blockHash);
        request.setEventBody(param);
        if (StringUtils.isBlank(nodeId)) {
            eventBroadcaster.broadcastAndCache(request, false);
        } else {
            eventBroadcaster.sendToNode(request, nodeId);
        }
        smbRequest.put(blockHash.getDigestHex(), System.currentTimeMillis());
        if (null == smbRequestCount.get(blockHash.getDigestHex())) {
            smbRequestCount.put(blockHash.getDigestHex(), 1);
        } else {
            smbRequestCount.put(blockHash.getDigestHex(), 1 + smbRequestCount.get(blockHash.getDigestHex()));
        }
    }

    public void requestTxGroup(NulsDigestData blockHash, String nodeId) {
        GetTxGroupRequest request = new GetTxGroupRequest();
        GetTxGroupParam data = new GetTxGroupParam();
        data.setBlockHash(blockHash);
        List<NulsDigestData> txHashList = new ArrayList<>();
        if(null==blockCacheManager){
            blockCacheManager  = BlockCacheManager.getInstance();
        }
        SmallBlock smb = blockCacheManager.getSmallBlock(blockHash.getDigestHex());
        for (NulsDigestData txHash : smb.getTxHashList()) {
            boolean exist = txCacheManager.txExist(txHash);
            if (!exist) {
                txHashList.add(txHash);
            }
        }
        data.setTxHashList(txHashList);
        request.setEventBody(data);
        if (StringUtils.isBlank(nodeId)) {
            eventBroadcaster.broadcastAndCache(request, false);
        } else {
            eventBroadcaster.sendToNode(request, nodeId);
        }
        tgRequest.put(blockHash.getDigestHex(), System.currentTimeMillis());
        if (null == tgRequestCount.get(blockHash.getDigestHex())) {
            tgRequestCount.put(blockHash.getDigestHex(), 1);
        } else {
            tgRequestCount.put(blockHash.getDigestHex(), 1 + tgRequestCount.get(blockHash.getDigestHex()));
        }
    }

    public void removeSmallBlock(String blockHash) {
        smbRequest.remove(blockHash);
        smbRequestCount.remove(blockHash);
    }

    public void removeTxGroup(String blockHash) {
        tgRequest.remove(blockHash);
        tgRequestCount.remove(blockHash);
    }

    public void remove(String blockHash) {
        smbRequest.remove(blockHash);
        tgRequest.remove(blockHash);
        smbRequestCount.remove(blockHash);
        tgRequestCount.remove(blockHash);
    }

    public void reRequest() {
        this.reRequestSmallBlock();
        this.reReqesetTxGroup();
    }

    private void reReqesetTxGroup() {
        for (String hash : this.tgRequest.keySet()) {
            Long time = tgRequest.get(hash);
            if (null != time && (System.currentTimeMillis() - time) >= 1000L) {
                this.requestTxGroup(NulsDigestData.fromDigestHex(hash), null);
            }
            if (tgRequestCount.get(hash) >= 10) {
                this.removeTxGroup(hash);
            }
        }

    }

    private void reRequestSmallBlock() {
        for (String hash : this.tgRequest.keySet()) {
            Long time = tgRequest.get(hash);
            if (null != time && (System.currentTimeMillis() - time)  >= 1000L) {
                this.requestSmallBlock(NulsDigestData.fromDigestHex(hash), null);
            }if (smbRequestCount.get(hash) >= 10) {
                this.removeTxGroup(hash);
            }
        }
    }
}
