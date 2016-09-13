/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.jackrabbit.oak.segment.standby.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.jackrabbit.oak.segment.RecordId;
import org.apache.jackrabbit.oak.segment.standby.codec.GetHeadRequest;
import org.apache.jackrabbit.oak.segment.standby.codec.GetHeadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handles 'get head' requests and produces 'get head' responses. A response is
 * generated only iff the record ID of the head root state can be read.
 */
class GetHeadRequestHandler extends SimpleChannelInboundHandler<GetHeadRequest> {

    private static final Logger log = LoggerFactory.getLogger(GetHeadRequest.class);

    private final StandbyHeadReader reader;

    GetHeadRequestHandler(StandbyHeadReader reader) {
        this.reader = reader;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GetHeadRequest msg) throws Exception {
        log.debug("Reading head for client {}", msg.getClientId());

        RecordId id = reader.readHeadRecordId();

        if (id == null) {
            log.debug("Head not found, discarding request from client {}", msg.getClientId());
            return;
        }

        ctx.writeAndFlush(new GetHeadResponse(msg.getClientId(), id));
    }

}