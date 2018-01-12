package io.nuls.consensus.event.handler;

import io.nuls.consensus.cache.manager.block.BlockCacheManager;
import io.nuls.consensus.event.BlockHeaderEvent;
import io.nuls.consensus.event.GetSmallBlockEvent;
import io.nuls.consensus.service.intf.BlockService;
import io.nuls.consensus.utils.DistributedBlockInfoRequestUtils;
import io.nuls.core.chain.entity.BasicTypeData;
import io.nuls.core.chain.entity.BlockHeader;
import io.nuls.core.constant.SeverityLevelEnum;
import io.nuls.core.context.NulsContext;
import io.nuls.core.validate.ValidateResult;
import io.nuls.event.bus.handler.AbstractEventHandler;
import io.nuls.event.bus.service.intf.EventBroadcaster;
import io.nuls.network.service.NetworkService;

/**
 * @author facjas
 * @date 2017/11/16
 */
public class BlockHeaderHandler extends AbstractEventHandler<BlockHeaderEvent> {

    private BlockCacheManager blockCacheManager = BlockCacheManager.getInstance();

    private NetworkService networkService = NulsContext.getInstance().getService(NetworkService.class);

    @Override
    public void onEvent(BlockHeaderEvent event, String fromId) {
        if (DistributedBlockInfoRequestUtils.getInstance().addBlockHeader(fromId, event.getEventBody())) {
            return;
        }
        BlockHeader header = event.getEventBody();
         blockCacheManager.cacheBlockHeader(header,fromId);

    }
}
