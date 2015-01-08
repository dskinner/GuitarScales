package cc.dasa.guitarscales;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends ActionBarActivity {

    private GuitarView mGuitarView;
    private SharedPreferences mSettings;
    private Adapter mAdapter;
    private int mNavIndex;

    private ActionBar.OnNavigationListener mNavigationListener = new ActionBar.OnNavigationListener() {
        @Override
        public boolean onNavigationItemSelected(int i, long l) {
            mNavIndex = i;
            if (i == 0) {
                mGuitarView.clearSelections();
                return true;
            }
            String name = mAdapter.getItem(i);
            Set<String> notes = mSettings.getStringSet(name, null);
            if (notes == null) {
                throw new RuntimeException("Got null when retrieving notes by name.");
            }
            mGuitarView.clearSelections();
            for (String note : notes) {
                mGuitarView.addSelections(Note.fromName(note));
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSettings = getSharedPreferences("foo", 0);

        if (!mSettings.contains("Waterfall Pentatonic")) {
            Set<String> notes = new HashSet<String>();
            for (Note note : new Note[]{Note.G, Note.A, Note.B, Note.C, Note.D}) {
                notes.add(note.getName());
            }
            mSettings.edit().putStringSet("Waterfall Pentatonic", notes).commit();
        }

        mAdapter = new Adapter();
        setContentView(R.layout.activity_main);
        getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        getSupportActionBar().setListNavigationCallbacks(mAdapter, mNavigationListener);
        mGuitarView = (GuitarView) findViewById(R.id.guitar_view);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem actionSave = menu.findItem(R.id.action_save);
        final EditText name = (EditText) actionSave.getActionView().findViewById(R.id.name);
        Button save = (Button) actionSave.getActionView().findViewById(R.id.save);

        actionSave.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (mNavIndex != 0) {
                    name.setText(mAdapter.getItem(mNavIndex));
                }
                return true;
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = name.getText().toString();
                if (key.equals("")) {
                    return;
                }
                Set<String> notes = new HashSet<String>();
                for (Note note : mGuitarView.getSelections()) {
                    notes.add(note.getName());
                }
                mSettings.edit().putStringSet(key, notes).commit();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(name.getWindowToken(), 0);
                actionSave.collapseActionView();
                mAdapter = new Adapter();
                getSupportActionBar().setListNavigationCallbacks(mAdapter, mNavigationListener);
                for (int i = 0; i < mAdapter.getCount(); i++) {
                    if (key.equals(mAdapter.getItem(i))) {
                        getSupportActionBar().setSelectedNavigationItem(i);
                        break;
                    }
                }
            }
        });
        return true;
    }

    private class Adapter extends BaseAdapter {

        private String[] mNames;

        public Adapter() {
            reloadData();
        }

        public void reloadData() {
            Set<String> strings = mSettings.getAll().keySet();
            mNames = strings.toArray(new String[strings.size()]);
        }

        @Override
        public int getCount() {
            return mNames.length + 1;
        }

        @Override
        public String getItem(int position) {
            if (position == 0) {
                return "";
            }
            return mNames[position - 1];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.spinner_item, parent, false);
            }
            TextView name = (TextView) convertView.findViewById(R.id.name);
            if (position == 0) {
                name.setText("Not Saved");
            } else {
                name.setText(getItem(position));
            }
            return convertView;
        }
    }
}
