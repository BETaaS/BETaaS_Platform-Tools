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

import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.Agreement;
import org.ogf.graap.wsag.api.AgreementOffer;
import org.ogf.schemas.graap.wsAgreement.AgreementContextType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesDocument;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.AgreementRoleType;
import org.ogf.schemas.graap.wsAgreement.AgreementStateDefinition;
import org.ogf.schemas.graap.wsAgreement.AgreementStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermType;
import org.ogf.schemas.graap.wsAgreement.ServiceDescriptionTermType;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.ServiceTermStateType;
import org.ogf.schemas.graap.wsAgreement.TemplateDocument;
import org.ogf.schemas.graap.wsAgreement.TermTreeType;
import org.w3c.dom.Text;

/**
 * Abstract implementation of an agreement. This class implements the required methods to access and store the
 * properties of an agreement. Additionally, when an agreement is created based on an offer, the corresponding
 * states are created for each service term and each guarantee term specified in the offer.
 * 
 * @TODO a future implementation of agreement type will use a handler mechanism to implement the agreement
 *       termination strategy. For that reason, this class will not be abstract anymore and subclassing of
 *       {@link AbstractAgreementType} will not be required anymore. This will make the framework more robust
 *       for user implementations.
 * @TODO Implement Observer pattern to allow registered objects to get notified if the abstract agreement is
 *       updated.
 * @author Oliver Waeldrich
 */
public abstract class AbstractAgreementType extends WSAGXmlType
    implements Agreement
{

    /**
     * The internal XML representation of the agreement instance.
     */
    protected AgreementPropertiesType agreementProperties;

    /**
     * Default XPath expression for selecting all service description terms in an agreement.
     */
    protected static final String DEFAULT_SDT_XPATH =
        "declare namespace wsag='http://schemas.ggf.org/graap/2007/03/ws-agreement';"
            + "$this/wsag:Terms/wsag:All//wsag:ServiceDescriptionTerm";

    /**
     * Default XPath expression for selecting all guarantee terms in an agreement.
     */
    protected static final String DEFAULT_GUARANTEE_TERM_XPATH =
        "declare namespace wsag='http://schemas.ggf.org/graap/2007/03/ws-agreement';"
            + "$this/wsag:Terms/wsag:All//wsag:GuaranteeTerm";

    /**
     * Persisted Execution Context of the agreement instance.
     */
    private Map<String, XmlObject> persistedExecutionContext = new HashMap<String, XmlObject>();

    /**
     * Transcient (Non-Persisted) Execution Context of the agreement instance.
     */
    private final Map<String, Object> transcientExecutionContext = new HashMap<String, Object>();

    /**
     * Creates an agreement based on an offer. The service term states and guarantee term states are
     * automatically created. For each service description term and each guarantee term in the offer an
     * appropriate state is created. for the term selection the according {@link #DEFAULT_SDT_XPATH} and
     * {@link #DEFAULT_GUARANTEE_TERM_XPATH}XPath expression are used.
     * 
     * @param offer
     *            the offer to create the agreement for
     */
    public AbstractAgreementType( AgreementOffer offer )
    {
        super();
        initialize( offer );
    }

    /**
     * Creates a new agreement instance. The provided agreement properties document is used for this instance.
     * 
     * @param agreementPropertiesType
     *            the agreement properties document
     */
    public AbstractAgreementType( AgreementPropertiesType agreementPropertiesType )
    {
        super();
        this.agreementProperties = agreementPropertiesType;
    }

    /**
     * Default constructor. Initializes a minimal resource properties document for this instance with minimal
     * default values.
     */
    public AbstractAgreementType()
    {
        super();

        TemplateDocument templateDoc = TemplateDocument.Factory.newInstance();

        templateDoc.addNewTemplate();

        templateDoc.getTemplate().setAgreementId( "ID" );
        templateDoc.getTemplate().addNewContext().setServiceProvider( AgreementRoleType.AGREEMENT_RESPONDER );

        TermTreeType terms = templateDoc.getTemplate().addNewTerms();

        // will be fixed in MUSE version 2.2.0
        // Muse Hack Issue 159 - Muse does not honor empty elements and therefore fails
        // if an element contains no children and the children are optional (minOccours = 0)
        // therefore we simply include a white space as text content
        // should be removed as soon as fixed

        Text text = terms.getDomNode().getOwnerDocument().createTextNode( " " );
        terms.getDomNode().appendChild( text );

        initialize( new AgreementOfferType( templateDoc.getTemplate() ) );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#validate()
     */
    @Override
    public boolean validate()
    {
        return validate( agreementProperties );
    }

    /**
     * Initializes an agreement type with the standard selection strategy for service description term states
     * and guarantee term states.
     * 
     * @param offer
     *            The agreement offer that is used for the agreement initialization process.
     */
    protected void initialize( AgreementOffer offer )
    {
        initialize( offer, DEFAULT_SDT_XPATH, DEFAULT_GUARANTEE_TERM_XPATH );
    }

    /**
     * Initializes an agreement type with the custom selection strategy for service description term states
     * and guarantee term states.
     * 
     * @param offer
     *            The agreement offer that is used for the agreement initialization process.
     * @param sdtXPath
     *            Custom expression for selecting service terms from the agreement offer.
     * @param guaranteeTermXPath
     *            Custom expression for selecting guarantee terms from the agreement offer.
     */
    protected void initialize( AgreementOffer offer, String sdtXPath, String guaranteeTermXPath )
    {
        //
        // agreementProperties should be an XML element node, not a document fragment
        //
        agreementProperties = AgreementPropertiesDocument.Factory.newInstance().addNewAgreementProperties();

        agreementProperties.setAgreementId( offer.getAgreementId() );
        agreementProperties.addNewContext().set( offer.getContext() );
        agreementProperties.addNewTerms().set( offer.getTerms() );

        agreementProperties.addNewAgreementState().setState( AgreementStateDefinition.OBSERVED );

        //
        // Create a new (empty) SDT state document for each SDT in the offer.
        // There is no extra (monitoring) data associated with this SDT.
        //
        XmlObject[] sdtArray = offer.getXMLObject().selectPath( sdtXPath );
        for ( int i = 0; i < sdtArray.length; i++ )
        {
            ServiceDescriptionTermType sdt = (ServiceDescriptionTermType) sdtArray[i];
            ServiceTermStateType sdtState = agreementProperties.addNewServiceTermState();

            sdtState.setTermName( sdt.getName() );
            sdtState.setState( ServiceTermStateDefinition.NOT_READY );
        }

        //
        // Create a new guarantee term state document for each guarantee term in the offer.
        // The default state is not determined.
        //
        XmlObject[] guaranteeArray = offer.getXMLObject().selectPath( guaranteeTermXPath );
        for ( int i = 0; i < guaranteeArray.length; i++ )
        {
            GuaranteeTermType guarantee = (GuaranteeTermType) guaranteeArray[i];
            GuaranteeTermStateType guaranteeState = agreementProperties.addNewGuaranteeTermState();

            guaranteeState.setTermName( guarantee.getName() );
            guaranteeState.setState( GuaranteeTermStateDefinition.NOT_DETERMINED );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#notifyReload(java.util.Map)
     */
    @Override
    public final void notifyReload( Map<String, XmlObject> executionCtx )
    {
        persistedExecutionContext = executionCtx;
        notifyReinitialized( persistedExecutionContext );
    }

    /**
     * Reload hook. Implementations can overwrite this method in order to implement custom functionality on
     * agreement reload.
     * 
     * @param persistedExecutionCtx
     *            persisted execution properties of the agreement instance
     *            <p>
     *            Since 1.0.0-m4
     *            </p>
     */
    protected void notifyReinitialized( Map<String, XmlObject> persistedExecutionCtx )
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getAgreementId()
     */
    @Override
    public String getAgreementId()
    {
        return agreementProperties.getAgreementId();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getContext()
     */
    @Override
    public AgreementContextType getContext()
    {
        return agreementProperties.getContext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getName()
     */
    @Override
    public String getName()
    {
        return agreementProperties.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getTerms()
     */
    @Override
    public TermTreeType getTerms()
    {
        return agreementProperties.getTerms();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setAgreementId(java.lang.String)
     */
    @Override
    public void setAgreementId( String agreementId )
    {
        agreementProperties.setAgreementId( agreementId );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ogf.graap.wsag.api.types.Agreement1#setContext(org.ogf.schemas.graap.wsAgreement.AgreementContextType
     * )
     */
    @Override
    public void setContext( AgreementContextType context )
    {
        agreementProperties.setContext( context );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setName(java.lang.String)
     */
    @Override
    public void setName( String name )
    {
        agreementProperties.setName( name );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setTerms(org.ogf.schemas.graap.wsAgreement.TermTreeType)
     */
    @Override
    public void setTerms( TermTreeType terms )
    {
        agreementProperties.setTerms( terms );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getState()
     */
    @Override
    public AgreementStateType getState()
    {
        return agreementProperties.getAgreementState();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getGuaranteeTermStates()
     */
    @Override
    public GuaranteeTermStateType[] getGuaranteeTermStates()
    {
        GuaranteeTermStateType[] guarateeStates = agreementProperties.getGuaranteeTermStateArray();

        if ( guarateeStates == null )
        {
            return new GuaranteeTermStateType[0];
        }

        return guarateeStates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getServiceTermStates()
     */
    @Override
    public ServiceTermStateType[] getServiceTermStates()
    {
        ServiceTermStateType[] serviceStates = agreementProperties.getServiceTermStateArray();

        if ( serviceStates == null )
        {
            return new ServiceTermStateType[0];
        }

        return serviceStates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.ogf.graap.wsag.api.types.Agreement1#setState(org.ogf.schemas.graap.wsAgreement.AgreementStateType)
     */
    @Override
    public void setState( AgreementStateType agreementState )
    {
        agreementProperties.setAgreementState( agreementState );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setGuaranteeTermStates(org.ogf.schemas.graap.wsAgreement.
     * GuaranteeTermStateType[])
     */
    @Override
    public void setGuaranteeTermStates( GuaranteeTermStateType[] guaranteeTermStateList )
    {
        agreementProperties.setGuaranteeTermStateArray( guaranteeTermStateList );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setServiceTermStates(org.ogf.schemas.graap.wsAgreement.
     * ServiceTermStateType[])
     */
    @Override
    public void setServiceTermStates( ServiceTermStateType[] serviceTermStateList )
    {
        agreementProperties.setServiceTermStateArray( serviceTermStateList );
        setChanged();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getXMLObject()
     */
    @Override
    public AgreementPropertiesType getXMLObject()
    {
        return agreementProperties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#setXmlObject(org.ogf.schemas.graap.wsAgreement.
     * AgreementPropertiesType)
     */
    @Override
    public void setXmlObject( AgreementPropertiesType properties )
    {
        agreementProperties = properties;
        setChanged();
    }

    /**
     * Returns the agreement instance.
     * 
     * @return the agreement instance
     * @deprecated
     */
    @Deprecated
    public AbstractAgreementType getAgreementInstance()
    {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getExecutionContext()
     */
    @Override
    public Map<String, XmlObject> getExecutionContext()
    {
        return persistedExecutionContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getTransientExecutionContext()
     */
    @Override
    public Map<String, Object> getTransientExecutionContext()
    {
        return transcientExecutionContext;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ogf.graap.wsag.api.types.Agreement1#getImplementationClass()
     */
    @Override
    public Class<?> getImplementationClass()
    {
        return getClass();
    }

}
