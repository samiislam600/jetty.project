/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.jetty.spdy.generator;

import java.nio.ByteBuffer;

import org.eclipse.jetty.spdy.ByteBufferPool;
import org.eclipse.jetty.spdy.api.SPDY;
import org.eclipse.jetty.spdy.frames.ControlFrame;
import org.eclipse.jetty.spdy.frames.GoAwayFrame;

public class GoAwayGenerator extends ControlFrameGenerator
{
    public GoAwayGenerator(ByteBufferPool bufferPool)
    {
        super(bufferPool);
    }

    @Override
    public ByteBuffer generate(ControlFrame frame)
    {
        GoAwayFrame goAway = (GoAwayFrame)frame;

        int frameBodyLength = 8;
        int totalLength = ControlFrame.HEADER_LENGTH + frameBodyLength;
        ByteBuffer buffer = getByteBufferPool().acquire(totalLength, true);
        generateControlFrameHeader(goAway, frameBodyLength, buffer);

        buffer.putInt(goAway.getLastStreamId() & 0x7F_FF_FF_FF);
        writeStatusCode(goAway, buffer);

        buffer.flip();
        return buffer;
    }

    private void writeStatusCode(GoAwayFrame goAway, ByteBuffer buffer)
    {
        switch (goAway.getVersion())
        {
            case SPDY.V2:
                break;
            case SPDY.V3:
                buffer.putInt(goAway.getStatusCode());
                break;
            default:
                throw new IllegalStateException();
        }
    }
}
