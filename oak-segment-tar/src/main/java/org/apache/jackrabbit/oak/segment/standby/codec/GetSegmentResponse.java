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

package org.apache.jackrabbit.oak.segment.standby.codec;

public class GetSegmentResponse {

    private final String clientId;

    private final String segmentId;

    private final byte[] segmentData;

    public GetSegmentResponse(String clientId, String segmentId, byte[] segmentData) {
        this.clientId = clientId;
        this.segmentId = segmentId;
        this.segmentData = segmentData;
    }

    public String getClientId() {
        return clientId;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public byte[] getSegmentData() {
        return segmentData;
    }

}
