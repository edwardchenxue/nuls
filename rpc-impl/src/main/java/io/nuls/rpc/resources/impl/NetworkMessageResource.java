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
package io.nuls.rpc.resources.impl;

import io.nuls.core.context.NulsContext;
import io.nuls.core.event.CommonStringEvent;
import io.nuls.core.utils.date.TimeService;
import io.nuls.event.bus.service.intf.EventBroadcaster;
import io.nuls.rpc.entity.InfoDto;
import io.nuls.rpc.entity.RpcResult;

import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Niels
 * @date 2017/10/24
 */
@Path("/network")
public class NetworkMessageResource {

    private EventBroadcaster eventBroadcaster = NulsContext.getServiceBean(EventBroadcaster.class);


    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public RpcResult broadcast(String message) {
        CommonStringEvent event = new CommonStringEvent();
        event.setMessage(message);
        eventBroadcaster.broadcastAndCacheAysn(event, false);
        return RpcResult.getSuccess();
    }


    public RpcResult send(String message, String nodeId) {
        CommonStringEvent event = new CommonStringEvent();
        event.setMessage(message);
        eventBroadcaster.sendToNodeAysn(event, nodeId);
        return RpcResult.getSuccess();
    }


    public RpcResult broadcast(String message, String groupId) {
        CommonStringEvent event = new CommonStringEvent();
        event.setMessage(message);
        eventBroadcaster.sendToGroupAysn(event, groupId);
        return RpcResult.getSuccess();
    }

    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public RpcResult getInfo() {
        RpcResult result = RpcResult.getSuccess();
        InfoDto info = new InfoDto(NulsContext.getInstance().getBestBlock().getHeader().getHeight(), NulsContext.getInstance().getNetBestBlockHeight(), TimeService.getNetTimeOffset());
        result.setData(info);
        return result;
    }
}
