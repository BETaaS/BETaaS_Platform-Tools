/* 
 * Copyright (c) 2005-2011, Fraunhofer-Gesellschaft
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
package org.ogf.graap.wsag.api.types;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.ogf.graap.wsag.api.WsagConstants;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.AgreementType;

/**
 * WSAGXmlType
 * 
 * @author Oliver Waeldrich
 */
public abstract class WSAGXmlType
{
    /**
     * Observable implementation for internal use.
     * 
     * @author owaeld
     */
    public class Wsag4jObservable extends Observable
    {
        private WSAGXmlType type;

        private Wsag4jObservable( WSAGXmlType type )
        {
            this.setType( type );
        }

        @Override
        protected synchronized void setChanged()
        {
            super.setChanged();
        }

        public WSAGXmlType getType()
        {
            return type;
        }

        public void setType( WSAGXmlType type )
        {
            this.type = type;
        }
    }

    private Wsag4jObservable observable = new Wsag4jObservable( this );

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.t#addObserver(java.util.Observer)
     */
    public void addObserver( Observer o )
    {
        observable.addObserver( o );
    }

    /**
     * @param o
     * @see java.util.Observable#deleteObserver(java.util.Observer)
     */
    public void deleteObserver( Observer o )
    {
        observable.deleteObserver( o );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.t#notifyObservers()
     */
    public void notifyObservers()
    {
        observable.notifyObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.t#notifyObservers(java.lang.Object)
     */
    public void notifyObservers( Object arg )
    {
        observable.notifyObservers( arg );
    }

    /**
     * 
     * @see java.util.Observable#deleteObservers()
     */
    public void deleteObservers()
    {
        observable.deleteObservers();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.t#hasChanged()
     */
    public boolean hasChanged()
    {
        return observable.hasChanged();
    }

    protected synchronized void setChanged()
    {
        observable.setChanged();
    }

    /**
     * @return
     * @see java.util.Observable#countObservers()
     */
    public int countObservers()
    {
        return observable.countObservers();
    }

    private static final Logger LOG = Logger.getLogger( WSAGXmlType.class );

    /**
     * Validates the internal XML object representation.
     * 
     * @return true, if the validation process succeeded, false otherwise.
     */
    public abstract boolean validate();

    /**
     * Validates an XML object against its type definition.
     * 
     * @param object
     *            the object to validate
     * @return true, if the validation process succeeded, false otherwise.
     */
    public boolean validate( XmlObject object )
    {
        XmlOptions options = new XmlOptions();
        ArrayList<XmlError> list = new ArrayList<XmlError>();
        options.setErrorListener( list );

        if ( !object.validate( options ) )
        {
            for ( int i = 0; i < list.size(); i++ )
            {
                LOG.error( list.get( i ).getMessage() );
            }

            return false;
        }

        return true;
    }

    /**
     * Processes an agreement template and strips off the template specific elements, namely the template id
     * and creation constraint section.
     * 
     * @param template
     *            the template to process
     * @return the processed agreement type
     */
    protected AgreementType processTemplate( AgreementTemplateType template )
    {
        template = (AgreementTemplateType) template.copy();

        //
        // remove constraint section, if present, and unset the template id
        //
        XmlObject[] constraints = template.selectChildren( WsagConstants.CREATION_CONSTRAINT_ELEMENT_QNAME );
        for ( int i = 0; i < constraints.length; i++ )
        {
            template.getDomNode().removeChild( constraints[i].getDomNode() );
        }

        if ( template.isSetTemplateId() )
        {
            template.unsetTemplateId();
        }

        return (AgreementType) template.changeType( AgreementType.type );
    }

    /**
     * Returns the {@link Observable} for this type.
     * 
     * @return the observable
     */
    public Wsag4jObservable getObservable()
    {
        return observable;
    }

    /**
     * Sets the {@link Observable} for this type.
     * 
     * @param observable
     *            the observable
     */
    public void setObservable( Wsag4jObservable observable )
    {
        this.observable = observable;
    }

}
