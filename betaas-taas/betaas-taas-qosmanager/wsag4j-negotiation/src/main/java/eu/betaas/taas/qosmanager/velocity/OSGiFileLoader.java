package eu.betaas.taas.qosmanager.velocity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.io.UnicodeInputStream;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.apache.velocity.util.StringUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;


public class OSGiFileLoader extends ResourceLoader {

	private static final Logger log = Logger.getLogger( OSGiFileLoader.class );
    /**
     * The paths to search for templates.
     */
    private List paths = new ArrayList();

    /**
     * Used to map the path that a template was found on
     * so that we can properly check the modification
     * times of the files. This is synchronizedMap
     * instance.
     */
    private Map templatePaths = Collections.synchronizedMap(new HashMap());

    /** Shall we inspect unicode files to see what encoding they contain?. */
    private boolean unicode = false;

    /**
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#init(org.apache.commons.collections.ExtendedProperties)
     */
    public void init( ExtendedProperties configuration)
    {
    	
        if (log.isTraceEnabled())
        {
            log.trace("OSGIFileResourceLoader : initialization starting.");
        }

        paths.addAll( configuration.getVector("path") );

        // unicode files may have a BOM marker at the start, but Java
        // has problems recognizing the UTF-8 bom. Enabling unicode will
        // recognize all unicode boms.
        unicode = configuration.getBoolean("unicode", false);

        if (log.isDebugEnabled())
        {
            log.debug("OSGIDo unicode file recognition:  " + unicode);
        }

        if (log.isDebugEnabled())
        {
            // trim spaces from all paths
            StringUtils.trimStrings(paths);

            // this section lets tell people what paths we will be using
            int sz = paths.size();
            for( int i=0; i < sz; i++)
            {
                log.debug("OSGIFileResourceLoader : adding path '" + (String) paths.get(i) + "'");
            }
            log.trace("OSGIFileResourceLoader : initialization complete.");
        }
    }

    /**
     * Get an InputStream so that the Runtime can build a
     * template with it.
     *
     * @param templateName name of template to get
     * @return InputStream containing the template
     * @throws ResourceNotFoundException if template not found
     *         in the file template path.
     */
    public InputStream getResourceStream(String templateName)
        throws ResourceNotFoundException
    {
        /*
         * Make sure we have a valid templateName.
         */

        if (org.apache.commons.lang.StringUtils.isEmpty(templateName))
        {
            /*
             * If we don't get a properly formed templateName then
             * there's not much we can do. So we'll forget about
             * trying to search any more paths for the template.
             */
        	log.debug("Not found");
        	throw new ResourceNotFoundException(
                "Need to specify a file name or file path!");
            
        }


        String template = StringUtils.normalizePath(templateName);
 
        if ( template == null || template.length() == 0 )
        {
            String msg = "File resource error : argument " + template +
                " contains .. and may be trying to access " +
                "content outside of template root.  Rejected.";

            log.error("FileResourceLoader : " + msg);
            log.error("Not found 2");

            throw new ResourceNotFoundException ( msg );
        }


        int size = paths.size();
        for (int i = 0; i < size; i++)
        {
            String path = (String) paths.get(i);
            InputStream inputStream = null;


            
            inputStream = findTemplate(path, template);

            if (inputStream != null)
            {
                /*
                 * Store the path that this template came
                 * from so that we can check its modification
                 * time.
                 */

                templatePaths.put(templateName, path);
                return inputStream;
            }
        }
        
        log.error("End of function");

        /*
         * We have now searched all the paths for
         * templates and we didn't find anything so
         * throw an exception.
         */
         throw new ResourceNotFoundException("FileResourceLoader : cannot find " + template);
    }

    /**
     * Overrides superclass for better performance.
     * @since 1.6
     */
    public boolean resourceExists(String name)
    {
        if (name == null)
        {
            return false;
        }
        name = StringUtils.normalizePath(name);
        if (name == null || name.length() == 0)
        {
            return false;
        }

        int size = paths.size();
        for (int i = 0; i < size; i++)
        {
            String path = (String)paths.get(i);
            try
            {
                File file = getFile(path, name);
                if (file.canRead())
                {
                    return true;
                }
            }
            catch (Exception ioe)
            {
                String msg = "Exception while checking for template " + name;
                log.debug(msg, ioe);
            }
        }
        return false;
    }

    /**
     * Try to find a template given a normalized path.
     *
     * @param path a normalized path
     * @param template name of template to find
     * @return InputStream input stream that will be parsed
     * @throws Exception 
     *
     */
    private InputStream findTemplate(final String path, final String template)
    {
    	// TODO manage errors
   		InputStream intemplate = OSGiFileLoader.class.getResourceAsStream(template);
   		return intemplate;
    	/*
        try
        {
        	log.debug("FIND2");
        	log.debug(path);
        	log.debug(template);
        	
        	log.debug(template);
        	InputStream intemplate = OSGiFileLoader.class.getResourceAsStream(template);
        	BufferedReader reader = new BufferedReader(new InputStreamReader(intemplate));
        	String inputLine;
            while ((inputLine = reader.readLine()) != null)
            	log.debug(inputLine);
        	
            
            
            File file = getFile(path,template);

            if (file.canRead())
            {
            	log.debug("CAN READ");
            	
                FileInputStream fis = null;
                try
                {
                    fis = new FileInputStream(file.getAbsolutePath());

                    if (unicode)
                    {
                        UnicodeInputStream uis = null;

                        try
                        {
                            uis = new UnicodeInputStream(fis, true);

                            log.debug("UNICODE");
                            
                            if (log.isDebugEnabled())
                            {
                                log.debug("File Encoding for " + file + " is: " + uis.getEncodingFromStream());
                            }

                            return new BufferedInputStream(uis);
                        }
                        catch(IOException e)
                        {
                            closeQuiet(uis);
                            throw e;
                        }
                    }
                    else
                    {
                        return new BufferedInputStream(fis);
                    }
                }
                catch (IOException e)
                {
                    closeQuiet(fis);
                    throw e;
                }
            }
            else
            {
                return null;
            }
        }
        catch(FileNotFoundException fnfe)
        {

            return null;
        }*/
    }

    private void closeQuiet(final InputStream is)
    {
        if (is != null)
        {
            try
            {
                is.close();
            }
            catch(IOException ioe)
            {
                // Ignore
            }
        }
    }

    /**
     * How to keep track of all the modified times
     * across the paths.  Note that a file might have
     * appeared in a directory which is earlier in the
     * path; so we should search the path and see if
     * the file we find that way is the same as the one
     * that we have cached.
     * @param resource
     * @return True if the source has been modified.
     */
    public boolean isSourceModified(Resource resource)
    {
        /*
         * we assume that the file needs to be reloaded;
         * if we find the original file and it's unchanged,
         * then we'll flip this.
         */
        boolean modified = true;

        String fileName = resource.getName();
        String path = (String) templatePaths.get(fileName);
        File currentFile = null;

        for (int i = 0; currentFile == null && i < paths.size(); i++)
        {
            String testPath = (String) paths.get(i);
            File testFile = getFile(testPath, fileName);
            if (testFile.canRead())
            {
                currentFile = testFile;
            }
        }
        File file = getFile(path, fileName);
        if (currentFile == null || !file.exists())
        {
            /*
             * noop: if the file is missing now (either the cached
             * file is gone, or the file can no longer be found)
             * then we leave modified alone (it's set to true); a
             * reload attempt will be done, which will either use
             * a new template or fail with an appropriate message
             * about how the file couldn't be found.
             */
        }
        else if (currentFile.equals(file) && file.canRead())
        {
            /*
             * if only if currentFile is the same as file and
             * file.lastModified() is the same as
             * resource.getLastModified(), then we should use the
             * cached version.
             */
            modified = (file.lastModified() != resource.getLastModified());
        }

        /*
         * rsvc.debug("isSourceModified for " + fileName + ": " + modified);
         */
        return modified;
    }

    /**
     * @see org.apache.velocity.runtime.resource.loader.ResourceLoader#getLastModified(org.apache.velocity.runtime.resource.Resource)
     */
    public long getLastModified(Resource resource)
    {
        String path = (String) templatePaths.get(resource.getName());
        File file = getFile(path, resource.getName());

        if (file.canRead())
        {
            return file.lastModified();
        }
        else
        {
            return 0;
        }
    }


    /**
     * Create a File based on either a relative path if given, or absolute path otherwise
     */
    private File getFile(String path, String template)
    {

        File file = null;

        if("".equals(path))
        {
            file = new File( template );
        }
        else
        {
            /*
             *  if a / leads off, then just nip that :)
             */
            if (template.startsWith("/"))
            {
                template = template.substring(1);
            }

            file = new File ( path, template );
        }

        return file;
    }
}
