package cc.cafetime.type;


import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * This application type factory recognizes some well-known Java application
 * based on their main class name.
 *
 * @author Tomas Hurka
 */
public class MainClassApplicationTypeFactory {
    private static final int CLASS_NAME = 0;
    private static final int NAME = 1;
    private static final int DESCRIPTION = 2;
    private static final int ICON_PATH = 3;

    private static Map<String, String[]> map;

    private static Properties descProps = new Properties();

    private static String[][] appmatrix = {
            // build tools
            {"org.apache.tools.ant.launch.Launcher", "Ant", descr("DESCR_Ant"), "application.png"},  // NOI18N

            // Application servers
            {"com.sun.enterprise.server.PELaunch", "GlassFish", descr("DESCR_GlassFish"), "GlassFish.png"},   // NOI18N
            {"com.sun.enterprise.glassfish.bootstrap.ASMain", "GlassFish", descr("DESCR_GlassFish"), "GlassFish.png"},   // NOI18N
            {"com.sun.enterprise.ee.nodeagent.NodeAgentMain", "GlassFish Node", "GlassFish Node", "GlassFish.png"}, // NOI18N
            {"org.apache.catalina.startup.Bootstrap", "Tomcat", descr("DESCR_Tomcat"), "Tomcat.png"},  // NOI18N
            {"org.jboss.Main", "JBoss", descr("DESCR_JBoss"), "application.png"},  // NOI18N

            // JDK tools
            {"sun.tools.jconsole.JConsole", "JConsole", descr("DESCR_JConsole"), "application.png"},  // NOI18N
            {"sun.tools.jps.Jps", "Jps", descr("DESCR_Jps"), "application.png"}, // NOI18N
            {"sun.tools.jstat.Jstat", "Jstat", descr("DESCR_Jstat"), "application.png"},   // NOI18N
            {"sun.tools.jstatd.Jstatd", "Jstatd", descr("DESCR_Jstatd"), "application.png"},    // NOI18N
            {"sun.jvm.hotspot.tools.JStack", "JStack", descr("DESCR_Jstack"), "application.png"},   // NOI18N
            {"sun.tools.jstack.JStack", "JStack", descr("DESCR_Jstack"), "application.png"},    // NOI18N
            {"sun.jvm.hotspot.tools.JMap", "JMap", descr("DESCR_Jmap"), "application.png"},   // NOI18N
            {"sun.tools.jmap.JMap", "JMap", descr("DESCR_Jmap"), "application.png"},  // NOI18N
            {"com.sun.tools.hat.Main", "JHat", descr("DESCR_Jhat"), "application.png"},   // NOI18N
            {"sun.tools.jinfo.JInfo", "JInfo", descr("DESCR_Jinfo"), "application.png"},   // NOI18N
            {"sun.jvm.hotspot.jdi.SADebugServer", "jsadebugd", descr("DESCR_Jsadebugd"), "application.png"},   // NOI18N

            // JDK utilitites
            {"sun.tools.jar.Main", "Jar", descr("DESCR_Jar"), "application.png"},    // NOI18N
            {"com.sun.java.util.jar.pack.Driver", "pack200", descr("DESCR_Pack200"), "application.png"}, // NOI18N
            {"com.sun.tools.javadoc.Main", "JavaDoc", descr("DESCR_JavaDoc"), "application.png"},    // NOI18N
            {"com.sun.tools.javac.Main", "Javac", descr("DESCR_Javac"), "application.png"},    // NOI18N
            {"com.sun.tools.javah.Main", "Javah", descr("DESCR_Javah"), "application.png"},    // NOI18N
            {"sun.tools.javap.Main", "Javap", descr("DESCR_Javap"), "application.png"},    // NOI18N
            {"sun.security.tools.JarSigner", "JarSigner", descr("DESCR_JarSigner"), "application.png"},    // NOI18N
            {"com.sun.tools.apt.Main", "APT", descr("DESCR_Apt"), "application.png"},    // NOI18N
            {"sun.applet.Main", "Applet Viewer", descr("DESCR_AppletViewer"), "application.png"},    // NOI18N
            {"sun.applet.AppletViewer", "Applet Viewer", descr("DESCR_AppletViewer"), "application.png"},    // NOI18N

            // Best known JDK demos
            {"FileChooserDemo", "FileChooserDemo", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"Font2DTest", "Font2DTest", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"java2d.Java2Demo", "Java2Demo", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"Metalworks", "Metalworks", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"Notepad", "Notepad", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"SampleTree", "SampleTree", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"Stylepad", "Stylepad", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"SwingSet2", "SwingSet2", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N
            {"TableExample", "TableExample", descr("DESCR_JdkDemoApp"), "application.png"},    // NOI18N

            // Java DB
            {"org.apache.derby.drda.NetworkServerControl", "JavaDB", descr("DESCR_JavaDb"), "JavaDB.png"},   // NOI18N

            // JRockit Mission Control
            {"com.jrockit.mc.rcp.start.MCMain", "JRockit Mission Control", descr("DESCR_JRMC"), "JRMC.png"},  // NOI18N

            // Oracle WebLogic
            {"weblogic.Server", "WebLogic", descr("DESCR_WLS"), "WLS.png"},  // NOI18N

            // JRuby runtime
            {"org.jruby.Main", descr("LBL_Jruby"), descr("DESCR_Jruby"), "JRuby.png"},  // NOI18N

            // Scala runtime
            {"scala.tools.nsc.MainGenericRunner", descr("LBL_Scala"), descr("DESCR_Scala"), "Scala.png"},  // NOI18N

            // Clojure runtime
            {"clojure.main", descr("LBL_Clojure"), descr("DESCR_Clojure"), "Clojure.png"},  // NOI18N
            {"clojure.jar", descr("LBL_Clojure"), descr("DESCR_Clojure"), "Clojure.png"},  // NOI18N
            {"clojure.lang.Script", descr("LBL_Clojure"), descr("DESCR_Clojure"), "Clojure.png"},  // NOI18N
            {"clojure.lang.Repl", descr("LBL_Clojure"), descr("DESCR_Clojure"), "Clojure.png"},  // NOI18N

            // Groovy runtime
            {"org.codehaus.groovy.tools.GroovyStarter", descr("LBL_Groovy"), descr("DESCR_Groovy"), "Groovy.png"},  // NOI18N

            // Jython runtime
            {"org.python.util.jython", descr("LBL_Jython"), descr("DESCR_Jython"), "Jython.png"},  // NOI18N

    };

    static {
        map = new HashMap();
        for (int i = 0; i < appmatrix.length; i++) {
            String[] appDesc = appmatrix[i];

            map.put(appDesc[CLASS_NAME], appDesc);
        }

        try {
            descProps.load(MainClassApplicationTypeFactory.class.getResourceAsStream("/Bundle.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String descr(String key) {
        return (String) descProps.get(key); // NOI18N
    }


    public MainClassApplicationType getApplicationType(String mainClass) {
        String[] appDesc = map.get(mainClass);
        if (appDesc != null) {
            return new MainClassApplicationType(appDesc[NAME], appDesc[DESCRIPTION], appDesc[ICON_PATH]);
        }
        return null;
    }

}
