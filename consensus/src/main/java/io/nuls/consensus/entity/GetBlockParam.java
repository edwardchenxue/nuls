/*
 *
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
 *
 */
package io.nuls.consensus.entity;

import io.nuls.core.chain.entity.BaseNulsData;
import io.nuls.core.chain.entity.NulsDigestData;
import io.nuls.core.constant.NulsConstant;
import io.nuls.core.exception.NulsException;
import io.nuls.core.utils.crypto.Utils;
import io.nuls.core.utils.date.TimeService;
import io.nuls.core.utils.io.NulsByteBuffer;
import io.nuls.core.utils.io.NulsOutputStreamBuffer;

import java.io.IOException;

/**
 * @author Niels
 * @date 2017/12/18
 */
public class GetBlockParam extends BaseNulsData {

    private long time;
    private long start;
    private long end;

    public GetBlockParam() {
        this.time = TimeService.currentTimeMillis();
    }

    @Override
    public int size() {
        int size = 0;
        size += NulsConstant.TIME_VALUE_LENGTH;
        size += Utils.sizeOfSerialize(start);
        size += Utils.sizeOfSerialize(end);
        return size;
    }

    @Override
    protected void serializeToStream(NulsOutputStreamBuffer stream) throws IOException {
        stream.writeTime(time);
        stream.writeVarInt(start);
        stream.writeVarInt(end);
    }

    @Override
    protected void parse(NulsByteBuffer byteBuffer) throws NulsException {
        this.time = byteBuffer.readTime();
        this.start = byteBuffer.readVarInt();
        this.end = byteBuffer.readVarInt();
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }
}
