package org.codehaus.stash.obie;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class IndexDuplicationException
    extends Exception
{
    public IndexDuplicationException( String message )
    {
        super( message );
    }

    public IndexDuplicationException( Throwable cause )
    {
        super( cause );
    }

    public IndexDuplicationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
