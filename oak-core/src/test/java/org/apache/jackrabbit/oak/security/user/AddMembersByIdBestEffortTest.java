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
package org.apache.jackrabbit.oak.security.user;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.oak.api.CommitFailedException;
import org.apache.jackrabbit.oak.spi.security.ConfigurationParameters;
import org.apache.jackrabbit.oak.spi.security.user.UserConfiguration;
import org.apache.jackrabbit.oak.spi.xml.ImportBehavior;
import org.apache.jackrabbit.oak.spi.xml.ProtectedItemImporter;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AddMembersByIdBestEffortTest extends AbstractAddMembersByIdTest {

    private List<Authorizable> toRemove;

    @Override
    public void after() throws Exception {
        try {
            if (toRemove != null) {
                for (Authorizable a : toRemove) {
                    a.remove();
                }
            }
        } finally {
            super.after();
        }
    }

    @Override
    protected ConfigurationParameters getSecurityConfigParameters() {
        return ConfigurationParameters.of(UserConfiguration.NAME,
                ConfigurationParameters.of(
                        ProtectedItemImporter.PARAM_IMPORT_BEHAVIOR, ImportBehavior.NAME_BESTEFFORT)
        );
    }

    @Test
    public void testNonExistingMember() throws Exception {
        Set<String> failed = addNonExistingMember();
        assertTrue(failed.isEmpty());

        Iterable<String> memberIds = getMemberIds(testGroup);
        Iterables.elementsEqual(ImmutableList.copyOf(NON_EXISTING_IDS), memberIds);

        Iterator<Authorizable> members = testGroup.getDeclaredMembers();
        assertFalse(members.hasNext());

        toRemove = new ArrayList<Authorizable>(NON_EXISTING_IDS.length);
        for (String id : NON_EXISTING_IDS) {
            toRemove.add(getUserManager(root).createGroup(id));
        }
        members = testGroup.getDeclaredMembers();
        assertTrue(members.hasNext());
        for (Authorizable a : toRemove) {
            assertTrue(testGroup.isDeclaredMember(a));
        }
    }

    @Test
    public void testAddByContentID() throws Exception {
        Set<String> failed = testGroup.addMembers(AuthorizableBaseProvider.getContentID(getTestUser().getID()));
        assertTrue(failed.isEmpty());

        assertFalse(testGroup.isMember(getTestUser()));
    }

    @Test
    public void testExistingMemberWithoutAccess() throws Exception {
        Set<String> failed = addExistingMemberWithoutAccess();
        assertTrue(failed.isEmpty());

        root.refresh();
        assertTrue(testGroup.isMember(memberGroup));
    }

    @Test
    public void testCyclicWithoutAccess() throws Exception {
        memberGroup.addMember(testGroup);
        root.commit();

        try {
            Set<String> failed = addExistingMemberWithoutAccess();
            fail("CommitFailedException expected");
        } catch (CommitFailedException e) {
            assertTrue(e.isConstraintViolation());
            assertEquals(31, e.getCode());
        } finally {
            root.refresh();
            assertFalse(testGroup.isMember(memberGroup));
        }

    }
}