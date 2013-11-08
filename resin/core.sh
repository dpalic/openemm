export LANG=en_US.UTF-8
export RESIN_HOME=$HOME
$HOME/bin/httpd.sh -J-Xms128m -J-Xmx512m -conf $HOME/conf/core.conf -server-root $HOME/webapps/core -jvm-log $HOME/webapps/core/log/openemm_jvm.log -stderr $HOME/webapps/core/log/openemm_stderr.log -stdout $HOME/webapps/core/log/openemm_stdout.log -server core -pid $HOME/var/run/core.pid $args $*
