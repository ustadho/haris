/*
 * ColumnGroup.java
 *
 * Created on December 20, 2006, 1:18 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tableRender;

/**
 *
 * @author root
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.TableColumn;

/**
 * A grouping of TableColumns and ColumnGroups used for grouping JTable headers.
 * 
 * @see au.com.objects.swing.table.GroupableTableHeader
 * @author mick http://www.objects.com.au
 *
 */
public class ColumnGroup
{
	private String Name = null;
	
	private List Children = new ArrayList();
	
	public ColumnGroup()
	{
		super();
	}
	
	/**
	 * Constructor
	 * @param name group name
	 */
	public ColumnGroup(String name)
	{
		super();
		Name = name;
	}
	
	
	/**
	 * Returns the name of this group of columns
	 * @return Returns the group name
	 */
	public String getName()
	{
		return Name;
	}
	
	/**
	 * Returns the number of table columns that this group spans
	 * @return number of columns spanned
	 */
	public int getColumnSpan()
	{
		int width = 0;
		Iterator i = Children.iterator();
		while (i.hasNext())
		{
			Object child = i.next();
			if (child instanceof ColumnGroup)
			{
				// another group, so add the number of columns it spans
				
				ColumnGroup group = (ColumnGroup) child;
				width += group.getColumnSpan();
			}
			else
			{
				// a TableColumn so increment count
				
				width++;
			}
		}
		return width;
	}
	
	/**
	 * Return an iterator for the members of this group
	 */
	
	public Iterator iterator()
	{
		return Children.iterator();
	}
	
	/**
	 * Adds a new TableColumn to the group. It is assumed this column
	 * is adjacent to the previously added column/group.
	 * @param column column to add
	 */
	
	public void add(TableColumn column)
	{
		Children.add(column);
	}
	
	/**
	 * Adds a new ColumnGroup to the group. It is assumed this group
	 * is adjacent to the previously added column/group.
	 * @param column column to add
	 */

	public void add(ColumnGroup group)
	{
		Children.add(group);
	}
	
	/**
	 * Returns true if this group (or one of its subgroups) contains
	 * a certain column.
	 */
	public boolean contains(TableColumn column)
	{
		boolean result = false;
		Iterator i = Children.iterator();
		while (!result && i.hasNext())
		{
			Object child = i.next();
			if (child instanceof ColumnGroup)
			{
				// a column group, so check if it contains the column
				
				ColumnGroup group = (ColumnGroup) child;
				result = group.contains(column);
			}
			else
			{
				// a column, so check for match
				
				result = (column==child);
			}
		}
		return result;
	}
	
	/**
	 * Returns the maximum depth of headers in this group.
	 * @return maximum header count
	 */
	public int getDepth()
	{
		int depth = 0;
		Iterator i = Children.iterator();
		while (i.hasNext())
		{
			Object child = i.next();
			if (child instanceof ColumnGroup)
			{
				ColumnGroup group = (ColumnGroup) child;
				depth = Math.max(depth, group.getDepth()+1);
			}
			else
			{
				depth = Math.max(depth, 2);
			}
		}
		return depth;
	}
	
	public String toString()
	{
		return Name;
	}
}