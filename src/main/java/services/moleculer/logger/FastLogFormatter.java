package services.moleculer.logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Fast, single-threaded log formatter for AsyncFileLogger.
 */
public final class FastLogFormatter extends Formatter {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");

	private static final char[] SEVERE = " | SEVERE  | ".toCharArray();
	private static final char[] WARNING = " | WARNING | ".toCharArray();
	private static final char[] INFO = " | INFO    | ".toCharArray();
	private static final char[] CONFIG = " | CONFIG  | ".toCharArray();
	private static final char[] FINE = " | FINE    | ".toCharArray();
	private static final char[] FINER = " | FINER   | ".toCharArray();
	private static final char[] FINEST = " | FINEST  | ".toCharArray();

	private static final char[] TUBE = " | ".toCharArray();
	private static final char[] BREAK = System.getProperty("line.separator", "\r\n").toCharArray();

	private static final char[] ERROR_AT_LINE = " at line ".toCharArray();
	private static final char[] ERROR_IN = " in ".toCharArray();
	private static final char[] ERROR_BRACKETS = "()".toCharArray();

	private final StringBuilder line = new StringBuilder(512);

	private volatile int position;

	public final String format(LogRecord record) {
		line.setLength(0);
		line.append(DATE_FORMAT.format(new Date(record.getMillis())));
		
		final Level l = record.getLevel();
		if (l == Level.SEVERE) {
			line.append(SEVERE);
		} else if (l == Level.WARNING) {
			line.append(WARNING);
		} else if (l == Level.INFO) {
			line.append(INFO);
		} else if (l == Level.CONFIG) {
			line.append(CONFIG);
		} else if (l == Level.FINE) {
			line.append(FINE);
		} else if (l == Level.FINER) {
			line.append(FINER);
		} else {
			line.append(FINEST);
		}
		
		String className = record.getSourceClassName();
		int n;
		if (className == null) {
			className = "unknown";
		} else {
			n = className.lastIndexOf('$');
			if (n > -1) {
				className = className.substring(0, n);
			}
		}
		line.append(className);
		n = line.length();
		if (n > position) {
			if (position == 0) {
				position = n + 10;
			} else {
				position = n;
			}
		} else if (position - n > 20) {
			position = n;
		}
		n = position - n;
		if (n > 0) {
			for (int i = 0; i < n; i++) {
				line.append(' ');
			}
		}
		line.append(TUBE);
		line.append(formatMessage(record));
		
		final Throwable cause = record.getThrown();
		if (cause != null) {
			n = line.length();
			line.append(BREAK);
			dump(cause, 0, n);
		} else {
			line.append(BREAK);
		}
		return line.toString();
	}

	private final void dump(Throwable cause, int level, int lineLength) {
		if (level == 0) {
			for (int i = 0; i < lineLength; i++) {
				line.append('-');
			}
			line.append(BREAK);
		}
		String msg = cause.getMessage();
		if (msg == null || msg.isEmpty()) {
			msg = cause.toString();
		}
		line.append(msg.trim());
		line.append(BREAK);
		line.append(BREAK);
		StackTraceElement[] elements = cause.getStackTrace();
		for (StackTraceElement element : elements) {
			line.append(ERROR_AT_LINE);
			int num = element.getLineNumber();
			line.append(num);
			line.append('.');
			if (num < 10) {
				line.append(' ');
			}
			if (num < 100) {
				line.append(' ');
			}
			if (num < 1000) {
				line.append(' ');
			}
			line.append(ERROR_IN);
			line.append(element.getClassName());
			line.append('.');
			line.append(element.getMethodName());
			line.append(ERROR_BRACKETS);
			line.append(BREAK);
		}
		cause = cause.getCause();
		if (level < 5 && cause != null) {
			line.append(BREAK);
			dump(cause, ++level, lineLength);
		} else {
			for (int i = 0; i < lineLength; i++) {
				line.append('-');
			}
			line.append(BREAK);
		}
	}

}