package org.codehaus.stash.obie;

import jdbm.helper.IntegerComparator;
import jdbm.helper.StringComparator;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * 
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: ObjectDatabase.java,v 1.1.1.1 2003/11/21 14:26:43 jvanzyl Exp $
 */
public class ObjectDatabase
{
    private Map stores;

    private Map comparators;

    private String databaseDirectory;

    public ObjectDatabase( String databaseDirectory )
    {
        this.databaseDirectory = databaseDirectory;

        stores = new HashMap();

        comparators = new HashMap();

        comparators.put( String.class, new StringComparator() );

        comparators.put( Integer.class, new IntegerComparator() );
    }

    public ObjectStore createStore( Class clazz )
    {
        ObjectStore store = new ObjectStore( this, clazz );

        stores.put( clazz, store );

        return store;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    protected Comparator getComparator( Class clazz )
    {
        Comparator c = (Comparator) comparators.get( clazz );

        return c;
    }

    protected String getDatabaseDirectory()
    {
        return databaseDirectory;
    }
}
