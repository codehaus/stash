package org.codehaus.stash.obie;

import jdbm.btree.BTree;
import jdbm.RecordManager;
import jdbm.helper.LongComparator;
import ognl.Ognl;
import ognl.OgnlException;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id:$
 */
public class Index
{
    private Object parsedExpression;

    /**
     * This is a mapping of a key to oid. The key is generated by applying an expression
     * to the target object.
     */
    private BTree keyMapping;

    /**
     * This is a mapping of oid to the generated key. We store these so that we can remove
     * existing index keys when the expression applied to the target object yields a
     * different key. For example, if the expression used to generate the key is the
     * "name" field and we change the value of the "name" field then we need to remove
     * the existing index key before we update with a new one.
     */
    private BTree oidMapping;

    private RecordManager rm;

    public Index( String expression, Class clazz, RecordManager rm, ObjectDatabase database )
        throws Exception
    {
        this.rm = rm;

        System.out.println( "creating an index using " + clazz );

        keyMapping = BTree.createInstance( rm, database.getComparator( clazz ) );

        oidMapping = BTree.createInstance( rm, new LongComparator() );

        //!!! I'm not sure this really scales here, this is an expression for
        // a particular store, but i need to store them ... fuck

        rm.setNamedObject( expression, keyMapping.getRecid() );

        try
        {
            parsedExpression = Ognl.parseExpression( expression );
        }
        catch ( OgnlException e )
        {
            // do nothing right now
        }
    }

    public long getId( Object key )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // Take a key and lookup the id for the object in question
        // ----------------------------------------------------------------------

        return ((Long)keyMapping.find( key )).longValue();
    }

    public void update( Object target, long id )
        throws Exception
    {
        remove( id );

        insert( target, id );
    }

    public void insert( Object target, long id )
        throws Exception
    {
        Object key = getIndexValue( target );

        Long oid = new Long( id );

        oidMapping.insert( oid, key, false );

        keyMapping.insert( key, oid, false );
    }

    public void remove( long id )
        throws Exception
    {
        // ----------------------------------------------------------------------
        // 1) Retrieve the existing key we have for this target object so that we can
        //    remove the old index value.
        //
        // 2) Remove the index value.
        //
        // 3) Remove the oid index value.
        // ----------------------------------------------------------------------

        Long oid = new Long( id );

        Object existingKey = oidMapping.find( oid );

        keyMapping.remove( existingKey );

        oidMapping.remove( oid );
    }

    private Object getIndexValue( Object target )
        throws Exception
    {
        return Ognl.getValue( parsedExpression, target );
    }

    public BTree getKeyMapping()
    {
        return keyMapping;
    }
}