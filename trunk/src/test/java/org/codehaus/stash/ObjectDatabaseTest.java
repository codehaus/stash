package org.codehaus.stash;

import junit.framework.TestCase;

import java.io.File;
import java.util.Iterator;

import org.codehaus.stash.obie.ObjectDatabase;
import org.codehaus.stash.obie.ObjectStore;

/**
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 * @version $Id: ObjectDatabaseTest.java,v 1.1.1.1 2003/11/21 14:26:43 jvanzyl Exp $
 */
public class ObjectDatabaseTest
    extends TestCase
{
    public void test()
        throws Exception
    {
        File f = new File( System.getProperty( "basedir" ), "target" );

        ObjectDatabase db = new ObjectDatabase( f.getPath() );

        ObjectStore store = db.createStore( Person.class );

        // ----------------------------------------------------------------------
        // Create an index for the "name" field
        // ----------------------------------------------------------------------

        store.createIndex( String.class, "name" );

        // ----------------------------------------------------------------------
        // Insert some objects
        // ----------------------------------------------------------------------

        store.insert( new Person( "bob", 29 ) );

        store.insert( new Person( "jason", 31 ) );

        long id = store.insert( new Person( "michal", 28 ) );

        // ----------------------------------------------------------------------
        // Test that we get the correct objects back
        // ----------------------------------------------------------------------

        Person p;

        p = (Person) store.fetch( "name", "bob" );

        assertEquals( "bob", p.getName() );

        p = (Person) store.fetch( "name", "jason" );

        assertEquals( "jason", p.getName() );

        p = (Person) store.fetch( "name", "michal" );

        assertEquals( "michal", p.getName() );

        assertEquals( 28, p.getAge() );

        // ----------------------------------------------------------------------
        // A change that doesn't involve a change to an indexed field
        // ----------------------------------------------------------------------

        p.setAge( 50 );

        store.update( id, p );

        p = (Person) store.fetch( "name", "michal" );

        assertEquals( "michal", p.getName() );

        assertEquals( 50, p.getAge() );

        // ----------------------------------------------------------------------
        // Now change the name which is an indexed field
        // ----------------------------------------------------------------------

        p.setName( "michal2" );

        store.update( id, p );

        p = (Person) store.fetch( "name", "michal2" );

        assertNotNull( p );

        // ----------------------------------------------------------------------
        // Now delete it
        //
        // We're doing a little trick here to make sure our indices are correctly
        // removed as well. I am changing value of the name field which is used
        // for indexing.
        // ----------------------------------------------------------------------

        p.setName( "tricky" );

        store.delete( id );

        try
        {
            store.fetch( "name", "michal2" );

            fail( "Object should not exist in store!" );
        }
        catch ( Exception e )
        {
        }

        // ----------------------------------------------------------------------
        // fetchAll
        // ----------------------------------------------------------------------

        // but what if you didn't have any indices?

        for ( Iterator i = store.fetchAll( "name" ); i.hasNext(); )
        {
            Person o = (Person) i.next();

            System.out.println( "o = " + o );
        }
    }
}
