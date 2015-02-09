/**

Copyright 2013 ATOS SPAIN S.A. 

Licensed under the Apache License, Version 2.0 (the License);
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Authors :
Oriol Collell, Francisco Javier Nieto. Atos Research and Innovation, Atos SPAIN SA
@email francisco.nieto@atosresearch.eu 

**/

package eu.betaas.taas.taasvmmanager.cloudsclients;

/**
 * A service instantiation exception indicates that the described service can not be provider by an
 * infrastructure provider, i.e. since the requested number of resources is not available.
 * 
 * @author owaeld
 * 
 */
public class OCCIClientException extends Exception
{
    private static final long serialVersionUID = 1L;

    /**
     * Creates a new exception with the given message and cause.
     * 
     * @param message
     *            the error message
     * @param cause
     *            the cause of the exception
     */
    public OCCIClientException( String message, Throwable cause )
    {
        super( message, cause );
    }

}
