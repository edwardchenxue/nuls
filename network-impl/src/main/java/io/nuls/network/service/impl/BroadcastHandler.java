/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.network.service.impl;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.SocketChannel;
import io.nuls.core.event.BaseEvent;
import io.nuls.core.mesasge.NulsMessage;
import io.nuls.network.entity.BroadcastResult;
import io.nuls.network.entity.Node;
import io.nuls.network.entity.NodeGroup;
import io.nuls.network.entity.param.AbstractNetworkParam;
import io.nuls.network.service.impl.netty.NioChannelMap;

import java.io.IOException;
import java.util.Map;

/**
 * @author vivi
 * @date 2017/11/29.
 */
public class BroadcastHandler {

    private NodesManager nodesManager;

    private AbstractNetworkParam network;

    private static BroadcastHandler instance = new BroadcastHandler();

    private BroadcastHandler() {
    }

    public static BroadcastHandler getInstance() {
        return instance;
    }

    public BroadcastResult broadcast(BaseEvent event, boolean asyn) {
        if (nodesManager.getNodes().isEmpty()) {
            return new BroadcastResult(false, "no node can be broadcast");
        }
        return broadcastToList(nodesManager.getNodes(), event, null, asyn);
    }

    public BroadcastResult broadcast(BaseEvent event, String excludeNodeId, boolean asyn) {
        if (nodesManager.getNodes().isEmpty()) {
            return new BroadcastResult(false, "no node can be broadcast");
        }
        return broadcastToList(nodesManager.getNodes(), event, excludeNodeId, asyn);
    }

    public BroadcastResult broadcastToGroup(BaseEvent event, String groupName, boolean asyn) {
        NodeGroup group = nodesManager.getNodeGroup(groupName);
        if (group == null) {
            return new BroadcastResult(false, "NodeGroup not found");
        }
        if (group.size() == 0) {
            return new BroadcastResult(false, "no node can be broadcast");
        }

        return broadcastToList(group.getNodes(), event, null, asyn);
    }

    public BroadcastResult broadcastToGroup(BaseEvent event, String groupName, String excludeNodeId, boolean asyn) {
        NodeGroup group = nodesManager.getNodeGroup(groupName);
        if (group == null) {
            return new BroadcastResult(false, "NodeGroup not found");
        }
        if (group.size() == 0) {
            return new BroadcastResult(false, "no node can be broadcast");
        }

        return broadcastToList(group.getNodes(), event, excludeNodeId, asyn);
    }

    public BroadcastResult broadcastToNode(BaseEvent event, String nodeId, boolean asyn) {
        try {
            NulsMessage message = new NulsMessage(network.packetMagic(), event.serialize());
            Node node = nodesManager.getNode(nodeId);
            if (node == null) {
                return new BroadcastResult(false, "node not found");
            }
            return broadcast(message, node, asyn);
        } catch (IOException e) {
            return new BroadcastResult(false, "event.serialize() error");
        }
    }

    public BroadcastResult broadcastToNode(BaseEvent event, Node node, boolean asyn) {
        try {
            NulsMessage message = new NulsMessage(network.packetMagic(), event.serialize());
            return broadcast(message, node, asyn);
        } catch (IOException e) {
            return new BroadcastResult(false, "event.serialize() error");
        }
    }

//
//    /**
//     * At least maxOutCount() Nodes should be broadcast success
//     *
//     * @param message
//     * @param excludeNodeId
//     * @return
//     */
//    private BroadcastResult broadcast(NulsMessage message, String excludeNodeId) {
////        List<Node> broadNodes = nodesManager.getAvailableNodes(excludeNodeId);
////        //todo only one node connected can't send message
////        if (broadNodes.size() < NetworkConstant.NETWORK_BROAD_SUCCESS_MIN_COUNT) {
////            return new BroadcastResult(false, "no node can be broadcast");
////        }
////
////        int successCount = 0;
////        int count = 0;
////        BroadcastResult result = new BroadcastResult(true, "OK");
////        List<Node> successNodes = new ArrayList<>();
////
////        while (successCount < network.maxOutCount() && count < NetworkConstant.NETWORK_BROAD_MAX_TRY_COUNT) {
////            for (Node node : broadNodes) {
////                if (successNodes.contains(node)) {
////                    continue;
////                }
////                try {
////                    node.sendMessage(message);
////                    successNodes.add(node);
////                    successCount++;
////                } catch (NotYetConnectedException | IOException e) {
////                    Log.warn("broadcast message error ， maybe the node closed ! node ip :{}, {}", node.getIp(), e.getMessage());
////                }
////            }
////            if (successCount < network.maxOutCount()) {
////                try {
////                    Thread.sleep(3000);
////                } catch (InterruptedException e) {
////                    e.printStackTrace();
////                }
////                broadNodes = nodesManager.getAvailableNodes(excludeNodeId);
////            }
////            count++;
////        }
////
////        Log.debug("成功广播给{}个节点，消息{}", successCount, message);
////        result.setBroadcastNodes(successNodes);
////        return result;
//        return null;
//    }
//
//    /**
//     * Broadcast half of the nodes, waiting for the other half to reply
//     *
//     * @param message
//     * @return
//     */
//    private BroadcastResult broadcastSync(NulsMessage message, String excludeNodeId) {
////        List<Node> broadNodes = nodesManager.getAvailableNodes(excludeNodeId);
////
////        if (broadNodes.size() <= 0) {
////            return new BroadcastResult(false, "no node can be broadcast");
////        }
////
////        int numConnected = broadNodes.size();
////        int numToBroadcastTo = (int) Math.max(1, Math.round(Math.ceil(broadNodes.size() / 2.0)));
////        Collections.shuffle(broadNodes);
////        broadNodes = broadNodes.subList(0, numToBroadcastTo);
////
////        int successCount = 0;
////        for (Node node : broadNodes) {
////            try {
////                node.sendMessage(message);
////                successCount++;
////            } catch (IOException e) {
////                broadNodes.remove(node);
////                Log.warn("broadcast message error ， maybe the node closed ! node ip :{}, {}", node.getIp(), e.getMessage());
////            }
////        }
////        if (successCount == 0) {
////            return new BroadcastResult(false, "broadcast fail");
////        }
////
////        BroadcastResult result = new BroadcastResult(true, "OK");
////        result.setHash(Sha256Hash.twiceOf(message.getData()).toString());
////        result.setBroadcastNodes(broadNodes);
////        result.setWaitReplyCount(numConnected - numToBroadcastTo);
////        NetworkCacheService.getInstance().addBroadCastResult(result);
//
//        return null;
//    }
//
//
//    private BroadcastResult broadcastToNode(NulsMessage message, String nodeId) {
////        Node node = nodesManager.getNode(nodeId);
////        if (node == null || node.getStatus() != Node.HANDSHAKE) {
////            return new BroadcastResult(false, "no node can be broadcast");
////        }
////        try {
////            node.sendMessage(message);
////        } catch (NotYetConnectedException | IOException e) {
////            Log.warn("broadcast message error ， maybe the node closed ! node ip :{}, {}", node.getIp(), e.getMessage());
////            return new BroadcastResult(false, "broadcast fail");
////        }
////        List<Node> broadNodes = new ArrayList<>();
////        broadNodes.add(node);
////        return new BroadcastResult(true, "OK", broadNodes);
//        return null;
//    }
//
//    private BroadcastResult broadcastToGroup(NulsMessage message,String areaName, String groupName, String excludeNodeId) {
////        List<Node> broadNodes = nodesManager.getGroupAvailableNodes(areaName, groupName, excludeNodeId);
////        if (broadNodes.size() <= 1) {
////            return new BroadcastResult(false, "no node can be broadcast");
////        }
////
////        int successCount = 0;
////        for (Node node : broadNodes) {
////            try {
////                node.sendMessage(message);
////                successCount++;
////            } catch (NotYetConnectedException | IOException e) {
////                Log.warn("broadcast message error ， maybe the node closed ! node ip :{}, {}", node.getIp(), e.getMessage());
////            }
////        }
////
////        if (successCount == 0) {
////            new BroadcastResult(false, "broadcast fail", broadNodes);
////        }
////        Log.debug("成功广播给{}个节点，消息{}", successCount, message);
//        return new BroadcastResult(true, "OK");
//    }
//
//    private BroadcastResult broadcastToGroup(NulsMessage message, String groupName, String excludeNodeId) {
//        return  broadcastToGroup(message,null,groupName,excludeNodeId);
//    }
//
//
//    private BroadcastResult broadcastToGroupSync(NulsMessage message, String groupName, String excludeNodeId) {
//
//        return null;
//    }
//
//
//    @Override
//    public BroadcastResult broadcast(BaseEvent event) {
//        return broadcast(event, null);
//    }
//
//    @Override
//    public BroadcastResult broadcast(BaseEvent event, String excludeNodeId) {
//        NulsMessage message = null;
//        try {
//            message = new NulsMessage(network.packetMagic(), event.serialize());
//        } catch (IOException e) {
//            return new BroadcastResult(false, "event.serialize() error");
//        }
//
//        return broadcast(message, excludeNodeId);
//    }
//
//
//
//
//
//    public BroadcastResult broadcastSync(BaseEvent event) {
//        return broadcastSync(event, null);
//    }
//
//    public BroadcastResult broadcastSync(BaseEvent event, String excludeNodeId) {
//        NulsMessage message = null;
//        try {
//            message = new NulsMessage(network.packetMagic(), event.serialize());
//        } catch (IOException e) {
//            return new BroadcastResult(false, "event.serialize() error");
//        }
//
//        return broadcastSync(message, excludeNodeId);
//    }
//
//    public BroadcastResult broadcastSync(byte[] data) {
//        return broadcastSync(data, null);
//    }
//
//    public BroadcastResult broadcastSync(byte[] data, String excludeNodeId) {
//        NulsMessage message = new NulsMessage(network.packetMagic(), data);
//        return broadcastSync(message, excludeNodeId);
//    }
//
//    @Override
//    public BroadcastResult broadcastToNode(BaseEvent event, String nodeId) {
//        NulsMessage message = null;
//        try {
//            message = new NulsMessage(network.packetMagic(), event.serialize());
//        } catch (IOException e) {
//            return new BroadcastResult(false, "event.serialize() error");
//        }
//        return broadcastToNode(message, nodeId);
//    }
//
//    @Override
//    public BroadcastResult broadcastToNode(BaseEvent event, Node node) {
//        return null;
//    }
//
//    @Override
//    public BroadcastResult broadcastToGroup(BaseEvent event, String groupName) {
//        return broadcastToGroup(event, groupName, null);
//    }
//
//    @Override
//    public BroadcastResult broadcastToGroup(BaseEvent event, String groupName, String excludeNodeId) {
//        return broadcastToGroup(event,null ,groupName, excludeNodeId);
//    }

    private BroadcastResult broadcastToList(Map<String, Node> nodeMap, BaseEvent event, String excludeNodeId, boolean asyn) {
        NulsMessage message;
        try {
            message = new NulsMessage(network.packetMagic(), event.serialize());
            int successCount = 0;
            for (Node node : nodeMap.values()) {
                if (excludeNodeId != null && node.getId().equals(excludeNodeId)) {
                    continue;
                }
                BroadcastResult result = broadcast(message, node, asyn);
                if (result.isSuccess()) {
                    successCount++;
                }
            }
            if (successCount == 0) {
                return new BroadcastResult(false, "send message failed");
            }
        } catch (IOException e) {
            return new BroadcastResult(false, "event.serialize() error");
        }
        return new BroadcastResult(true, "OK");
    }

    private BroadcastResult broadcast(NulsMessage message, Node node, boolean asyn) throws IOException {
        try {
            SocketChannel channel = NioChannelMap.get(node.getChannelId());
            if (channel == null) {
                return new BroadcastResult(false, "node not found");
            }
            ChannelFuture future = channel.writeAndFlush(Unpooled.wrappedBuffer(message.serialize()));
            if (!asyn) {
                boolean success = future.isSuccess();
                if (!success) {
                    return new BroadcastResult(false, "send message failed");
                }
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            return new BroadcastResult(false, "send message failed");
        }
        return new BroadcastResult(true, "OK");
    }

    public void setNetwork(AbstractNetworkParam network) {
        this.network = network;
    }

    public void setNodesManager(NodesManager nodesManager) {
        this.nodesManager = nodesManager;
    }
}
