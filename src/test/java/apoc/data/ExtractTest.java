package apoc.data;

import apoc.util.TestUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import static apoc.util.MapUtil.map;
import static apoc.util.TestUtil.testCall;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;

public class ExtractTest {

    private GraphDatabaseService db;

    @Before
    public void setUp() throws Exception {
        db = new TestGraphDatabaseFactory().newImpermanentDatabase();
        TestUtil.registerProcedure(db, Extract.class);
    }

    @After
    public void tearDown() {
        db.shutdown();
    }

    @Test
    public void testQuotedEmail() {
        testCall(db, "CALL apoc.data.domain('<foo@bar.baz>')",
                row -> Assert.assertThat(row.get("value"), equalTo("bar.baz")));
    }

    @Test
    public void testEmail() {
        testCall(db, "CALL apoc.data.domain('foo@bar.baz')",
                row -> Assert.assertThat(row.get("value"), equalTo("bar.baz")));
    }

    @Test
    public void testNull() {
        testCall(db, "CALL apoc.data.domain(null) YIELD value",
                row -> assertEquals(null, row.get("value")));
    }

    @Test
    public void testBadString() {
        testCall(db, "CALL apoc.data.domain('asdsgawe4ge') YIELD value",
                row -> assertEquals(null, row.get("value")));
    }

    @Test
    public void testEmptyString() {
        testCall(db, "CALL apoc.data.domain('') YIELD value",
                row -> assertEquals(null, row.get("value")));
    }

    @Test
    public void testUrl() {
        testCall(db, "CALL apoc.data.domain('http://www.example.com/lots-of-stuff') YIELD value",
                row -> assertEquals("www.example.com", row.get("value")));
    }

    @Test
    public void testQueryParameter() {
        testCall(db, "CALL apoc.data.domain({param}) YIELD value",
                map("param", "www.foo.bar/baz"),
                row -> assertEquals("www.foo.bar", row.get("value")));
    }
}
