package	org.agnitas.util;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.servlet.ServletException;

public class	ExceptionProperties	extends Properties	{
	private static final long serialVersionUID = 2335686698133734099L;
	private	Throwable	exception;

	private void	load(Class cl) {
		Class	prev=cl.getSuperclass();

		if(prev != null)
			load(prev);

		try {
			ResourceBundle	res=PropertyResourceBundle.getBundle("exceptions."+cl.getName(), Locale.GERMAN);
			Enumeration keys=res.getKeys();

			while(keys.hasMoreElements()) {
				String	name=(String) keys.nextElement();

				setProperty(name,res.getString(name));
			}
		} catch(Exception e) {
			;
		}
	}

	public	ExceptionProperties(Exception e) {
		exception=e;
		if(e instanceof ServletException) {
			Throwable sub=((ServletException) e).getRootCause();
			if(sub != null)
				exception=sub;
		}
		String	message=exception.getMessage();

		if(message == null)
			message=new String("");

		StringTokenizer	msg=new StringTokenizer(message,"$");

		load(exception.getClass());
		if(msg.hasMoreTokens()) {
			Object[]	obj={	null,null,null,null,null,
						null,null,null,null,null
					};
			String	id=msg.nextToken();
			String	ret=getProperty(id);

			if(ret == null)
				ret=id;
			for(int c=0;c < 10 && msg.hasMoreTokens();c++)
				obj[c]=msg.nextElement();
                        System.out.println("ret: "+ret+" Obj: "+obj);
			setProperty("Message",MessageFormat.format(ret,obj));
			if((ret=getProperty(id+".Solution")) != null)
				setProperty("Solution",ret);
		} else
			setProperty("Message",message);
	}

	public void	printStackTrace(PrintWriter dst) {
		exception.printStackTrace(dst);
	}

	public void	printStackTrace(PrintStream dst) {
		exception.printStackTrace(dst);
	}
}

