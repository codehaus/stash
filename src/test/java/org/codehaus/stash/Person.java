package org.codehaus.stash;

import java.io.Serializable;

/**
 *
 *
 * @author <a href="mailto:jason@maven.org">Jason van Zyl</a>
 *
 * @version $Id: Person.java,v 1.1.1.1 2003/11/21 14:26:43 jvanzyl Exp $
 */
public class Person
    implements Serializable
{
    private String name;

    private int age;

    public Person( String name, int age )
    {
        this.name = name;

        this.age = age;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public int getAge()
    {
        return age;
    }

    public void setAge( int age )
    {
        this.age = age;
    }

    // ----------------------------------------------------------------------
    //
    // ----------------------------------------------------------------------

    public String toString()
    {
        return name;
    }

}
