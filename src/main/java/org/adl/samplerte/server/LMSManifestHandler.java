/*******************************************************************************
**
** Advanced Distributed Learning Co-Laboratory (ADL Co-Lab) Hub grants you 
** ("Licensee") a non-exclusive, royalty free, license to use, modify and 
** redistribute this software in source and binary code form, provided that 
** i) this copyright notice and license appear on all copies of the software; 
** and ii) Licensee does not utilize the software in a manner which is 
** disparaging to ADL Co-Lab Hub.
**
** This software is provided "AS IS," without a warranty of any kind.  ALL 
** EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING 
** ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE 
** OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED.  ADL Co-Lab Hub AND ITS LICENSORS 
** SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF 
** USING, MODIFYING OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES.  IN NO 
** EVENT WILL ADL Co-Lab Hub OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, 
** PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
** INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE 
** THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE 
** SOFTWARE, EVEN IF ADL Co-Lab Hub HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH 
** DAMAGES. 
**
*******************************************************************************/

package org.adl.samplerte.server;

import org.adl.parsers.dom.DOMTreeUtility;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.adl.samplerte.util.RTEFileHandler;
import org.adl.sequencer.ADLSeqUtilities;
import org.adl.sequencer.ADLSequencer;
import org.adl.sequencer.SeqActivityTree;
import org.adl.sequencer.ADLLaunch;
import org.adl.sequencer.SeqNavRequests;

import java.util.Vector;
import java.util.logging.Logger;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.sql.*;


import org.adl.util.decode.decodeHandler;
import org.adl.validator.contentpackage.CPValidator;
import org.adl.validator.contentpackage.ManifestHandler;
import org.adl.validator.contentpackage.LaunchData;
import org.adl.validator.ADLValidatorOutcome;
import arta.common.Constants;
import arta.common.logic.sql.ConnectionPool;
import arta.common.logic.util.Log;
import arta.common.logic.util.StringTransform;
import arta.common.logic.db.Varchar;

/**
 * This class contains methods to parse an imsmanifest.xml file, process a 
 * content package,and simplify the access of data in a DOM tree that 
 * corresponds to the 
 * imsmanifest.xml file from a PIF.  <br><br>
 * 
 * <strong>Filename:</strong> LMSManifestHandler.java<br><br>
 * 
 * <strong>Description:</strong> <br><br>
 * This class contains methods used by the Sample Run-Time Environment to parse
 * the imsmanifest.xml file and process the content package using the ADL SCORM
 * Validator.  It also contains methods used by both the Sample Run-Time 
 * Environment and the ADL Sequencer to gain access to information in the 
 * imsmanifest.xml file.
 * <br><br>
 * 
 * <strong>Design Issues:</strong><br><br>
 * 
 * <strong>Implementation Issues:</strong><br><br>
 * 
 * <strong>Known Problems:</strong> <br><br>
 * 
 * <strong>Side Effects:</strong> <br><br>
 * 
 * <strong>References:</strong> SCORM <br><br>
 * 
 * @author ADL Technical Team
 */

public class LMSManifestHandler implements Serializable{

    //String httpFolderName = "http://demo.arta.kz/books/";
    String httpFolderName = "/books/";

   /**
    * This is the DOM structure that will be returned with the 
    * ADLValidatorOutcome class.  It will contain all of the 
    * information contained in the imsmanifest file and will serve as a means
    * of accessing that information.
    */
   protected Document mDocument;

   /**
    * This is the title of the course.  It will be populated with the value of
    * the title attribute of an organization element.
    */
   protected String mCourseTitle;

   /**
    * This is the ID of the course.  It will be given the value of the
    * nextCourseID stored in the Application Data in the SampleRTE database.
    */
   protected int mCourseID;

   /**
    * This is the path that will be used when copying files.
    */
   protected String mWebPath;

   /**
    * This is a list of the &lt;organization&gt; elements in the manifest.
    */
   protected Vector mOrganizationList;

   /**
    * This vector will consist of LaunchData objects containing information
    * from the organization and resource elements which will be stored in the
    * SampleRTE database.
    */
   protected Vector mLaunchDataList;
   
   /**
    * Logger object used for debug logging.
    */
   private Logger mLogger = Logger.getLogger("org.adl.util.debug.samplerte");

   /**
    * The manifest element of the imsmanifest.xml file.
    */
   private Node mManifest;

   /**
    * The location of the schema xsd files.
    */                                         
   private String mXSDLocation;

   /**
    * The result of the file copy.
    */
   private boolean mFileCopyResult = true;
   
   /**
    * The result of the zip file extraction.
    */
   private boolean mZipExtractionResult = true;
   
   /**
    * Default constructor method which initializes member variables
    * 
    * @param iXSDLocation
    *           The location where the XSDs can be found for use 
    *           during validation.
    * 
    */ 
   public LMSManifestHandler( String iXSDLocation )
   {
      mDocument = null;
      mOrganizationList = new Vector();
      mLaunchDataList = new Vector();
      mCourseTitle = "";
      mCourseID = 0;
      mManifest = null;
      mXSDLocation = iXSDLocation;
      mWebPath = "";
   }

   /**
    * Sets up the String of schema locations
    *
    * @param iExtendedSchemaLocations
    *               The schema locations extended by the vendor.
    *               <br><br>
    *
    *               <strong>Implementation Issues:</strong><br>
    *               The 80th column Java Coding Standard is not followed here
    *               due to the need to represent an exact string for schema
    *               locations.<br><br>
    *
    * @return String representing all of the schema locations needed.
    */
   private String getSRTESchemaLocations( String iExtendedSchemaLocations )
   {
      mLogger.entering("---LMSManifestHandler", "getSRTESchemaLocations()");
      String result = new String();
      
      String xsdLocation = mXSDLocation;
      xsdLocation = "file:///" + xsdLocation + File.separator + "xml" + 
                    File.separator + "xsd" + File.separator;

      
      xsdLocation = xsdLocation.replaceAll( " ", "%20");

      xsdLocation = xsdLocation.replace( '\\', '/');

      mLogger.info("+++++++++++xsdLocation IS: " + xsdLocation +
                        "+++++++++++++++++++");

      result = "http://www.imsglobal.org/xsd/imscp_v1p1 " +
                xsdLocation +"imscp_v1p1.xsd " +
               "http://www.w3.org/XML/1998/namespace " +
                "xml.xsd " +
               "http://www.adlnet.org/xsd/adlcp_v1p3 " +
                xsdLocation + "adlcp_v1p3.xsd " +
               "http://www.adlnet.org/xsd/adlseq_v1p3 " +
                xsdLocation + "adlseq_v1p3.xsd " +
               "http://www.adlnet.org/xsd/adlnav_v1p3 " +
                xsdLocation + "adlnav_v1p3.xsd " +
               "http://www.imsglobal.org/xsd/imsss " +
                xsdLocation + "imsss_v1p0.xsd " +
               "http://ltsc.ieee.org/xsd/LOM " +
               xsdLocation + "lomStrict.xsd";


      mLogger.info("+++++++++++RESULT IS: " + result + 
                         "+++++++++++++++++++");
      if ( ! iExtendedSchemaLocations.equals("") )
      {
         result = result + " " + iExtendedSchemaLocations;
      }

      return result;
   }


    /**
    * Uses the CPValidator and ADLValidatorOutcome classes of the
    * <code>ADLValidator</code> to parse a manifest file and to create the
    * corresponding DOM tree.  This tree is then traversed (with the use of
    * additional <code>LMSManifestHandler</code> methods, appropriate
    * database inserts are performed, a template activity tree is created
    * using the ADLSeqUtilities class, and serialized files are created for
    * each organization element in the manfest.
    * <br><br>
    *
    * <b> Modified by Topa</b>
    * Метод парсит manifest file и вносит в бд инфрмацию о распарcенном курсе
    *
    * @param iFilePath - A string representing the path of the file to be
    *                   validated.
    * @param iValidate - A boolean value representing whether or not validation
    *                    should be performed.
    *
    * @return An ADLValidator object containing the DOM object as well as
    * validation results.
    */
    public ADLValidatorOutcome processPackage( String iFilePath, boolean iValidate,
                                               String courseIdentifier, Connection con, int resourceID) {

        String iExtendedSchemaLocations = "";
        RTEFileHandler fileHandler = new RTEFileHandler();
      
        mLogger.entering("---LMSManifestHandler", "processManifest()");

        String SRTE_EnvironmentVariable = mXSDLocation;   //E:\Development\Services\tomcat\webapps\adl

        CPValidator scormvalidator = new CPValidator( SRTE_EnvironmentVariable, courseIdentifier);
        scormvalidator.setSchemaLocation( getSRTESchemaLocations(
                                     iExtendedSchemaLocations ) );

        //Turn validation on or off
        scormvalidator.setPerformValidationToSchema( iValidate );
        scormvalidator.validate( iFilePath, "pif", "contentaggregation", false );
      
        mZipExtractionResult = scormvalidator.getZipExtractionResult();

        // retrieve object that stores the results of the validation activites
        ADLValidatorOutcome outcome = scormvalidator.getADLValidatorOutcome();

        Statement st = null;
        ResultSet res = null;

        try {

            mLogger.info( "Document parsing complete." );

            // Проверка на корректность загруженного курса
            if ( (!iValidate && outcome.getDoesIMSManifestExist()  &&
               outcome.getIsWellformed() && outcome.getIsValidRoot()) || 
               (iValidate && 
               (outcome.getDoesIMSManifestExist() && 
                    outcome.getIsValidRoot() &&
                    outcome.getIsWellformed() &&
                    outcome.getIsValidToSchema() &&
                    outcome.getIsValidToApplicationProfile() &&
                    outcome.getDoRequiredCPFilesExist() &&
                    mZipExtractionResult )) ) {


                st = con.createStatement();

                outcome.rollupSubManifests( false );

                mDocument = outcome.getDocument();

                mLaunchDataList = scormvalidator.getLaunchData(mDocument, false, false);

                this.mManifest = mDocument.getDocumentElement();

                // get information from manifest and update database
                mOrganizationList = this.getOrganizationList();

                //todo Запись в БД информации о распрасенном курсе

                updateDB(courseIdentifier, st, resourceID);

                Vector resources = ManifestHandler.getSSPResourceList( mManifest );
                //todo запись в БД информации о bucket-ах
                updateSSPDB(resources , st);


            } else {

                if (!(outcome.getIsWellformed())) {
                    mLogger.info("NOT WELL FORMED!!!");
                }
                if (!(outcome.getIsValidRoot())) {
                    mLogger.info("INVALID ROOT!!!");
                }
                if (!(outcome.getIsValidToSchema())) {
                    mLogger.info("NOT VALID TO SCHEMA!!!");
                }
                if (!(outcome.getIsValidToApplicationProfile())) {
                    mLogger.info("NOT VALID TO APP PROFILE!!!");
                }
                if (!(outcome.getDoRequiredCPFilesExist())) {
                    mLogger.info("REQUIRED FILES DO NOT EXIST!!!");
                }

                mLogger.info("-----NOT CONFORMANT!!!----");
            }

        } catch(Exception e) {

            Log.writeLog(e);

        } finally{
            try{
                if (st != null) st.close();
            } catch(Exception exc){
                Log.writeLog(exc);
            }
        }

        //scormvalidator.cleanImportDirectory( mXSDLocation + File.separator + "PackageImport" );
        scormvalidator.cleanImportDirectory( Constants.getCousreUnpackTemporaryDirectory(courseIdentifier), true);

        // Удаление временных файлов
        fileHandler.deleteTempUloadFiles(courseIdentifier);

        mLogger.exiting( "---LMSManifestHandler", "processManifest()" );
        //  Return boolean signifying whether or not the parsing was successful
        return outcome;
    }


   /**
    * This method will copy a course from the specified directory where it
    * already exists, to a new specified directory where it is to be copied to.
    * 
    * @param iInFilePath - The path of the current file or directory that needs
    *                      to be copied.
    * @param iOutFilePath - The path of the directory that the file is to be
    *                       copied to.
    */
   private boolean copyCourse( String iInFilePath, String iOutFilePath ) 
   {
       
      boolean result = true;
      try
      {
         String inDirName = iInFilePath;
         inDirName.replace('/',java.io.File.separatorChar);

         File tempFile = new File(inDirName);
         File[] fileNames = tempFile.listFiles();

         String outDirName = iOutFilePath;

         outDirName = outDirName.replace('/',java.io.File.separatorChar);
         File tempDir = new File(outDirName);
         tempDir.mkdirs();
         
         FileInputStream fi = null;
         FileOutputStream fo = null;
         BufferedInputStream in = null;
         BufferedOutputStream out = null;
           
         for ( int i=0; i < fileNames.length; i++ )
         {
            String tempString = outDirName + java.io.File.separatorChar + 
                                                fileNames[i].getName();
            if ( fileNames[i].isDirectory() )
            {
               File dirToCreate = new File(tempString);
               dirToCreate.mkdirs();
               result = copyCourse( fileNames[i].getAbsolutePath(), tempString );
            }
            else
            {
               fi = new FileInputStream(fileNames[i]);
               fo = new FileOutputStream(tempString);
               in = new BufferedInputStream( fi );
               out = new BufferedOutputStream( fo );
               int c;
               while ((c = in.read()) != -1) 
               {
                  out.write(c);
               }

               in.close();
               fi.close();
               out.close();
               fo.close();
            }
         }
      }
      catch ( IOException ioe )
      {
         result = false;
         Log.writeLog(ioe);
      }
      return result;
         
   }

   /**
    *
    * This method will return the course ID.
    * 
    * @return A string containing the course ID.       
    */
   public String getCourseID()
   {
      return this.mCourseID + "";
   }

   /**
    * This method gets a list of the &lt;organization&gt; elements in the manifest.
    * 
    * @return A vector containing the &ltorganization&gt nodes.
    */
   protected Vector getOrganizationList()
   {
      return ManifestHandler.getOrganizationNodes(mManifest, false);
   }

   /**
    * This method gets the sequencingCollection node from the DOM tree.
    * 
    * @return The sequencingCollection node from the DOM tree.
    */
   public Node getSeqCollection()
   {
      return DOMTreeUtility.getNode( mManifest, "sequencingCollection" );
   }

   /**
    * This method determines if a package processing error has occurred.
    * 
    * @return A boolean containing the status of package processing.
    */
   public boolean getPackageProcessingStatus()
   {      
      return mFileCopyResult && mZipExtractionResult;
   }
   
   /**
    * This method sets mCourseTitle to the value passed in with courseTitle.
    * 
    * @param iCourseTitle The title of the course.
    */
   public void setCourseName( String iCourseTitle )
   {
      this.mCourseTitle = iCourseTitle;
   }

   /**
    * This method sets the web path.
    * 
    * @param iWebPath The web path.
    */
   public void setWebPath( String iWebPath )
   {
      this.mWebPath = iWebPath;
      mLogger.info("***MWEBPATH IS " + mWebPath + "***");
   }
   
    /**
    * This method takes the relevant information from the
    * populated parser structure and writes it to a related
    * database.  This is done so that the JSP coding is
    * more straight forward.
     * @param courseIdentifier
     * @param st
     * @throws Exception
     */
    protected void updateDB(String courseIdentifier, Statement st, int resourceID) throws Exception{

        mLogger.entering("---LMSManifestHandler", "updateDB()  *********");
        SeqActivityTree mySeqActivityTree;

        ResultSet res = null;

        try {

            // loop through all organizations performing the database updates and
            // sequencing actions for each one.
            for ( int j=0; j < mOrganizationList.size(); j++ ) {

                Node tempOrganization = (Node) mOrganizationList.elementAt(j);
                String tempOrgIdentifier = DOMTreeUtility.getAttributeValue (tempOrganization, "identifier" );
                Node tempOrgTitleNode = DOMTreeUtility.getNode ( tempOrganization, "title" );
                String tempOrgTitle = DOMTreeUtility.getNodeValue ( tempOrgTitleNode );

                StringTransform trsf = new StringTransform();
                
                st.execute("INSERT INTO courseinfo (coursetitle, active, created, resourceID) " +
                        " VALUES ('"+ trsf.getDBStringSCORM(tempOrgTitle, Varchar.SCORM_TITLE) +"' , " +
                        " true, current_timestamp, " + resourceID + ")  ", Statement.RETURN_GENERATED_KEYS);
                res = st.getGeneratedKeys();
                if (res.next())
                    mCourseID = res.getInt(1);

                // Create a temporary LaunchData object
                LaunchData ld = new LaunchData();
                
                // Loop through each item in the course adding it to the database
                for ( int i = 0; i < mLaunchDataList.size(); i++ ) {
                    ld = (LaunchData)mLaunchDataList.elementAt(i);

                    // If the organization identifier of the current launch data
                    // equals the identifier of the current entry of the
                    // organization list, perform the database updates.
                    if (ld.getOrganizationIdentifier().equals(tempOrgIdentifier)) {

                        // Decode the URL before inserting into the database
                        decodeHandler decoder = new decodeHandler( ld.getLocation(), "UTF-8");
                        decoder.decodeName();

                        String alteredLocation = new String();
                
                        //If it's blank or it's external, don't concatenate to the
                        //local Web root.
                        if ((ld.getLocation().equals(""))||
                         (ld.getLocation().startsWith("http://"))||
                         (ld.getLocation().startsWith("https://"))) {

                            alteredLocation = decoder.getDecodedFileName();
                            if ( !(ld.getParameters().equals("")) &&
                                    !(ld.getParameters() == null ) ) {
                                alteredLocation = addParameters(alteredLocation,
                                                        ld.getParameters());
                            }

                        } else {
                            //todo когда нить заменить books на константу 
                            // Create the altered location (with decoded url)
                            alteredLocation =  httpFolderName + mCourseID +"/"
                                          + decoder.getDecodedFileName();

                            if ( !(ld.getParameters().equals("")) && !(ld.getParameters() == null )){
                                alteredLocation = addParameters(alteredLocation,
                                                        ld.getParameters());
                            }

                        }
                     
                        // Insert into the database

                    st.execute("INSERT INTO iteminfo (courseID, organizationidentifier,  " +
                            " itemidentifier, type, title,  " +
                            " launch, parameterstring, " +
                            " datafromlms, timelimitaction, " +
                            " minnormalizedmeasure, attemptabsolutedurationlimit, " +
                            " completionthreshold, next, previous, isexit, exitall, " +
                            " abandon, resourceidentifier, suspend) " +
                            " VALUES " +
                            " (" + mCourseID +", " +
                            " '" + trsf.getDBStringSCORM(ld.getOrganizationIdentifier(), Varchar.NAME) + "', " +
                            " '" + trsf.getDBStringSCORM(ld.getItemIdentifier(), Varchar.NAME) + "', " +
                            " '" + trsf.getDBStringSCORM(ld.getSCORMType(), Varchar.NAME) + "', " +
                            " '" + trsf.getDBStringSCORM(ld.getItemTitle(), Varchar.NAME) + "', " +
                            " '" + trsf.getDBStringSCORM(alteredLocation, Varchar.NAME)+ "', " +
                            " '" + trsf.getDBStringSCORM(ld.getParameters(), Varchar.NAME)+ "', " +
                            " '" + trsf.getDBStringSCORM(ld.getDataFromLMS(), Varchar.NAME) + "', " +
                            " '" + trsf.getDBStringSCORM(ld.getTimeLimitAction(), Varchar.NAME) + "' , " +
                            " '" + trsf.getDBStringSCORM(ld.getMinNormalizedMeasure(), 50) + "', " +
                            " '" + trsf.getDBStringSCORM(ld.getAttemptAbsoluteDurationLimit(), Varchar.NAME) + "' , " +
                            " '" + trsf.getDBStringSCORM(ld.getCompletionThreshold(), Varchar.NAME) + "',  " +
                            " " + ld.getContinue() + ", " +
                            " " + ld.getPrevious() + ", " +
                            " " + ld.getExit() + ", " +
                            " " + ld.getExitAll() + ", " +
                            " " + ld.getAbandon() + ", " +
                            " '" + trsf.getDBString(ld.getResourceIdentifier()) + "', " +
                            " " + ld.getSuspendAll() + " )");


                    }
                }

                //todo this is a directory where files are copied
                //Copy course files from the temp directory and serialize
                // String copyInDirName = mWebPath + "PackageImport";
                String copyInDirName = Constants.getCousreUnpackTemporaryDirectory(courseIdentifier);
                String copyOutDirName = Constants.getCoursesStoreDirectory() +
                                         java.io.File.separatorChar + mCourseID;

                mFileCopyResult = copyCourse( copyInDirName, copyOutDirName );
            
                // If files failed to copy, delete the course from the database
                if ( !mFileCopyResult ) {
                    throw new IOException("Files could not be copied!");
                }
            
                //create a SeqActivityTree and serialize it
                mySeqActivityTree = new SeqActivityTree();

                String tempObjectivesGlobalToSystem = DOMTreeUtility.
                getAttributeValue( tempOrganization,"objectivesGlobalToSystem" );

                // include sequencing collection as a parameter as well as
                // the organization node.
                mySeqActivityTree = ADLSeqUtilities.buildActivityTree(tempOrganization,
                                                                  getSeqCollection());

                if( tempObjectivesGlobalToSystem.equals("false") ) {
                    mySeqActivityTree.setScopeID(mCourseID + "");
                }

                mySeqActivityTree.setCourseID(mCourseID + "");

                // мой костыль, преобразует строки дерева дл винды, так как у нее после парскра хз какая кодировка
                String osname = System.getProperty("os.name");
                if (osname != null && osname.toLowerCase().indexOf("windows") >= 0){
                    mySeqActivityTree.getMRoot().deleteBadBytes();
                }

                String serializeFileName = Constants.getCoursesStoreDirectory() + File.separator + mCourseID
                                                 + File.separator + "serialize.obj";
                java.io.File serializeFile = new java.io.File(serializeFileName);
            
                FileOutputStream outFile = new FileOutputStream(serializeFile);
                ObjectOutputStream s = new ObjectOutputStream(outFile);
                s.writeObject(mySeqActivityTree);
                s.flush();
                s.close();
                outFile.close();
            
                /////////////////////////////////////////////////////////
                ADLSequencer theSequencer = new ADLSequencer();
                ADLLaunch launch = new ADLLaunch();

                theSequencer.setActivityTree(mySeqActivityTree);

                launch = theSequencer.navigate(SeqNavRequests.NAV_NONE);

                st.execute("UPDATE courseinfo SET start=" + launch.mNavState.mStart + ", " +
                        " TOC=" + (launch.mNavState.mTOC != null) + " " +
                        " WHERE courseID="+mCourseID+" ");
            }

        } catch ( Exception e) {
            throw new Exception(e);
        }
    }


   /**
    * This method adds parameters to a URL using the following algorithm 
    * from the SCORM CAM Version 1.3:
    * While first char of parameters is in "?&"
    *    Clear first char of parameters
    * If first char of parameters is "#"
    *    If URL contains "#" or "?"
    *        Discard parameters
    *        Done processing URL
    * If URL contains "?"
    *    Append "&" to the URL
    * Else
    *    Append "?" to the URL
    * Append parameters to URL
    *
    * 
    * @param iURL  URL of content
    * 
    * @param iParameters  Parameters to be appended
    * 
    * @return URL with added parameters
    */                                                      
   public String addParameters(String iURL, String iParameters)
   {
       if ( (iURL.length() == 0) || (iParameters.length() == 0) )
       {
          return iURL;
       }
        while ( (iParameters.charAt(0) == '?') || 
                                    (iParameters.charAt(0) == '&') )
        {
           iParameters = iParameters.substring(1);
        }
        if ( iParameters.charAt(0) == '#' )
        {
             if ( (iURL.indexOf('#') != -1) || (iURL.indexOf('?') != -1) )
             {
                return iURL;
             }
        }
        if ( iURL.indexOf('?') != -1 )
        {   
           iURL = iURL + '&';
        }
        else
        {
           iURL = iURL + '?';
        }
        iURL = iURL + iParameters;

        return iURL;
   }



    /**
    *
    * This method catches all bucket related info within the manifest and
    * stores to the SSP Database.<br>
    *
    * todo Переложить этот метод на ДБ MySQL
    *
    * @param iResources list of resources from the manifest
    * @param st - java.sql.Statement
    * @throws Exception При неудачном сохранении информации
    */
    private void updateSSPDB( Vector iResources, Statement st ) throws Exception{

        String scoID = null;
        String resourceID = null;
        String courseID = null;
        String bucketID = null;
        String bucketType = null;
        String persistence = "learner";
        String min = null;
        String requested = null;
        String reducible = "false";
        String temp = null;

        try {

            // Looping over resource
            for ( int i = 0; i < iResources.size(); i++) {

                Node tempResource = (Node)iResources.elementAt(i);
                Vector children = DOMTreeUtility.getNodes(tempResource, "bucket");

                // Looping over each child of this instance of resource
                for (int j = 0; j < children.size(); j++) {

                    Node tempBucket = (Node)children.elementAt(j);
                    Node tempSize = DOMTreeUtility.getNode(tempBucket, "size");
                    resourceID = DOMTreeUtility.getAttributeValue(tempResource,
                                                         "identifier" );
                    // Loop through to find the SCOID
                    for (int k = 0; k < mOrganizationList.size(); k++) {
                        Node tempOrg = (Node)mOrganizationList.elementAt(k);
                        Vector orgList = DOMTreeUtility.getNodes(tempOrg, "item");

                        for (int n = 0; n < orgList.size(); n++) {
                            Node tempItem = (Node)orgList.elementAt(n);
                            String tempRef = DOMTreeUtility.getAttributeValue(tempItem, "identifierref");
                            if (tempRef.equals(resourceID)){
                                scoID = DOMTreeUtility.getAttributeValue(tempItem, "identifier");
                                break;
                            }
                        }
                    }

                    courseID = getCourseID();

                    bucketID = DOMTreeUtility.getAttributeValue(tempBucket, "bucketID");
                    bucketType = DOMTreeUtility.getAttributeValue(tempBucket,
                                                              "bucketType");
                    temp = DOMTreeUtility.getAttributeValue(tempBucket, "persistence");
                    persistence = (temp == null || temp.equals("")) ? "learner" : temp;
                    min = DOMTreeUtility.getAttributeValue(tempSize, "minimum");
                    requested = DOMTreeUtility.getAttributeValue(tempSize, "requested");
                    temp = DOMTreeUtility.getAttributeValue(tempSize, "reducible");
                    reducible = (temp == null || temp.equals("")) ? "false" : temp ;
      
                    StringTransform trsf = new StringTransform();

                    st.execute("INSERT INTO ssp_buckettbl (ScoID, CourseID, BucketID, BucketType," +
                            " Persistence, Min, Requested, Reducible) " +
                            " VALUES('" + trsf.getDBString(scoID) + "', " +
                            " '" + courseID + "', " +
                            " '" + trsf.getDBString(bucketID) + "', " +
                            " '" + trsf.getDBString(bucketType)+ "', " +
                            " '" + trsf.getDBString(persistence) + "', " +
                            " '" + trsf.getDBString(min) + "', " +
                            " '" + trsf.getDBString(requested) + "', " +
                            " '" + trsf.getDBString(reducible)+ "') ");
                }
            }
        } catch ( Exception e ) {
            Exception exc = new Exception(e);
            throw exc;
        }
    }

}
