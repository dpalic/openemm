# ifndef	__WIN32_SYSLOG_H
# define	__WIN32_SYSLOG_H		1
# define	LOG_PID				0
# define	LOG_LOCAL0			0
# define	LOG_MAIL			0
# define	LOG_NOTICE			0

# define	openlog(...)
# define	closelog()
# define	syslog(...)
# endif		/* __WIN32_SYSLOG_H */
