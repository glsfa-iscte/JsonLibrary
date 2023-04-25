import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
@Suite
@SelectClasses(
    ModelTesting::class,
    VisitorTesting::class,
    TestingReflexionInstantiation::class
)
/**
 * Test suite This class is used to run all test classes that have been selected in @SelectClasses
 *
 */
class TestSuite