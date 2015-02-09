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
package org.ogf.graap.wsag.api;

import org.apache.xmlbeans.XmlObject;
import org.ogf.graap.wsag.api.exceptions.NegotiationException;
import org.ogf.schemas.graap.wsAgreement.AgreementTemplateType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationContextType;
import org.ogf.schemas.graap.wsAgreement.negotiation.NegotiationOfferType;

/**
 * This interface defines the contract of a concrete Negotiation implementation. <br>
 * A negotiation instance implements the state pattern. its behavior changes depending on the negotiation
 * type, which is either negotiation or re-negotiation.
 * 
 * @author owaeld
 */
public interface Negotiation
{

    /**
     * Returns the context of an negotiation instance. The context is defined when a new negotiation instance
     * is initiated. It defines the type of the negotiation process (negotiation or re-negotiation), the
     * liability, identifies the participating parties, defines constraints on the negotiation process, etc.
     * 
     * @return The context of the negotiation instance.
     */
    NegotiationContextType getNegotiationContext();

    /**
     * Returns the templates for SLAs that are supported by this negotiation instance. Negotiable templates
     * are dynamically generated. If an agreement factory supports negotiation for a specific SLA, it
     * implements a corresponding negotiation strategy. For each SLA where a negotiation strategy is
     * implemented, the corresponding template is returned by the negotiation instance. In case of SLA
     * re-negotiation, the negotiation instance may dynamically generate a set of negotiable templates in
     * order to guide the negotiation participator in the re-negotiation process.
     * 
     * @return a set of negotiable templates
     */
    AgreementTemplateType[] getNegotiableTemplates();

    /**
     * This method returns a list of negotiation offers. These offers represent the offers exchanged in the
     * negotiation process. Only offers that are still valid (e.g. which are not expired) are returned.
     * 
     * @return A set of exchanged negotiation offers.
     */
    NegotiationOfferType[] getNegotiationOffers();

    /**
     * Negotiates acceptable agreement offers with a negotiation participator. This method implements an
     * offer/counter-offer model for bilateral agreement negotiation.
     * 
     * @param quotes
     *            The negotiation quotes represent offers of a negotiation participator. Each negotiation
     *            quote relates to a originating quote in this negotiation, and is based on an agreement
     *            template exposed by the agreement factory associated with this negotiation instance.
     * @param nocriticalExtensions
     *            A negotiation implementation SHOULD obey the non-critical extensions if possible. If the
     *            extensions are not known or the implementation is not willing to support them, they can be
     *            ignored. Alternatively, the negotiation implementation MAY raise an exception.
     * @return Returns a set of negotiation counter offers. Each counter offer must refer to a negotiation
     *         offer passed as input. For each offer, one or more counter offers are created.
     * @throws NegotiationException
     *             indicates an exception during the negotiation process
     */
    NegotiationOfferType[] negotiate( NegotiationOfferType[] quotes, XmlObject[] nocriticalExtensions )
        throws NegotiationException;

    /**
     * Advertises the state change of particular agreement offers to a negotiation participator. This method
     * implements an notification mechanism in bilateral agreement negotiations.
     * 
     * @param quotes
     *            The negotiation quotes represent offers of a negotiation participator. Each negotiation
     *            quote relates to a originating quote in this negotiation, and is based on an agreement
     *            template exposed by the agreement factory associated with this negotiation instance.
     * @param nocriticalExtensions
     *            A negotiation implementation SHOULD obey the non-critical extensions if possible. If the
     *            extensions are not known or the implementation is not willing to support them, they can be
     *            ignored. Alternatively, the negotiation implementation MAY raise an exception.
     * @throws NegotiationException
     *             indicates an exception sending the advertise message
     */
    void advertise( NegotiationOfferType[] quotes, XmlObject[] nocriticalExtensions )
        throws NegotiationException;

    /**
     * terminates a negotiation process
     */
    void terminate();
}
