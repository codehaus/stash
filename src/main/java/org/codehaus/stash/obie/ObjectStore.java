package org.codehaus.stash.obie;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.helper.Tuple;
import jdbm.helper.TupleBrowser;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class ObjectStore
{
    private RecordManager rm;

    private Map indices;

    private File data;

    // always have an id index, just make that inherent

    private ObjectDatabase database;

    public ObjectStore( ObjectDatabase database, Class clazz )
    {
        this.database = database;

        try
        {
            data = new File( database.getDatabaseDirectory(), clazz.getName() );

            rm = RecordManagerFactory.createRecordManager( data.getPath() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }

        indices = new HashMap();
    }

    public void createIndex( Class clazz, String expression )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // We don't want to duplicate indices.
        // ----------------------------------------------------------------------

        if ( indices.containsKey( expression ) )
        {
            return;
        }

        try
        {
            Index index = new Index( expression, clazz, rm, database );

            indices.put( expression, index );
        }
        catch ( IOException e )
        {
            // do nothing right now.
        }
    }

    // ----------------------------------------------------------------------
    // Insert
    // ----------------------------------------------------------------------

    public long insert( Object value )
        throws Exception
    {
        try
        {
            long id = rm.insert( value );

            insertIndices( id, value );

            rm.commit();

            return id;

        }
        catch ( Exception e )
        {
            rm.rollback();

            throw e;
        }
    }

    protected void insertIndices( long id, Object value )
        throws Exception
    {
        for ( Iterator i = indices.values().iterator(); i.hasNext(); )
        {
            Index index = (Index) i.next();

            index.insert( value, id );
        }
    }

    // ----------------------------------------------------------------------
    // Update
    // ----------------------------------------------------------------------

    public void update( String id, Object value )
        throws Exception
    {
        update( Long.parseLong( id ), value );
    }

    public void update( long id, Object value )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Could this be more efficient in terms of only updating the indices
        // if necessary?
        // ----------------------------------------------------------------------

        try
        {
            updateIndices( id, value );

            rm.update( id, value );

            rm.commit();
        }
        catch ( IOException e )
        {
            rm.rollback();
        }
    }

    protected void updateIndices( long id, Object value )
        throws Exception
    {
        for ( Iterator i = indices.values().iterator(); i.hasNext(); )
        {
            Index index = (Index) i.next();

            index.update( value, id );
        }
    }

    // ----------------------------------------------------------------------
    // Delete
    // ----------------------------------------------------------------------

    public void delete( String id )
        throws Exception
    {
        delete( Long.parseLong( id ) );
    }

    public void delete( long id )
        throws Exception
    {
        deleteIndices( id );

        rm.delete( id );

        rm.commit();
    }

    protected void deleteIndices( long id )
        throws Exception
    {
        for ( Iterator i = indices.values().iterator(); i.hasNext(); )
        {
            Index index = (Index) i.next();

            index.remove( id );
        }
    }

    // ----------------------------------------------------------------------
    // Fetch
    // ----------------------------------------------------------------------

    public Object fetch( String id )
        throws Exception
    {
        return rm.fetch( Long.parseLong( id ) );
    }

    public Object fetch( long id )
        throws Exception
    {
        return rm.fetch( id );
    }

    public Object fetch( String expression, Object key )
        throws Exception
    {
        Index i = (Index) indices.get( expression );

        return rm.fetch( i.getId( key ) );
    }

    public Iterator fetchAll( String expression )
        throws Exception
    {
        Index index = (Index) indices.get( expression );

        TupleBrowser browser = index.getKeyMapping().browse();

        return new BrowserIterator( browser );
    }

    class BrowserIterator
        implements Iterator
    {
        TupleBrowser browser;

        Tuple tuple;

        public BrowserIterator( TupleBrowser browser )
        {
            this.browser = browser;
        }

        public boolean hasNext()
        {
            try
            {
                tuple = new Tuple();

                return browser.getNext( tuple );
            }
            catch ( IOException e )
            {
                return false;
            }
        }

        public Object next()
        {
            try
            {
                return fetch( ((Long)tuple.getValue()).longValue() );
            }
            catch ( Exception e )
            {
            }

            return null;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
