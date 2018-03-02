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
package io.nuls.consensus.event.handler;

import io.nuls.consensus.event.GetSmallBlockRequest;
import io.nuls.consensus.event.SmallBlockEvent;
import io.nuls.consensus.service.intf.BlockService;
import io.nuls.core.chain.entity.*;
import io.nuls.core.context.NulsContext;
import io.nuls.event.bus.handler.AbstractEventHandler;
import io.nuls.event.bus.service.intf.EventBroadcaster;
import io.nuls.ledger.service.intf.LedgerService;

/**
 * @author facjas
 * @date 2017/11/16
 */
public class GetSmallBlockHandler extends AbstractEventHandler<GetSmallBlockRequest> {

    private EventBroadcaster eventBroadcaster = NulsContext.getServiceBean(EventBroadcaster.class);
    private BlockService blockService = NulsContext.getServiceBean(BlockService.class);

    @Override
    public void onEvent(GetSmallBlockRequest event, String fromId) {
        Block block = blockService.getBlock(event.getEventBody().getBlockHash().getDigestHex());
        SmallBlockEvent smallBlockEvent = new SmallBlockEvent();
        SmallBlock smallBlock = new SmallBlock();
        smallBlock.setBlockHash(block.getHeader().getHash());
        smallBlock.setTxHashList(block.getTxHashList());
        smallBlock.setTxCount(block.getHeader().getTxCount());
        smallBlockEvent.setEventBody(smallBlock);
        eventBroadcaster.sendToNode(smallBlockEvent, fromId);
    }
}
