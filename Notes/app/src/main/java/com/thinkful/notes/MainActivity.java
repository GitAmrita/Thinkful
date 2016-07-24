package com.thinkful.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private NoteListItemAdapter mAdapter;
    private Button mButton;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new NoteListItemAdapter(this, mRecyclerView);
        mRecyclerView.setAdapter(mAdapter);
        mButton = (Button) findViewById(R.id.button);
        mEditText = (EditText) findViewById(R.id.edit_text);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String note = mEditText.getText().toString();
                final NoteListItem item = new NoteListItem(note);
                mAdapter.addItem(item);
                mEditText.setText("");
                mLayoutManager.scrollToPosition(0);
            }
        });
    }
}
