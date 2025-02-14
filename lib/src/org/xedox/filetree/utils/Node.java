package org.xedox.treeview.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Node {
    public String name;
    public String path;
    public String fullPath;

    public boolean isFile;
    public boolean isOpen;
    public List<Node> children;

    public int level = 0;
    public int id;

    public static int idCount = 0;

    public Node() {
        name = "";
        path = "";
        fullPath = "";
        isOpen = false;
        children = null;
        isFile = true;
        id = idCount++;
        fullPath = path + File.separator + name;
    }

    public Node(String fullPath) {
        File f = new File(fullPath);
        name = f.getName();
        path = f.getParent();
        this.fullPath = fullPath;
        isOpen = false;
        isFile = f.isFile();
        id = idCount++;
    }

    public List<Node> updateChildren() {
        children = new ArrayList<>();
        File directory = new File(fullPath);

        File[] files = directory.listFiles();
        for (File f : files) {
            Node node = new Node(f.getAbsolutePath());
            node.level = level + 1;
            children.add(node);
        }
        return children;
    }

    public List<Node> children() {
        if (children == null) return updateChildren();
        return children;
    }

    @Override
    public String toString() {
        return String.format(
                "Node {name='%s', path='%s', fullPath='%s', childrenCount=%d, isFile=%b, isOpen=%b, children=%s}\n",
                name,
                path,
                fullPath,
                (children() == null ? 0 : children().size()),
                isFile,
                isOpen,
                children());
    }
}
