/*
 * GroupableTableHeader.java
 *
 * Created on December 20, 2006, 1:20 PM
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

import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * JTableHeader subclass that supports grouping headers.
 * 
 * @author mick http://www.objects.com.au
 */

public class GroupableTableHeader extends JTableHeader
{
	public static final String uiClassID = "GroupableTableHeaderUI";

	/** list of column groups */

	
	private List Groups = new ArrayList();
	
	public GroupableTableHeader()
	{
		super();
	}

	public GroupableTableHeader(TableColumnModel cm)
	{
		super(cm);
	}

	/**
	 * Adds a column group to header 
	 * @param group column group
	 */

	public void addGroup(ColumnGroup group)
	{
		Groups.add(group);
	}
	
	/**
	 * Returns the maximum depth of headers at any column.
	 * @return maximum header count
	 */

	public int getDepth()
	{
		int depth = 0;
		Iterator i = Groups.iterator();
		while (i.hasNext())
		{
			ColumnGroup group = (ColumnGroup) i.next();
			depth = Math.max(depth, group.getDepth());
		}
		return depth;
	}

	/**
	 * Returns a list of headers at a particular column.

	 * This last element in the list will be the TableColumn
	 * for that column, with the other elements being
	 * ColumnGroup's.
	 * @param col column index
	 * @return list of column headings
	 */
	
	public List getHeaders(int col)
	{
		List result = new ArrayList();
		
		// get the column model
		
		TableColumn column = getColumnModel().getColumn(col);
		
		// iterate thru the groups looking for the column
		
		Iterator i = Groups.iterator();
		while (i!=null && i.hasNext())
		{
			Object child = i.next();
			if (child instanceof ColumnGroup)
			{
				// found a group, check if it contains column
				
				ColumnGroup group = (ColumnGroup) child;
				if (group.contains(column))
				{
					// It does, so add group to result list
					
					result.add(group);
					
					// and continue iterating thru that group
					
					i = group.iterator();
				}
			}
		}
		
		// finally add column
		
		result.add(column);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.JTableHeader#setReorderingAllowed(boolean)
	 */

	public void setReorderingAllowed(boolean reorderingAllowed)
	{
		if (reorderingAllowed)
		{
			throw new IllegalArgumentException(
					"Cannot reorder groupable headings");
		}
		super.setReorderingAllowed(reorderingAllowed);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#getUIClassID()
	 */

	public String getUIClassID()
	{
		return uiClassID;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.table.JTableHeader#initializeLocalVars()
	 */
	
	protected void initializeLocalVars()
	{
		super.initializeLocalVars();
		reorderingAllowed = false;
	}
}
