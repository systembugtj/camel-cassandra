/**
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
package com.github.oscerd.component.cassandra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.IndexMetadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.TableMetadata;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import com.datastax.driver.core.schemabuilder.SchemaBuilder;

import org.apache.camel.builder.RouteBuilder;

import com.github.oscerd.component.cassandra.embedded.CassandraBaseTest;

import org.apache.camel.component.mock.MockEndpoint;
import org.junit.Test;

public class CassandraCreateIndexTest extends CassandraBaseTest {

    @Test
    public void testCreateIndex() throws IOException, InterruptedException {
        MockEndpoint mock = getMockEndpoint("mock:result");
        mock.expectedMessageCount(1);
        String body = "";
        Map<String, Object> headers = new HashMap<String, Object>();
        String addr = "127.0.0.1";
        List<String> collAddr = new ArrayList<String>();
        collAddr.add(addr);
        headers.put(CassandraConstants.CASSANDRA_CONTACT_POINTS, collAddr);
        headers.put(CassandraConstants.CASSANDRA_INDEX_COLUMN, "artist");
        headers.put(CassandraConstants.CASSANDRA_INDEX_NAME, "artist_idx");
        ResultSet result = (ResultSet) template.requestBodyAndHeaders("direct:in", body, headers); 
        assertEquals(result.isExhausted(), true);
        assertMockEndpointsSatisfied();
        Cluster cluster = Cluster.builder().addContactPoint("127.0.0.1").build();
        TableMetadata table = cluster.getMetadata()
                .getKeyspace("simplex")
                .getTable("songs");
        IndexMetadata index1 = table.getIndex("album_idx");
        IndexMetadata index2 = table.getIndex("title_idx");
        assertNotNull(index1);
        assertNotNull(index2);
        cluster.close();
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:in")
                    .to("cassandra:cassandraConnection?keyspace=simplex&table=songs&operation=createIndex")
                    .to("mock:result");
            }
        };
    }
}
