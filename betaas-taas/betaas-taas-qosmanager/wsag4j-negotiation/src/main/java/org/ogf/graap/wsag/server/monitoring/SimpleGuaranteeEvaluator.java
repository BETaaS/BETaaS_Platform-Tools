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

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;
import org.ogf.graap.wsag.server.accounting.AccountingContext;
import org.ogf.graap.wsag.server.accounting.IAccountingContext;
import org.ogf.graap.wsag.server.accounting.IAccountingSystem;
import org.ogf.graap.wsag.server.accounting.SimpleAccountingSystemLogger;
import org.ogf.graap.wsag4j.types.engine.GuaranteeEvaluationResultType;
import org.ogf.graap.wsag4j.types.engine.SLOEvaluationResultType;
import org.ogf.schemas.graap.wsAgreement.AgreementPropertiesType;
import org.ogf.schemas.graap.wsAgreement.CompensationType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateDefinition;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermStateType;
import org.ogf.schemas.graap.wsAgreement.GuaranteeTermType;
import org.ogf.schemas.graap.wsAgreement.ServiceLevelObjectiveType;

/**
 * Default implementation of a guarantee evaluator. The guarantee evaluator evaluates the qualifying condition
 * and service level objective of a given guarantee and returns the guarantee term state based on the
 * evaluation result.
 * 
 * @author Oliver Waeldrich
 * 
 */
public class SimpleGuaranteeEvaluator implements IGuaranteeEvaluator
{

    //
    // constants
    //
    private static final String GUARANTEE_TERM_XPATH =
        "declare namespace wsag='http://schemas.ggf.org/graap/2007/03/ws-agreement';" //$NON-NLS-1$
            + "$this//wsag:GuaranteeTerm"; //$NON-NLS-1$

    //
    // error message definitions
    //
    private static final String MSG_QC_FULFILLED = "SimpleGuaranteeEvaluator.QC_FULFILLED"; //$NON-NLS-1$

    private static final String MSG_VEXPR_TYPE_ERROR = "SimpleGuaranteeEvaluator.VALUE_EXPR_TYPE_ERROR"; //$NON-NLS-1$

    private static final String MSG_QC_NOT_FULFILLED = "SimpleGuaranteeEvaluator.QC_NOT_FULFILLED"; //$NON-NLS-1$

    private static final String MSG_QC_TYPE_ERROR = "SimpleGuaranteeEvaluator.QC_TYPE_ERROR"; //$NON-NLS-1$

    private static final String MSG_SLO_NO_CONTENT = "SimpleGuaranteeEvaluator.SLO_NO_CONTENT"; //$NON-NLS-1$

    private static final String MSG_SLO_TYPE_ERROR = "SimpleGuaranteeEvaluator.SLO_TYPE_ERROR"; //$NON-NLS-1$

    //
    // private variable definitions
    //
    private static final Logger LOG = Logger.getLogger( SimpleGuaranteeEvaluator.class );

    private IAccountingSystem accountingSystem;

    private JexlEngine jexl;

    //
    // open issues:
    // - assessment intervals are not supported at all
    //
    //
    // The AgreementPropertiesDocument holds all the information related to an agreement.
    // This information is externally exposed as different properties of the agreement
    // and can be queried vie the WSRF GetResourceProperty method. Internally, all this
    // information is stored in one document.
    //

    /**
     * Default constructor
     */
    public SimpleGuaranteeEvaluator()
    {
        accountingSystem = new SimpleAccountingSystemLogger();

        //
        // create JEXL engine and initialize wsag4j functions
        //
        jexl = new JexlEngine();
        JEXLWSAG4JContext functions = new JEXLWSAG4JContext();
        SLAFunctions funcImpl = new SLAFunctions();
        functions.set( "wsag4j", funcImpl ); //$NON-NLS-1$
        jexl.setFunctions( functions );
    }

    /**
     * @return the accountingSystem
     * 
     * @deprecated
     */
    public IAccountingSystem getAccountingSystem()
    {
        return accountingSystem;
    }

    /**
     * @param accountingSystem
     *            the accountingSystem to set
     * 
     * @deprecated
     */
    public void setAccountingSystem( IAccountingSystem accountingSystem )
    {
        this.accountingSystem = accountingSystem;
    }

    /**
     * {@inheritDoc}
     */
    public GuaranteeEvaluationResultType evaluate( GuaranteeTermType guarantee, Map<String, Object> variables )
        throws Exception
    {
        //
        // initialize guarantee term evaluation result
        //
        GuaranteeEvaluationResultType evaluationResult = GuaranteeEvaluationResultType.Factory.newInstance();
        evaluationResult.addNewDetails();
        evaluationResult.getDetails().addNewGuarantee().set( guarantee );
        evaluationResult.setName( guarantee.getName() );
        evaluationResult.setType( SLOEvaluationResultType.SLO_NOT_DETERMINED );

        if ( guarantee.getBusinessValueList().isSetImportance() )
        {
            evaluationResult.setImportance( guarantee.getBusinessValueList().getImportance().intValue() );
        }
        else
        {
            evaluationResult.setImportance( 0 );
        }

        GuaranteeTermStateType state = evaluationResult.getDetails().addNewGuaranteeState();
        state.setTermName( guarantee.getName() );
        state.setState( GuaranteeTermStateDefinition.NOT_DETERMINED );

        // Create a JEXL context
        JEXLWSAG4JContext jc = new JEXLWSAG4JContext();
        jc.setVars( variables );

        //
        // create Guarantee States
        //
        if ( guarantee.isSetQualifyingCondition() )
        {
            //
            // First, check whether the guarantee term will be evaluated or not.
            // Therefore we need to check the qualifying condition, if present
            //
            // In the default implementation we assume that the qualifying condition
            // is set as a string value.
            //
            XmlObject qcObject = guarantee.getQualifyingCondition();
            String condition = XmlString.Factory.parse( qcObject.getDomNode() ).getStringValue();

            Expression qcExpr = jexl.createExpression( condition );
            Object qcExprResult = qcExpr.evaluate( jc );
            if ( qcExprResult instanceof Boolean )
            {
                if ( ( (Boolean) qcExprResult ).booleanValue() )
                {
                    LOG.info( Messages.formatString( MSG_QC_FULFILLED, guarantee.getName() ) );
                }
                else
                {
                    LOG.info( Messages.formatString( MSG_QC_NOT_FULFILLED, guarantee.getName() ) );
                    return evaluationResult;
                }
            }
            else
            {
                String message =
                    Messages.formatString( MSG_QC_TYPE_ERROR, new Object[] { guarantee.getName() } );
                throw new Exception( message );
            }
        }

        ServiceLevelObjectiveType slo = guarantee.getServiceLevelObjective();

        XmlObject sloCSL = null;
        if ( slo.isSetKPITarget() )
        {
            sloCSL = slo.getKPITarget().getCustomServiceLevel();
        }
        else if ( slo.isSetCustomServiceLevel() )
        {
            sloCSL = slo.getCustomServiceLevel();
        }
        else
        {
            throw new Exception( Messages.getString( MSG_SLO_NO_CONTENT ) ); //$NON-NLS-1$
        }

        String exprLit = XmlString.Factory.parse( sloCSL.getDomNode() ).getStringValue();
        Expression expr = jexl.createExpression( exprLit );

        //
        // Now evaluate the expression, getting the result
        //
        Object exprResult = expr.evaluate( jc );
        if ( exprResult instanceof Boolean )
        {
            IAccountingContext context = new AccountingContext();
            context.setGuarantee( guarantee );
            context.setEvaluationResult( ( (Boolean) exprResult ).booleanValue() );

            if ( context.getEvaluationResult() )
            {
                //
                // account a reward
                //
                state.setState( GuaranteeTermStateDefinition.FULFILLED );
                evaluationResult.setType( SLOEvaluationResultType.SLO_FULFILLED );

                if ( guarantee.getBusinessValueList().getRewardArray().length > 0 )
                {
                    CompensationType reward = guarantee.getBusinessValueList().getRewardArray( 0 );

                    XmlObject valueExpr = reward.getValueExpression();
                    String expression = XmlString.Factory.parse( valueExpr.getDomNode() ).getStringValue();

                    Long value = evaluateBusinessValue( expression, jc );
                    String unit = ( reward.getValueUnit() != null ) ? reward.getValueUnit() : ""; //$NON-NLS-1$

                    evaluationResult.addNewCompensation();
                    evaluationResult.getCompensation().setValue( BigDecimal.valueOf( value ) );
                    evaluationResult.getCompensation().setUnit( unit );
                }
            }
            else
            {
                //
                // account a penalty
                //
                state.setState( GuaranteeTermStateDefinition.VIOLATED );
                evaluationResult.setType( SLOEvaluationResultType.SLO_VIOLATED );

                if ( guarantee.getBusinessValueList().getPenaltyArray().length > 0 )
                {
                    CompensationType penalty = guarantee.getBusinessValueList().getPenaltyArray( 0 );

                    XmlObject valueExpr = penalty.getValueExpression();
                    String expression = XmlString.Factory.parse( valueExpr.getDomNode() ).getStringValue();

                    Long value = evaluateBusinessValue( expression, jc );
                    String unit = ( penalty.getValueUnit() != null ) ? penalty.getValueUnit() : ""; //$NON-NLS-1$

                    evaluationResult.addNewCompensation();
                    evaluationResult.getCompensation().setValue( BigDecimal.valueOf( value ) );
                    evaluationResult.getCompensation().setUnit( unit );
                }
            }

        }
        else
        {
            String message = Messages.formatString( MSG_SLO_TYPE_ERROR, guarantee.getName() );
            throw new Exception( message );
        }

        return evaluationResult;
    }

    private long evaluateBusinessValue( String exprLit, JexlContext jc ) throws Exception
    {

        try
        {
            Expression expr = jexl.createExpression( exprLit );
            Object exprResult = expr.evaluate( jc );

            if ( exprResult instanceof Double )
            {

                return ( (Double) exprResult ).longValue();
            }

            if ( exprResult instanceof Integer )
            {
                return ( (Integer) exprResult ).intValue();
            }

            if ( exprResult instanceof String )
            {
                return Long.parseLong( (String) exprResult );
            }

            return Long.parseLong( exprResult.toString() );
        }
        catch ( NumberFormatException e )
        {
            throw new Exception( Messages.getString( MSG_VEXPR_TYPE_ERROR ), e );
        }
    }

    /**
     * Selects all guarantees in an agreement properties document.
     * 
     * @param properties
     *            the agreement properties document
     * 
     * @return the selected guarantees
     */
    public GuaranteeTermType[] selectGuaranteeTerms( AgreementPropertiesType properties )
    {
        //
        // Find all guarantee terms, that are defined in this SLA
        //
        XmlObject[] result = properties.getTerms().selectPath( GUARANTEE_TERM_XPATH );

        GuaranteeTermType[] guarantees = new GuaranteeTermType[result.length];
        for ( int i = 0; i < guarantees.length; i++ )
        {
            guarantees[i] = (GuaranteeTermType) result[i];
        }

        return guarantees;
    }

}
