/*
 * Copyright (c) 2004 Your Corporation. All Rights Reserved.
 */
package org.codehaus.stash.obie;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class IndexCreationException
    extends Exception
{
    public IndexCreationException( String message )
    {
        super( message );
    }

    public IndexCreationException( Throwable cause )
    {
        super( cause );
    }

    public IndexCreationException( String message, Throwable cause )
    {
        super( message, cause );
    }
}
