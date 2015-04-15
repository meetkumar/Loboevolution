/* -*- Mode: java; tab-width: 8; indent-tabs-mode: nil; c-basic-offset: 4 -*-
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */


package org.mozilla.javascript;

import java.io.CharArrayWriter;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The class of exceptions thrown by the JavaScript engine.
 */
public abstract class RhinoException extends RuntimeException
{

    /**
     * Instantiates a new rhino exception.
     */
    RhinoException()
    {
        Evaluator e = Context.createInterpreter();
        if (e != null)
            e.captureStackInfo(this);
    }

    /**
     * Instantiates a new rhino exception.
     *
     * @param details the details
     */
    RhinoException(String details)
    {
        super(details);
        Evaluator e = Context.createInterpreter();
        if (e != null)
            e.captureStackInfo(this);
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public final String getMessage()
    {
        String details = details();
        if (sourceName == null || lineNumber <= 0) {
            return details;
        }
        StringBuilder buf = new StringBuilder(details);
        buf.append(" (");
        if (sourceName != null) {
            buf.append(sourceName);
        }
        if (lineNumber > 0) {
            buf.append('#');
            buf.append(lineNumber);
        }
        buf.append(')');
        return buf.toString();
    }

    /**
     * Details.
     *
     * @return the string
     */
    public String details()
    {
        return super.getMessage();
    }

    /**
     * Get the uri of the script source containing the error, or null
     * if that information is not available.
     *
     * @return the string
     */
    public final String sourceName()
    {
        return sourceName;
    }

    /**
     * Initialize the uri of the script source containing the error.
     *
     * @param sourceName the uri of the script source responsible for the error.
     *                   It should not be <tt>null</tt>.
     */
    public final void initSourceName(String sourceName)
    {
        if (sourceName == null) throw new IllegalArgumentException();
        if (this.sourceName != null) throw new IllegalStateException();
        this.sourceName = sourceName;
    }

    /**
     * Returns the line number of the statement causing the error,
     * or zero if not available.
     *
     * @return the int
     */
    public final int lineNumber()
    {
        return lineNumber;
    }

    /**
     * Initialize the line number of the script statement causing the error.
     *
     * @param lineNumber the line number in the script source.
     *                   It should be positive number.
     */
    public final void initLineNumber(int lineNumber)
    {
        if (lineNumber <= 0) throw new IllegalArgumentException(String.valueOf(lineNumber));
        if (this.lineNumber > 0) throw new IllegalStateException();
        this.lineNumber = lineNumber;
    }

    /**
     * The column number of the location of the error, or zero if unknown.
     *
     * @return the int
     */
    public final int columnNumber()
    {
        return columnNumber;
    }

    /**
     * Initialize the column number of the script statement causing the error.
     *
     * @param columnNumber the column number in the script source.
     *                     It should be positive number.
     */
    public final void initColumnNumber(int columnNumber)
    {
        if (columnNumber <= 0) throw new IllegalArgumentException(String.valueOf(columnNumber));
        if (this.columnNumber > 0) throw new IllegalStateException();
        this.columnNumber = columnNumber;
    }

    /**
     * The source text of the line causing the error, or null if unknown.
     *
     * @return the string
     */
    public final String lineSource()
    {
        return lineSource;
    }

    /**
     * Initialize the text of the source line containing the error.
     *
     * @param lineSource the text of the source line responsible for the error.
     *                   It should not be <tt>null</tt>.
     */
    public final void initLineSource(String lineSource)
    {
        if (lineSource == null) throw new IllegalArgumentException();
        if (this.lineSource != null) throw new IllegalStateException();
        this.lineSource = lineSource;
    }

    /**
     * Record error origin.
     *
     * @param sourceName the source name
     * @param lineNumber the line number
     * @param lineSource the line source
     * @param columnNumber the column number
     */
    final void recordErrorOrigin(String sourceName, int lineNumber,
                                 String lineSource, int columnNumber)
    {
        // XXX: for compatibility allow for now -1 to mean 0
        if (lineNumber == -1) {
            lineNumber = 0;
        }

        if (sourceName != null) {
            initSourceName(sourceName);
        }
        if (lineNumber != 0) {
            initLineNumber(lineNumber);
        }
        if (lineSource != null) {
            initLineSource(lineSource);
        }
        if (columnNumber != 0) {
            initColumnNumber(columnNumber);
        }
    }

    /**
     * Generate stack trace.
     *
     * @return the string
     */
    private String generateStackTrace()
    {
        // Get stable reference to work properly with concurrent access
        CharArrayWriter writer = new CharArrayWriter();
        super.printStackTrace(new PrintWriter(writer));
        String origStackTrace = writer.toString();
        Evaluator e = Context.createInterpreter();
        if (e != null)
            return e.getPatchedStack(this, origStackTrace);
        return null;
    }

    /**
     * Get a string representing the script stack of this exception.
     * If optimization is enabled, this includes java stack elements
     * whose source and method names suggest they have been generated
     * by the Rhino script compiler.
     * @return a script stack dump
     * @since 1.6R6
     */
    public String getScriptStackTrace()
    {
        StringBuilder buffer = new StringBuilder();
        String lineSeparator = SecurityUtilities.getSystemProperty("line.separator");
        ScriptStackElement[] stack = getScriptStack();
        for (ScriptStackElement elem : stack) {
            elem.render(buffer);
            buffer.append(lineSeparator);
        }
        return buffer.toString();
    }

    /**
     * Get a string representing the script stack in a way that is compatible with V8.
     * If the function "Error.prepareStackTrace" is defined, then call that function,
     * passing it an array of CallSite objects. Otherwise, behave as if "getScriptStackTrace"
     * was called instead.
     *
     * @param cx the cx
     * @param scope the scope
     * @param err the err
     * @return the prepared script stack trace
     * @since 1.7R5
     */
    public Object getPreparedScriptStackTrace(Context cx, Scriptable scope, Scriptable err)
    {
        Scriptable top = ScriptableObject.getTopLevelScope(scope);
        Scriptable error = TopLevel.getBuiltinCtor(cx, top, TopLevel.Builtins.Error);
        Object prepare = error.get("prepareStackTrace", error);
        if (prepare instanceof Function) {
            Function prepareFunc = (Function)prepare;
            ScriptStackElement[] elts = getScriptStack();

            Object[] rawStack = new Object[elts.length];
            for (int i = 0; i < elts.length; i++) {
                rawStack[i] = cx.newObject(top, "CallSite");
                ((NativeCallSite)rawStack[i]).setElement(elts[i]);
            }
            return prepareFunc.call(cx, scope, null, new Object[] { err, cx.newArray(top, rawStack) });
        }
        return getScriptStackTrace();
    }

    /**
     * Get a string representing the script stack of this exception.
     *
     * @param filter ignored
     * @return a script stack dump
     * @since 1.6R6
     * @deprecated the filter argument is ignored as we are able to
     * recognize script stack elements by our own. Use
     * #getScriptStackTrace() instead.
     */
    public String getScriptStackTrace(FilenameFilter filter)
    {
        return getScriptStackTrace();
    }

    /**
     * Get the script stack of this exception as an array of
     * {@link ScriptStackElement}s.
     * If optimization is enabled, this includes java stack elements
     * whose source and method names suggest they have been generated
     * by the Rhino script compiler.
     * @return the script stack for this exception
     * @since 1.7R3
     */
    public ScriptStackElement[] getScriptStack() {
        List<ScriptStackElement> list = new ArrayList<ScriptStackElement>();
        ScriptStackElement[][] interpreterStack = null;
        if (interpreterStackInfo != null) {
            Evaluator interpreter = Context.createInterpreter();
            if (interpreter instanceof Interpreter)
                interpreterStack = ((Interpreter) interpreter).getScriptStackElements(this);
        }
        int interpreterStackIndex = 0;
        StackTraceElement[] stack = getStackTrace();
        // Pattern to recover function name from java method name -
        // see Codegen.getBodyMethodName()
        // kudos to Marc Guillemot for coming up with this
        Pattern pattern = Pattern.compile("_c_(.*)_\\d+");
        for (StackTraceElement e : stack) {
            String fileName = e.getFileName();
            if (e.getMethodName().startsWith("_c_")
                    && e.getLineNumber() > -1
                    && fileName != null
                    && !fileName.endsWith(".java")) {
                String methodName = e.getMethodName();
                Matcher match = pattern.matcher(methodName);
                // the method representing the main script is always "_c_script_0" -
                // at least we hope so
                methodName = !"_c_script_0".equals(methodName) && match.find() ?
                        match.group(1) : null;
                list.add(new ScriptStackElement(fileName, methodName, e.getLineNumber()));
            } else if ("org.mozilla.javascript.Interpreter".equals(e.getClassName())
                    && "interpretLoop".equals(e.getMethodName())
                    && interpreterStack != null
                    && interpreterStack.length > interpreterStackIndex) {
                for (ScriptStackElement elem : interpreterStack[interpreterStackIndex++]) {
                    list.add(elem);
                }
            }
        }
        return list.toArray(new ScriptStackElement[list.size()]);
    }


    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace(java.io.PrintWriter)
     */
    @Override
    public void printStackTrace(PrintWriter s)
    {
        if (interpreterStackInfo == null) {
            super.printStackTrace(s);
        } else {
            s.print(generateStackTrace());
        }
    }

    /* (non-Javadoc)
     * @see java.lang.Throwable#printStackTrace(java.io.PrintStream)
     */
    @Override
    public void printStackTrace(PrintStream s)
    {
        if (interpreterStackInfo == null) {
            super.printStackTrace(s);
        } else {
            s.print(generateStackTrace());
        }
    }

    /**
     * Returns true if subclasses of <code>RhinoException</code>
     * use the Mozilla/Firefox style of rendering script stacks
     * (<code>functionName()@fileName:lineNumber</code>)
     * instead of Rhino's own Java-inspired format
     * (<code> at fileName:lineNumber (functionName)</code>).
     * @return true if stack is rendered in Mozilla/Firefox style
     * @see ScriptStackElement
     * @since 1.7R3
     */
    public static boolean usesMozillaStackStyle() {
        return (stackStyle == StackStyle.MOZILLA);
    }

    /**
     * Tell subclasses of <code>RhinoException</code> whether to
     * use the Mozilla/Firefox style of rendering script stacks
     * (<code>functionName()@fileName:lineNumber</code>)
     * instead of Rhino's own Java-inspired format
     * (<code>at fileName:lineNumber (functionName)</code>)
     * Calling this with "true" is the equivalent of calling:
     * <code>setStackStyle(StackStyle.MOZILLA);</code>
     * @param flag whether to render stacks in Mozilla/Firefox style
     * @see ScriptStackElement
     * @since 1.7R3
     */
    public static void useMozillaStackStyle(boolean flag) {
        stackStyle = (flag ? StackStyle.MOZILLA : StackStyle.RHINO);
    }

    /**
     * Return the stack trace style currently in use.
     *
     * @return the stack style
     * @since 1.7R5
     */
    public static StackStyle getStackStyle() {
        return stackStyle;
    }

    /**
     * Set the stack trace style to use. This replaces "useMozillaStackStyle" since there are now
     * more than two different formats. See "StackStyle" for documentation.
     *
     * @param style the new stack style
     * @since 1.7R5
     */
    public static void setStackStyle(StackStyle style) {
        stackStyle = style;
    }

    /** The Constant serialVersionUID. */
    static final long serialVersionUID = 1883500631321581169L;

    /** The stack style. */
    private static StackStyle stackStyle = StackStyle.RHINO;

    /** The source name. */
    private String sourceName;
    
    /** The line number. */
    private int lineNumber;
    
    /** The line source. */
    private String lineSource;
    
    /** The column number. */
    private int columnNumber;

    /** The interpreter stack info. */
    Object interpreterStackInfo;
    
    /** The interpreter line data. */
    int[] interpreterLineData;
}