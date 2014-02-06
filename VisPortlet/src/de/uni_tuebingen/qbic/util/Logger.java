package de.uni_tuebingen.qbic.util;

import java.util.Arrays;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * Abstraction helper class to keep specific logging calls out of the ethz.* code to switch
 * more easy to different logging mechanisms later (if required)
 * @author lorenz
 *
 */
public class Logger {

	Log liferayLog;
	
	/**
	 * The constructor initializes the underlying logger object (e.g.
	 * liferayLog)
	 */
	public Logger(Class<?> c) {
		liferayLog = LogFactoryUtil.getLog(c);
	}
	
	/**
	 * Logs debug messages (used for developing/testing only)
	 * @param s string to log
	 */
	public void debug(String s) {
		liferayLog.debug(s);
	}
	
	/**
	 * Logs info messages (used for important non-error messages, for 
	 * real problems use warn/error methods)
	 * @see #warn(String)
	 * @see #error(String)
	 * @param s string to log
	 */
	public void info(String s) {
		liferayLog.info(s);
	}
	
	/**
	 * Logs non-critical error messages
	 * @param s string to log
	 */
	public void warn(String s) {
		liferayLog.warn(s);
	}
	
	/**
	 * Logs error messages (severe faults). If you need more output see
	 * @see #error(String, Throwable)
	 * @param s string to log
	 */
	public void error(String s) {
		liferayLog.error(s);
	}
	
	/**
	 * Logs error message (severe fault) and adds the top 10 lines of the stack trace (to
	 * not overload the console) 
	 * @param s string to log
	 * @param t throwable to expand log
	 */
	public void error(String s, Throwable t) {
		//do not fill logfile with millions of lines per error, please
		t.setStackTrace(Arrays.copyOfRange(t.getStackTrace(), 0, 10));
		liferayLog.error(s,t);
	}
	
}
