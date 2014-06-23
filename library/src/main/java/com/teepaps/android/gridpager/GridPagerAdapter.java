package com.gastrograph.adapters;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ToggleButton;


import java.util.ArrayList;
import java.util.List;

/**
 * A Pager Adapter that creates Grid-like/Table-like pages, given a ListAdapter.
 *
 * @author Ted Papaioannou
 * @author Mitch Clarke
 */
public class GridPagerAdapter extends PagerAdapter {

    //**********************************************************************************************
    // NON-STATIC DATA MEMBERS
    //**********************************************************************************************

    /* The holder for all of the views */
    ListAdapter adapter;

    /**
     * List the pages' layouts
     */
    List<LinearLayout> tables;

    /**
     * Inflater to get xml for textviews
     */
    LayoutInflater inflater;

    /**
     * Context for views to use from
     */
    Context context;

    // ---- Table constants
    /**
     * Number of columns to display per page
     */
    private int columnCount;
    /**
     * Number of rows to display per page
     */
    private int rowCount;
    /**
     * Number of cells displayed per table (rows * column)
     */
    private int cellsPerTable;

    // --- Non-grid views
    /**
     * View to use when only a single view should be displayed without the grid
     */
    private View displayView;

    // --- Non-grid view text
    /**
     * View pager to set OnPageChangeListener
     */
    private ViewPager viewPager;

    /**
     * Should the grid pager scroll smoothly?
     */
    private boolean isSmoothScroll;

    //**********************************************************************************************
    // CONSTRUCTORS
    //**********************************************************************************************

    public GridPagerAdapter(Context context, int rows, int columns, ListAdapter adapter,
                            ViewPager pager, final boolean isSmoothScroll)
    {
        super();
        this.context = context;
        this.viewPager = pager;
        this.columnCount = columns;
        this.rowCount = rows;
        this.isSmoothScroll = isSmoothScroll;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(isSmoothScroll)
            viewPager.setOnPageChangeListener(new OnPageChangeListener() {

                @Override
                public void onPageSelected(int position) {
                }

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageScrollStateChanged(int state)
                {
                    if (isSmoothScroll && state == ViewPager.SCROLL_STATE_IDLE) {
                        int pageCount = getCount();
                        int currentItem = viewPager.getCurrentItem();
                        if (currentItem == 0) {
                            viewPager.setCurrentItem(pageCount - 2, false);
                        } else if (currentItem == pageCount - 1) {
                            viewPager.setCurrentItem(1, false);
                        }
                    } }
            });


        setCellsPerTable(rowCount, columnCount);
        changeListAdapter(adapter);
        buildPages();
    }

    //**********************************************************************************************
    // OVERRIDDEN METHODS
    //**********************************************************************************************

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        if (adapter != null) {
            view = tables.get(position);
        } else {
            view = displayView;
        }
        view.setTag(view);
        container.addView(view);

        return view;
    }


    @Override
    public int getCount() {
        if (adapter != null) {
            int padding = 0;
            if(isSmoothScroll)
                padding = 2;
            return (adapter.getCount() / cellsPerTable) + 1 + padding;
        }

        return 1;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //**********************************************************************************************
    // NON-STATIC METHODS
    //**********************************************************************************************

    /**
     * Build the table list which will be displayed in the pages
     */
    public void buildPages() {
        if (adapter != null) {
            int numberOfTables = getCount();
            tables = new ArrayList<LinearLayout>(numberOfTables);

            // Build and add all the tables to the list
            if(isSmoothScroll)
                {
                    numberOfTables -=2; //We are adding the padding manually
                    tables.add(buildTable((numberOfTables-1)*cellsPerTable));
                }
            for (int i = 0; i < numberOfTables; i++) {
                tables.add(buildTable(cellsPerTable * i));
            }
            if(isSmoothScroll) tables.add(buildTable(0));
        } else {
            tables = null;
        }
    }

    /**
     * Create the TableRows and add them to the TableLayout to create the table starting at
     * 'startingIndex'
     */
    public LinearLayout buildTable(int startingIndex) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setWeightSum(rowCount);
        linearLayout.setGravity(Gravity.FILL_VERTICAL);

        // Create all the rows
        for (int i = 0; i < rowCount; i++) {
            linearLayout.addView(buildRow(startingIndex + (columnCount * i)));
        }

        return linearLayout;
    }

    /**
     * Build the row of columns starting at 'startingIndex' that holds 'columnCount' number of cells
     */
    private LinearLayout buildRow(int startingIndex) {
        LinearLayout row = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.weight = 1;
        row.setLayoutParams(params);
        row.setWeightSum(columnCount);
        row.setOrientation(LinearLayout.HORIZONTAL);


        // Create all the columns in the row
        int i = startingIndex;
        while ((i < adapter.getCount()) && (i < columnCount + startingIndex)) {
            View view = buildCell(i);
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT);
            params2.weight = 1;
            view.setLayoutParams(params2);

            row.addView(view);
            i++;
        }
        ToggleButton toggleButton = new ToggleButton(context);
        toggleButton.setText("");
        toggleButton.setTextOn("");

        return row;
    }

    /**
     * Builds the view for a cell
     */
    private View buildCell(int index) {
        return adapter.getView(index, null, null);
    }

    public void changeListAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        this.displayView = null;
        buildPages();
    }

    private void setCellsPerTable(final int rowCount, final int columnCount) {
        cellsPerTable = rowCount * columnCount;
    }

    //**********************************************************************************************
    // GETTERS
    //**********************************************************************************************

    public List<LinearLayout> getTables() {
        return tables;
    }

    //**********************************************************************************************
    // SETTERS
    //**********************************************************************************************

    public void setViewPager(ViewPager pager){this.viewPager = pager;}

    public void setDisplayView(View view) {
        this.displayView = view;
    }



    public void setRows(int rowCount) {
        this.rowCount = rowCount;
        setCellsPerTable(rowCount, columnCount);
    }


    public void setColumns(int columnCount) {
        this.columnCount = columnCount;
        setCellsPerTable(rowCount, columnCount);
    }
}