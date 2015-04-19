/*
 * GroupableTableHeaderUI.java
 *
 * Created on December 20, 2006, 1:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package tableRender;

/**
 *
 * @author root
 */

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * UI implementation for displaying groupable headers.
 * 
 * @see au.com.objects.swing.table.GroupableTableHeader
 * @author mick http://www.objects.com.au
 *  
 */
public class GroupableTableHeaderUI extends BasicTableHeaderUI
{
	public GroupableTableHeaderUI()
	{
		super();
	}

	public static ComponentUI createUI(JComponent c)
	{
		return new GroupableTableHeaderUI();
	}

	public void paint(Graphics g, JComponent c)
	{
		TableColumnModel cm = header.getColumnModel();
		if (cm.getColumnCount() > 0)
		{
			Rectangle clip = g.getClipBounds();
			Point left = clip.getLocation();
			Point right = new Point(clip.x + clip.width - 1, clip.y);
			int cMin = header.columnAtPoint(left);
			int cMax = header.columnAtPoint(right);
			if (cMax == -1)
			{
				// If the table does not have enough columns to fill 
				// the view we'll get -1.
				// Replace this with the index of the last column.

				cMax = cm.getColumnCount() - 1;
			}

			int columnWidth;
			Rectangle cellRect = header.getHeaderRect(cMin);
			
			Set done = new HashSet();
			for (int column = cMin; column <= cMax; column++)
			{
				TableColumn aColumn = cm.getColumn(column);
				columnWidth = aColumn.getWidth();
				cellRect.width = columnWidth;
				paintCell(done, g, cellRect, column);
				cellRect.x += columnWidth;
			}

			// Remove all components in the rendererPane.
			
			rendererPane.removeAll();
		}
	}

	/**
	 * Paints a column header, plus any (unpainted) group headers that it is a member of
	 * @param done set of group headers already painted
	 * @param g graphic context
	 * @param cellRect bounds to paint header
	 * @param columnIndex column index
	 */
	private void paintCell(Set done, Graphics g, Rectangle cellRect, int columnIndex)
	{
		GroupableTableHeader groups = (GroupableTableHeader) header;
		int depth = groups.getDepth();
		
		// get list of headers at this column index
		
		List headers = groups.getHeaders(columnIndex);
		for (int i=0; i<headers.size(); i++)
		{
			Object heading = headers.get(i);
			if (heading instanceof ColumnGroup)
			{
				ColumnGroup group = (ColumnGroup) heading;
				if (!done.contains(group))
				{
					// group header has not been painted so paint it
					
					Component component = getGroupedHeaderRenderer(group);
					int height = component.getPreferredSize().height;
					int columns = group.getColumnSpan();
					rendererPane.paintComponent(g, component, header, cellRect.x,
							cellRect.y + height*i, cellRect.width * columns,
							height, true);
					
					// and flag it as painted
					
					done.add(group);
				}
			}
			else
			{
				// Paint TableColumn header
				
				Component component = getHeaderRenderer(columnIndex);
				int height = component.getPreferredSize().height;
				rendererPane.paintComponent(g, component, header, cellRect.x,
						cellRect.y + height*(headers.size() - 1), cellRect.width,
						height * (depth - headers.size() + 1), true);
			}
		}
	}

	private int viewIndexForColumn(TableColumn aColumn)
	{
		TableColumnModel cm = header.getColumnModel();
		for (int column = 0; column < cm.getColumnCount(); column++)
		{
			if (cm.getColumn(column) == aColumn)
			{
				return column;
			}
		}
		return -1;
	}

	private int getHeaderHeight()
	{
		int result = 0;
		TableColumnModel columns = header.getColumnModel();
		for (int i = 0; i < columns.getColumnCount(); i++)
		{
			TableColumn column = columns.getColumn(i);
			Component comp = getHeaderRenderer(i);
			GroupableTableHeader groups = (GroupableTableHeader) header;
			int height = comp.getPreferredSize().height * groups.getDepth();
			result = Math.max(result, height);
		}
		return result;
	}

	private Dimension createHeaderSize(long width)
	{
		TableColumnModel columnModel = header.getColumnModel();
		if (width > Integer.MAX_VALUE)
		{
			width = Integer.MAX_VALUE;
		}
		return new Dimension((int) width, getHeaderHeight());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.plaf.ComponentUI#getPreferredSize(javax.swing.JComponent)
	 */
	public Dimension getPreferredSize(JComponent c)
	{
		long width = 0;
		Enumeration enumeration = header.getColumnModel().getColumns();
		while (enumeration.hasMoreElements())
		{
			TableColumn aColumn = (TableColumn) enumeration.nextElement();
			width = width + aColumn.getPreferredWidth();
		}
		return createHeaderSize(width);
	}

	private Component getHeaderRenderer(int colindex)
	{
		TableColumn column = header.getColumnModel().getColumn(colindex);
		TableCellRenderer renderer = column.getHeaderRenderer();
		if (renderer == null)
		{
			renderer = header.getDefaultRenderer();
		}
		return renderer.getTableCellRendererComponent(header.getTable(), column
				.getHeaderValue(), false, false, -1, colindex);
	}

	private Component getGroupedHeaderRenderer(ColumnGroup group)
	{
		TableCellRenderer renderer = header.getDefaultRenderer();
		return renderer.getTableCellRendererComponent(header.getTable(),
				group.getName(), false, false, -1, -1);
	}
}
