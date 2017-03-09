package cc.cafetime.sqlstat;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.lang.instrument.*;
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

    /**
     * 使用-javaagent参数调用入口
     * @param s
     * @param inst
     * @throws Exception
     */
    public static void premain(String s, Instrumentation inst) throws Exception {
        synchronized (Main.class) {
            if (firstTime) {
                firstTime = false;
                System.setProperty(SQL_STAT_AGENT_LOADED, Boolean.TRUE.toString());
            } else {
                throw new Exception("Main : attempting to load SqlStat agent more than once");
            }
        }
        doTransform(inst);
    }

    /**
     * 使用VirtualMachine loadAgent方法调用入口
     * jar包中的manifest文件中需要开启
     * <Can-Redefine-Classes>true</Can-Redefine-Classes>
     * <Can-Retransform-Classes>true</Can-Retransform-Classes>
     * @param s
     * @param inst
     * @throws Exception
     */
    public static void agentmain(String s, Instrumentation inst) throws Exception {
        premain(s, inst);
    }

    public static void doTransform(Instrumentation inst) throws UnmodifiableClassException {
        Class<?>[] allLoadedClasses = inst.getAllLoadedClasses();
        boolean isLoaded = false;
        Class clazz = null;
        for (int i = 0; i < allLoadedClasses.length; i++) {
            String className = allLoadedClasses[i].getName();
            if (0 != className.length() && "com.mysql.jdbc.ConnectionImpl".equals(className)) {
                isLoaded = true;
                clazz = allLoadedClasses[i];
            }
        }
        if (isLoaded) {
            inst.addTransformer(new JDBCTransformer(), true);
            inst.retransformClasses(clazz);
        } else {
            inst.addTransformer(new JDBCTransformer());
        }
    }


    /**
     * 在 ConntectionImpl 中注入监听代码
     */
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


