package org.jvnet.hudson.plugins.platformlabeler;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.File;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;

public class PlatformDetailsTaskTest {

  private PlatformDetailsTask platformDetailsTask;

  public PlatformDetailsTaskTest() {}

  @Before
  public void createPlatformDetailsTask() {
    platformDetailsTask = new PlatformDetailsTask();
  }

  @Test
  public void testCall() throws Exception {
    Set<String> details = platformDetailsTask.call();
    if (isWindows()) {
      assertThat(details, hasItems("windows"));
    } else {
      assertThat(details, not(hasItems("windows")));
    }
    String osName = System.getProperty("os.name", "os.name.is.unknown");
    if (osName.toLowerCase().startsWith("linux")) {
      assertThat(details, not(hasItems("linux")));
      assertThat(details, not(hasItems("Linux")));
      assertThat(
          details,
          anyOf(
              hasItems("Alpine"),
              hasItems("Amazon"),
              hasItems("AmazonAMI"),
              hasItems("Debian"),
              hasItems("CentOS"),
              hasItems("Ubuntu")));
    }
  }

  @Test
  public void testComputeLabelsLinux32Bit() throws Exception {
    Set<String> details = platformDetailsTask.computeLabels("x86", "linux", "xyzzy");
    assertThat(details, not(hasItems("windows")));
    String osName = System.getProperty("os.name", "os.name.is.unknown");
    if (osName.toLowerCase().startsWith("linux")) {
      assertThat(details, not(hasItems("linux")));
      assertThat(details, not(hasItems("Linux")));
      assertThat(
          details,
          anyOf(
              hasItems("Alpine"),
              hasItems("Amazon"),
              hasItems("AmazonAMI"),
              hasItems("Debian"),
              hasItems("CentOS"),
              hasItems("Ubuntu")));
      // Yes, this is a dirty trick to detect the hardware architecture on some JVM's
      String expectedArch =
          System.getProperty("sun.arch.data.model", "23").equals("32") ? "x86" : "amd64";
      // Assumes tests run in JVM that matches operating system
      assertThat(details, hasItems(expectedArch));
    }
  }

  @Test
  public void testCheckWindows32Bit() {
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "AMD64", null), is("amd64"));
  }

  @Test
  public void testCheckWindows32BitAMD64SecondArgument() {
    assertThat(platformDetailsTask.checkWindows32Bit("x86", "x86", "AMD64"), is("amd64"));
  }

  private boolean isWindows() {
    return File.pathSeparatorChar == ';';
  }
}
