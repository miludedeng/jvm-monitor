package cc.cafetime.sqlstat;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;

/**
 * Created by steven on 2017/3/6.
 */
public class Main {
    public static boolean firstTime = true;
    public final static String SQL_STAT_AGENT_LOADED = "cc.cafetime.sqlstat.loaded";
    public static String pid = null;

    static {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        System.out.println(name);
        pid = name.split("@")[0];
    }

    public static void agentmain(String s, Instrumentation inst) throws Exception {
        synchronized (Main.class) {
            if (firstTime) {
                firstTime = false;
                System.setProperty(SQL_STAT_AGENT_LOADED, Boolean.TRUE.toString());
            } else {
                throw new Exception("Main : attempting to load SqlStat agent more than once");
            }
        }
        Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
        for (int i = 0; i < allLoadedClasses.length; i++) {
            String className = allLoadedClasses[i].getName();
            if (0 != className.length() && "com.mysql.jdbc.ConnectionImpl".equals(className)) {
                inst.addTransformer(new JDBCTransformer(), true);
                inst.retransformClasses(allLoadedClasses[i]);
            }
        }
    }

    public static void premain(String s, Instrumentation inst) throws Exception {
        synchronized (Main.class) {
            if (firstTime) {
                firstTime = false;
                System.setProperty(SQL_STAT_AGENT_LOADED, Boolean.TRUE.toString());
            } else {
                throw new Exception("Main : attempting to load SqlStat agent more than once");
            }
        }
        inst.addTransformer(new JDBCTransformer());
    }

    static class JDBCTransformer implements ClassFileTransformer {
        public byte[] transform(ClassLoader loader, final String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
            if (className != null && className.equals("com/mysql/jdbc/ConnectionImpl")) {
                ClassReader reader = new ClassReader(classfileBuffer);
                //创建操作字节流值对象，ClassWriter.COMPUTE_MAXS:表示自动计算栈大小
                ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);
                //接受一个ClassVisitor子类进行字节码修改
                reader.accept(new JdbcClassVisitor(writer, className), 8);
                //返回修改后的字节码流
                return writer.toByteArray();
            }
            return classfileBuffer;
        }
    }
}


