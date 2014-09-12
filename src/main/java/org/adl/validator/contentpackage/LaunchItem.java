/*******************************************************************************
** The Advanced Distributed Learning Co-Laboratory (ADL Co-Lab) Hub grants you
** ("Licensee") a non- exclusive, royalty free, license to use and redistribute
** this software in source and binary code form, provided that i) this copyright
** notice and license appear on all copies of the software; and ii) Licensee does
** not utilize the software in a manner which is disparaging to ADL Co-Lab Hub. 
**
** This software is provided "AS IS," without a warranty of any kind. ALL EXPRESS
** OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED
** WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NON-
** INFRINGEMENT, ARE HEREBY EXCLUDED. ADL Co-Lab Hub AND ITS LICENSORS SHALL NOT
** BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
** OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL ADL Co-Lab Hub
** OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
** INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED
** AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
** INABILITY TO USE SOFTWARE, EVEN IF ADL Co-Lab Hub HAS BEEN ADVISED OF THE
** POSSIBILITY OF SUCH DAMAGES. 
******************************************************************************/
package org.adl.validator.contentpackage;

/**
 * This class will store information about each sco to be launched.  The resource
 * identifier and launch line will be stored
 * 
 * @author ADL Technical Team
 *
 */
public class LaunchItem
{
   /**
    * The identifier attribute value of the resource to be launched
    */
   private String mResourceIdentifier;
   
   /**
    * The actual launch line of the resource to be launched 
    */
   private String mLaunchLine;
      
   /**
    * Default Constructor
    * 
    * @param iResourceID - identifier of resource element
    * @param iLaunchLine - launch line of SCO
    */
   public LaunchItem( String iResourceID, String iLaunchLine )
   {
      setResourceIdentifier( iResourceID );
      setLaunchLine( iLaunchLine );
   }

   /**
    * Returns a String representing the SCO launch line
    * 
    * @return a String representing the SCO launch line
    */
   public String getLaunchLine()
   {
      return mLaunchLine;
   }

   /**
    * Sets the value of the launch line
    * 
    * @param iLaunchLine a String containing the SCO launch line
    */
   public void setLaunchLine(String iLaunchLine)
   {
      mLaunchLine = iLaunchLine;
   }

   /**
    * Returns the resource identifier associatd with a given SCO
    * 
    * @return A String representing the identifer of a resource for the given SCO
    */
   public String getResourceIdentifier()
   {
      return mResourceIdentifier;
   }

   /**
    * Sets the value of the resource identifier
    * 
    * @param iResourceIdentifier A String containing the value to which the resource 
    * identifier will be set
    */
   public void setResourceIdentifier(String iResourceIdentifier)
   {
      mResourceIdentifier = iResourceIdentifier;
   }
}
