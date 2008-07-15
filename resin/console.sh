export LANG=en_US.ISO8859_1
$HOME/bin/httpd.sh -conf $HOME/conf/console.conf -jvm-log $HOME/var/log/jvm_console.log -stderr $HOME/var/log/console_stderr.log -stdout $HOME/var/log/console_stdout.log -pid $HOME/var/run/openemm.pid -server console $args $*
