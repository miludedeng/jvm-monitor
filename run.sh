#!/bin/bash
DIR=$( cd $(dirname $0) ; pwd -P )

if [ -z "$JAVA_HOME" ] ; then
        JAVA_HOME=`readlink -f \`which java 2>/dev/null\` 2>/dev/null | \
        sed 's/\/bin\/java//'`
fi

TOOLSJAR="$JAVA_HOME/lib/tools.jar"

if [ ! -f "$TOOLSJAR" ] ; then
        echo "$JAVA_HOME seems to be no JDK!" >&2
        exit 1
fi

"$JAVA_HOME"/bin/java $JAVA_OPTS -cp "$DIR/jvmtop-0.9.0-SNAPSHOT.jar:$TOOLSJAR" org.springframework.boot.loader.JarLauncher "$@"
exit $?
