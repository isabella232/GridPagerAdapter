package com.teepaps.android.gridpager;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ted on 6/10/14.
 */
public class GridPagerAdapter extends PagerAdapter {

    //**********************************************************************************************
    // NON-STATIC DATA MEMBERS
    //**********************************************************************************************

    /* The holder for all of the views */
    ListAdapter adapter;
    List<LinearLayout> tables;
    LayoutInflater inflater;
    Context context;
    final int columnCount;
    final int rowCount;
    final int cellsPerTable;

    private String emptyText = "This is empty";
    private String displayText;

    //**********************************************************************************************
    // CONSTRUCTORS
    //**********************************************************************************************

    public GridPagerAdapter(Context context, int rows, int columns, ListAdapter adapter) {
        super();
        this.context = context;

        this.columnCount = columns;
        this.rowCount = rows;
        this.cellsPerTable = columnCount * rowCount;
        this.adapter = adapter;

//        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
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
        }
        else {
            TextView textView = new TextView(context);
            textView.setText(displayText != null ? displayText : emptyText);
            view  = textView;
        }

        container.addView(view);
        return view;
    }

    @Override
    public int getCount() {
        if (adapter != null) {
            return (adapter.getCount() / cellsPerTable) + 1;
        }
//        else if (displayText != null) {
//            return 1;
//        }

//        return 0;

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
            final int numberOfTables = (adapter.getCount() / cellsPerTable) + 1;
            tables = new ArrayList<LinearLayout>(numberOfTables);

            // Build and add all the tables to the list
            for (int i = 0; i < numberOfTables; i++) {
                tables.add(buildTable(cellsPerTable * i));
            }
        }
        else {
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
//        ViewPager.LayoutParams params = new ViewPager.LayoutParams();
//        params.height = LayoutParams.MATCH_PARENT;
//        linearLayout.setLayoutParams(params);
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
        this.displayText = null;
        buildPages();
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

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public void setEmptyText(String emptyText) {
        this.emptyText = emptyText;
    }

}
