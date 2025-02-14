package org.xedox.treeview.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import static androidx.recyclerview.widget.RecyclerView.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xedox.treeview.utils.Node;
import org.xedox.treeview.R;

public class FileTreeAdapter extends Adapter<FileTreeAdapter.VH> {

    public List<Node> nodes = new ArrayList<>();
    public Node root;

    public Context context;
    // default file item
    public int fileItemLayout = R.layout.file_item;

    // indent relatively parent folder
    public int indent = 20;
    // Icons for files
    //   exmaple: ".lua": R.drawable.lua_file
    public Map<String, Integer> icons = new HashMap<>();

    // listeners for file (not necessarily )
    public OnFileClickListener onFileClickListener;
    public OnFileLongClickListener onFileLongClickListener;

    public FileTreeAdapter(Context context) {
        this.context = context;
        icons.put("file", R.drawable.file);
        icons.put("folder", R.drawable.folder);
    }

    class VH extends ViewHolder {
        public ImageButton isOpen;
        public ImageView icon;
        public TextView name;
        public View parent;

        public VH(View root) {
            super(root);
            parent = root.findViewById(R.id.parent);
            icon = root.findViewById(R.id.icon);
            name = root.findViewById(R.id.name);
            isOpen = root.findViewById(R.id.isOpen);
        }

        public void bind(Node node) {
            parent.setX(indent * node.level);
            // file name to textview
            name.setText(node.name);

            // init view
            if (node.isFile) initFile(node);
            else initFolder(node);

            // listeners...
            if (onFileClickListener != null) {
                parent.setOnClickListener(
                        (v) -> {
                            onFileClickListener.onClick(node, new File(node.fullPath));
                        });
            }

            if (onFileLongClickListener != null) {
                parent.setOnLongClickListener(
                        (v) -> {
                            onFileLongClickListener.onClick(node, new File(node.fullPath));
                            return true;
                        });
            }
        }

        public void initFile(Node node) {
            if (icons != null) {
                // search icon on extension in map icons...
                boolean found = false;
                for (String key : icons.keySet()) {
                    if (key.equals("folder")) continue;
                    if (node.name.endsWith(key)) {
                        // FIND :)
                        icon.setImageResource(icons.get(key));
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    // No found :(
                    icon.setImageResource(R.drawable.file);
                }
            } else {
                // default...
                icon.setImageResource(R.drawable.file);
            }
        }

        public void initFolder(Node node) {
            // you can set to custom folder icon:
            //   icons.set("folder", R.drawble.my_folder_icon);
            if (icons != null) {
                icon.setImageResource(icons.get("folder"));
            } else {
                // default
                icon.setImageResource(R.drawable.folder);
            }

            isOpen.setImageResource(R.drawable.arrow_up);
            isOpen.setRotation(node.isOpen ? 180 : 0);

            View.OnClickListener ocl =
                    (v) -> {
                        node.isOpen = !node.isOpen;
                        if (node.isOpen) {
                            openFolder(node);
                        } else {
                            closeFolder(node, true);
                        }
                        isOpen.animate().scaleY(isOpen.getScaleY() == 1 ? -1 : 1);
                        /* debugging items level
                        StringBuilder txt = new StringBuilder();
                                nodes.forEach(
                                        (el) -> {
                                            txt.append(el.level + "\n");
                                        });
                                new AlertDialog.Builder(context).setMessage(txt.toString()).create().show();
                        */
                    };

            // set listeners
            isOpen.setOnClickListener(ocl);
            parent.setOnClickListener(ocl);
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup vg, int pos) {
        View root = LayoutInflater.from(context).inflate(fileItemLayout, vg, false);
        VH vh = new VH(root);
        return vh;
    }

    @Override
    public void onBindViewHolder(VH vh, int pos) {
        vh.bind(nodes.get(pos));
    }

    @Override
    public int getItemCount() {
        return nodes.size();
    }

    public boolean isDebug = false;

    public void removeNode(Node node) {
        nodes.remove(node);
        notifyItemRemoved(nodes.indexOf(node));
    }

    public void removeNode(int pos) {
        nodes.remove(pos);
        notifyItemRemoved(pos);
    }
    
    public void addNode(Node parent, Node toAdd) {
        if(parent.isFile) return;
        parent.children.add(toAdd);
        int parentIndex = nodes.indexOf(parent)+1;
        if(parent.isOpen) {
            nodes.add(parentIndex, toAdd);
            notifyItemInserted(parentIndex);
        }
    }

    public interface OnFileClickListener {
        public void onClick(Node node, File file);
    }

    public interface OnFileLongClickListener {
        public void onClick(Node node, File file);
    }

    public void openFolder(Node node) {
        node.children();
        if (node.children.size() > 0) {
            openFolder2(node, true);
        }
    }

    private List<Node> toAdd;

    private void openFolder2(Node node, boolean isRoot) {
        if (isRoot) {
            node.isOpen = true;
            toAdd = new ArrayList<>();
        }
        for (Node child : node.children) {
            toAdd.add(child);
            if (child.isFile && child.isOpen) openFolder2(child, false);
        }
        if (isRoot) {
            nodes.addAll(nodes.indexOf(node) + 1, toAdd);
            notifyItemRangeInserted(nodes.indexOf(node) + 1, toAdd.size());
        }
    }

    private List<Integer> indicesToRemove;

    public void closeFolder(Node node, boolean isRoot) {
        List<Node> children = node.children();

        if (isRoot) {
            indicesToRemove = new ArrayList<>();
        }

        if (children.size() > 0) {
            for (Node c : children) {
                int index = nodes.indexOf(c);
                if (index != -1) {
                    indicesToRemove.add(index);

                    if (!c.isFile && c.isOpen) {
                        closeFolder(c, false);
                    }
                }
            }
        }

        if (isRoot) {
            node.isOpen = false;
            Collections.sort(indicesToRemove, Collections.reverseOrder());
            for (int index : indicesToRemove) {
                nodes.remove(index);
            }
            notifyItemRangeRemoved(nodes.indexOf(node) + 1, indicesToRemove.size());
            indicesToRemove.clear();
        }
    }

    public void setRoot(Node newRoot) {
        this.root = newRoot;
        root.isOpen = false;
        root.level = 0;
        nodes.clear();
        nodes.add(newRoot);
        notifyDataSetChanged();
    }
}
