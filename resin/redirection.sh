export LANG=en_US.ISO8859_1
$HOME/bin/httpd.sh -conf $HOME/conf/redirection.conf -jvm-log $HOME/var/log/jvm_redirection.log -stderr $HOME/var/log/redirection_stderr.log -stdout $HOME/var/log/redirection_stdout.log -pid $HOME/var/run/redirection.pid -server redirection $args $*
