/* 
 * Copyright (c) 2012, Fraunhofer-Gesellschaft
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * (1) Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the disclaimer at the end.
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 * 
 * (2) Neither the name of Fraunhofer nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 * 
 * DISCLAIMER
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  
 */
package org.ogf.graap.wsag.api.logging;


import java.text.MessageFormat;

/**
 * A simple log message that can be used in conjunction with Log4J in order to improve logging speed. A LogMessage
 * takes a message and a number of arguments that are formated with {@link MessageFormat} on demand, i.e. the
 * rendering of the resulting message will only take place if the message is really logged by the Log4J
 * framework, i.e. the log level is set accordingly.
 * 
 * @author owaeld
 */
public class LogMessage
{
    private String pattern;

    private Object[] arguments;

    /**
     * Creates a new log message.
     * 
     * @param message
     *            the message pattern
     * @param params
     *            the message parameters
     * 
     * @see MessageFormat#format(String, Object...)
     */
    public LogMessage( String message, Object... params )
    {
        pattern = message;
        arguments = params;
    }

    /**
     * Creates a new log message.
     * 
     * @param message
     *            the message pattern
     * @param params
     *            the message parameters
     * 
     * @return the new {@link LogEntry}
     * 
     * @see MessageFormat#format(String, Object...)
     */
    public static LogMessage getMessage( String message, Object... params )
    {
        return new LogMessage( message, params );
    }

    /**
     * Formats a log message to a string.
     * 
     * @param message
     *            the message pattern
     * @param params
     *            the message parameters
     * 
     * @return the new formated message
     * 
     * @see MessageFormat#format(String, Object...)
     */
    public static String format( String message, Object... params )
    {
        return new LogMessage( message, params ).toString();
    }
    
    /**
     * Renders the message when required by Log4J, i.e. the message is finally printed to the log.
     * 
     * @return the rendered message
     */
    @Override
    public String toString()
    {
        return MessageFormat.format( pattern, arguments );
    }
}
