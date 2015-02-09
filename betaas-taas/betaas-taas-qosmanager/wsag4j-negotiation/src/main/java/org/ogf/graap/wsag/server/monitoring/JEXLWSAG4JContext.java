/* 
 * Copyright (c) 2007, Fraunhofer-Gesellschaft
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
package org.ogf.graap.wsag.server.monitoring;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.jexl2.JexlContext;

/**
 * An implementation of the {@link JexlContext}. In contrast to the default implementation this specific
 * implementation throws an {@link IllegalStateException} if a non-existing variable is resolved from the
 * context. This prevents that default values are used for non-existing variables during expression
 * evaluation.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class JEXLWSAG4JContext extends HashMap<String, Object> implements JexlContext
{

    private static final long serialVersionUID = 1L;

    /**
     * Sets a list variables for this context at once.
     * 
     * @param map
     *            the parameters to set
     * 
     * @see JexlContext#set(String, Object)
     */
    public void setVars( Map<String, Object> map )
    {
        clear();
        putAll( map );
    }

    /**
     * {@inheritDoc}
     */
    public Object get( String key )
    {
        Object result = super.get( key );

        if ( result == null )
        {
            throw new IllegalStateException(
                                             MessageFormat.format( "Unable to resolve value for variable {0}",
                                                                   new Object[] { key } ) );
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.jexl2.JexlContext#has(java.lang.String)
     */
    public boolean has( String key )
    {
        return containsKey( key );
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.apache.commons.jexl2.JexlContext#set(java.lang.String, java.lang.Object)
     */
    public void set( String name, Object value )
    {
        super.put( name, value );
    }

}
