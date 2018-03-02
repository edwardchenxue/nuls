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
package io.nuls.consensus.entity.block;

import io.nuls.core.chain.entity.BlockHeader;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.context.NulsContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Niels
 * @date 2018/1/12
 */
public class BifurcateProcessor {

    private static final BifurcateProcessor INSTANCE = new BifurcateProcessor();

    private List<BlockHeaderChain> chainList = new ArrayList<>();

    private long bestHeight;

    private BifurcateProcessor() {
    }

    public static BifurcateProcessor getInstance() {
        return INSTANCE;
    }

    public boolean addHeader(BlockHeader header) {
        for (BlockHeaderChain chain : chainList) {
            if (chain.contains(header)) {
                return false;
            }
        }
        for (BlockHeaderChain chain : chainList) {
            int index = chain.indexOf(header.getPreHash().getDigestHex(), header.getHeight() - 1);
            if (index == chain.size() - 1) {
                chain.addHeader(header);
                setBestHeight(header);
                return true;
            } else if (index >= 0) {
                BlockHeaderChain newChain = chain.getBifurcateChain(header);
                chainList.add(newChain);
                setBestHeight(header);
                return true;
            }
        }
        if ((bestHeight + 1) != header.getHeight()) {
            return false;
        }
        BlockHeaderChain chain = new BlockHeaderChain();
        chain.addHeader(header);
        chainList.add(chain);
        setBestHeight(header);
        return true;
    }

    private void setBestHeight(BlockHeader header) {
        if (header.getHeight() <= bestHeight) {
            return;
        }
        bestHeight = header.getHeight();
    }

    public BlockHeaderChain getLongestChain() {
        List<BlockHeaderChain> longestChainList = new ArrayList<>();
        for (BlockHeaderChain chain : chainList) {
            if (longestChainList.isEmpty() || chain.size() > longestChainList.get(0).size()) {
                longestChainList.clear();
                longestChainList.add(chain);
            } else if (longestChainList.isEmpty() || chain.size() == longestChainList.get(0).size()) {
                longestChainList.add(chain);
            }
        }
        if (longestChainList.size() > 1 || longestChainList.isEmpty()) {
            return null;
        }
        return longestChainList.get(0);
    }


    public long getBestHeight() {
        if (bestHeight == 0 && null != NulsContext.getInstance().getBestBlock()) {
            bestHeight = NulsContext.getInstance().getBestBlock().getHeader().getHeight();
        }
        return bestHeight;
    }

    public void removeHeight(long height) {
        if (chainList.isEmpty()) {
            return;
        }
        List<BlockHeaderChain> tempList = new ArrayList<>(this.chainList);
        tempList.forEach((BlockHeaderChain chain) -> removeBlock(chain, height));

    }

    private void removeBlock(BlockHeaderChain chain, long height) {
        HeaderDigest hd = chain.getHeaderDigest(height);
        if (hd == null) {
            return;
        }
        chain.removeHeaderDigest(height);
        if (chain.size() == 0) {
            this.chainList.remove(chain);
        }
    }

    public List<String> getHashList(long height) {
        Set<String> set = new HashSet<>();
        for (BlockHeaderChain chain : this.chainList) {
            HeaderDigest headerDigest = chain.getHeaderDigest(height);
            if (null != headerDigest) {
                set.add(headerDigest.getHash());
            }
        }
        return new ArrayList<>(set);
    }
}
