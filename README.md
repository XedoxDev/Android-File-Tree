# AndroidFileTree

Default module ":app" turnoff, change it in settings.gradle. Its module use for testing library;

**// Information //**

• **Latest Version:** 0.0.1a

• **Current State:** In Development (Indev)

• **Target SDK:** 21

• **Compile SDK:** 33

• **Package:** org.xedox.filetree

FileTree for Android by XedoxSL.
Built upon RecyclerView for smooth and responsive file system navigation.

# Usage

1. Instantiate the `FileTree` class in your code or XML layout.

2. Call the `filetree.loadPath("path/to/root/directory")` method to display the contents of the specified directory.

3. You can make custom icons, example:
```java
filetree.icons.put(".lua", R.drawable.lua_icon);
```
In key, you can paste any extension.

If you use my lib, please specify link to this repo...