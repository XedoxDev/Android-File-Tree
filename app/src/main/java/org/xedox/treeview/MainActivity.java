package org.xedox.treeview;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Environment;

import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import org.xedox.treeview.widget.FileTree;

public class MainActivity extends AppCompatActivity {

    private FileTree tree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        tree = findViewById(R.id.filetree);
        tree.loadPath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/");
        setSupportActionBar((MaterialToolbar) findViewById(R.id.toolbar));
        EditText ed = findViewById(R.id.path);
        findViewById(R.id.set)
                .setOnClickListener(
                        (v) -> {
                            tree.loadPath(ed.getText().toString());
                        });
        
        tree.adapter.onFileClickListener = (node, file) -> {
            new AlertDialog.Builder(MainActivity.this).setMessage(node.toString()).create().show();
        };
    }
}
