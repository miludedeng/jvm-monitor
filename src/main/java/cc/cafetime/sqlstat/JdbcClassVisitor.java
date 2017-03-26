package cc.cafetime.sqlstat;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by liujing on 2017/3/4.
 */
public class JdbcClassVisitor extends ClassVisitor {
    private String className;

    public JdbcClassVisitor(ClassVisitor cv, String className) {
        super(Opcodes.ASM5, cv);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, final String name, final String desc, String signature, String[] exceptions) {
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        if ("execSQL".equals(name) && "(Lcom/mysql/jdbc/StatementImpl;Ljava/lang/String;ILcom/mysql/jdbc/Buffer;IIZLjava/lang/String;[Lcom/mysql/jdbc/Field;Z)Lcom/mysql/jdbc/ResultSetInternalMethods;".equals(desc) && mv != null) {
            mv = new AdviceAdapter(Opcodes.ASM5, mv, access, name, desc) {
                private int time;

                //方法进入时获取开始时间
                @Override
                public void onMethodEnter() {
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System",
                            "currentTimeMillis", "()J", false);
                    time = newLocal(Type.LONG_TYPE);
                    mv.visitVarInsn(Opcodes.LSTORE, time);
                }

                //方法退出时将参数和开始时间传出
                @Override
                public void onMethodExit(int opcode) {
                    mv.visitVarInsn(LLOAD, time);
                    loadArgArray();
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "cc/cafetime/sqlstat/SQLReciver", "recive","(J[Ljava/lang/Object;)V", false);
                }
            };
        }
        return mv;
    }


}

