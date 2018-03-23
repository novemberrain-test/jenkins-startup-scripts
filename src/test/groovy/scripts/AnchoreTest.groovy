package scripts

import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.jvnet.hudson.test.JenkinsRule
import org.jvnet.hudson.test.recipes.LocalData
import org.jvnet.hudson.test.recipes.WithPlugin
import utilities.ZipTestFiles

import static junit.framework.TestCase.assertTrue
import static org.hamcrest.CoreMatchers.equalTo
import static org.junit.Assert.assertFalse
import static org.junit.Assert.assertThat
import static utilities.AddScriptToLocalDataZip.addScriptToLocalDataZip
import static utilities.ResourcePath.resourcePath

class AnchoreTest {
    private static final List SCRIPTS = ["main.groovy", "scripts/anchore.groovy", "config/scripts.config"]
    private static final String SCRIPT_PATH = "scripts"
    private static final String SCRIPT_TARGET = "init.groovy.d"

    @Rule
    public JenkinsRule jenkinsRule = new JenkinsRule()

    @BeforeClass
    public static void setUp() {
        addScriptToLocalDataZip(AnchoreTest.class, SCRIPTS, SCRIPT_PATH, SCRIPT_TARGET, ["loader.groovy": resourcePath("loader/local.groovy", "")])
    }

    @Test
    @LocalData
    @WithPlugin([
            "anchore-container-scanner-1.0.12.hpi",
            "structs-1.10.hpi"])
    @ZipTestFiles(files = ["jenkins.config"])
    void shouldConfigureAnchoreEngine() {
        def descriptor = jenkinsRule.instance.getDescriptor('com.anchore.jenkins.plugins.anchore.AnchoreBuilder')
        assertFalse(descriptor.getDebug() as boolean)
        assertTrue(descriptor.getEnabled() as boolean)
        assertFalse(descriptor.getUseSudo() as boolean)
        assertThat(descriptor.getEnginemode() as String, equalTo('anchoreengine'))
        assertThat(descriptor.getEngineurl() as String, equalTo('https://example.com/anchore'))
        assertThat(descriptor.getEngineuser() as String, equalTo('admin'))
        assertThat(descriptor.getEnginepass() as String, equalTo('foobar'))
        assertFalse(descriptor.engineverify as boolean)
        assertThat(descriptor.getContainerImageId() as String, equalTo('anchore/jenkins:latest'))
        assertThat(descriptor.getContainerId() as String, equalTo('jenkins_anchore'))
    }
}