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
package org.apache.camel.component.cassandra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.cassandra.embedded.CassandraBaseTest;
import org.junit.Test;

public class CassandraSelectAllWhereTest extends CassandraBaseTest {

    @Test
    public void testSelectWhere() throws IOException, InterruptedException {
        String body = "";
        Map<String, Object> headers = new HashMap<String, Object>();
        String addr = "127.0.0.1";
        List<String> collAddr = new ArrayList<String>();
        collAddr.add(addr);
        headers.put(CassandraConstants.CASSANDRA_CONTACT_POINTS, collAddr);
        headers.put(CassandraConstants.CASSANDRA_WHERE_COLUMN, "album");
        headers.put(CassandraConstants.CASSANDRA_WHERE_VALUE, "The gathering");
        headers.put(CassandraConstants.CASSANDRA_OPERATOR, "eq");
        ResultSet result = (ResultSet) template.requestBodyAndHeaders("direct:in", body, headers);
        for (Row row : result) {
            assertEquals(row.getString("album"), "The gathering");
        }
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            public void configure() {
                from("direct:in")
                    .to("cassandra:cassandraConnection?keyspace=simplex&table=songs&operation=selectAllWhere");
            }
        };
    }
}
