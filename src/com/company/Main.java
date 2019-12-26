package com.company;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

public class Main {

    public static void main(String[] args) {
        FileSystem fileSystem = new FileSystem();
        fileSystem.ls("/");
        fileSystem.mkdir("/m");
        fileSystem.mkdir("/w");
        fileSystem.addContentToFile("/dycete", "emer");
        fileSystem.ls("/");

    }
}

// PQ
class FileSystem {

    class Node {
        boolean isFile;
        String name, content;
        PriorityQueue<Node> children;

        Node() {
            isFile = false;
            name = "";
            content = "";
            children = new PriorityQueue<>((n1, n2) -> n1.name.compareTo(n2.name));
        }
    }

    Node root;

    public FileSystem() {
        root = new Node();
        root.isFile = false;
    }

    public List<String> ls(String path) {
        List<String> res = new ArrayList<>();
        Node curr = findNodeByPath(path);
        if (curr != null) {
            if (curr.isFile) {
                res.add(curr.name);
                return res;
            } else {
                List<Node> temp = new ArrayList<>();
                while (!curr.children.isEmpty()) {
                    temp.add(curr.children.poll());
                    res.add(temp.get(temp.size() - 1).name);
                }
                for (Node node : temp) {
                    curr.children.offer(node);
                }
                return res;
            }
        }
        return res;
    }

    public void mkdir(String path) {
        if (path.equals("/"))
            return;
        Node curr = root;
        String[] pArr = path.split("/");
        int i = 0;
        while (i < pArr.length) {
            String toFind = pArr[i];
            if (toFind.isEmpty()){
                i++;
                continue;
            }

            Iterator itr = curr.children.iterator();
            boolean toCreate = true;
            while (itr.hasNext()) {
                Node child = (Node) itr.next();
                if (child.name.equals(toFind)) {
                    curr = child;
                    toCreate = false;
                    break;
                }
            }
            if (toCreate) {
                Node child = new Node();
                child.isFile = false;
                child.name = toFind;
                curr.children.offer(child);
                curr = child;
            }
            i++;
        }
    }

    public void addContentToFile(String filePath, String content) {
        int lastIndex = filePath.lastIndexOf('/');
        mkdir(filePath.substring(0, lastIndex + 1));
        Node curr = findNodeByPath(filePath.substring(0, lastIndex));
        String toCreateFile = filePath.substring(lastIndex + 1);
        if (curr != null) {
            Iterator itr = curr.children.iterator();
            boolean toCreate = true;
            while (itr.hasNext()) {
                Node child = (Node) itr.next();
                if (child.name.equals(toCreateFile)) {
                    curr = child;
                    toCreate = false;
                    break;
                }
            }
            if (toCreate) {
                Node child = new Node();
                child.isFile = true;
                child.name = toCreateFile;
                child.content = content;
                curr.children.offer(child);
            } else {
                curr.content = curr.content + content;
            }
        }
    }

    public String readContentFromFile(String filePath) {
        Node curr = findNodeByPath(filePath);
        if (curr != null && curr.isFile) {
            return curr.content;
        } else
            return "";
    }

    private Node findNodeByPath(String path) {
        Node curr = root;
        if (path.equals("/") || path.isEmpty())
            return curr;
        String[] pArr = path.split("/");
        int i = 0;
        boolean found = false;
        while (i < pArr.length) {
            String toFind = pArr[i];
            if (toFind.isEmpty()) {
                i++;
                continue;
            }
            found = false;
            Iterator itr = curr.children.iterator();
            while (itr.hasNext()) {
                Node child = (Node) itr.next();
                if (child.name.equals(toFind)) {
                    found = true;
                    curr = child;
                    break;
                }
            }
            if (found)
                i++;
            else
                break;
        }
        return found ? curr : null;
    }
}


public class FileSystem {
    private FileNode root;

    public FileSystem() {
        root = new FileNode("");
    }

    public List<String> ls(String path) {
        return findNode(path).getList();
    }

    public void mkdir(String path) {
        findNode(path);
    }

    public void addContentToFile(String filePath, String content) {
        findNode(filePath).addContent(content);
    }

    public String readContentFromFile(String filePath) {
        return findNode(filePath).getContent();
    }

    //-- private method section --//
    private FileNode findNode(String path){
        String[] files = path.split("/");

        FileNode cur = root;
        for(String file : files){
            if(file.length() == 0) continue;

            cur.children.putIfAbsent(file, new FileNode(file));
            cur = cur.children.get(file);

            if(cur.isFile()) break;
        }

        return cur;
    }

    // Private class
    private class FileNode{
        private TreeMap<String, FileNode> children;
        private StringBuilder file;
        private String name;

        public FileNode(String name) {
            children = new TreeMap<>();
            file = new StringBuilder();
            this.name = name;
        }

        public String getContent(){
            return file.toString();
        }

        public String getName(){
            return name;
        }

        public void addContent(String content){
            file.append(content);
        }

        public boolean isFile(){
            return file.length() > 0;
        }

        public List<String> getList(){
            List<String> list = new ArrayList<>();
            if(isFile()){
                list.add(getName());
            }else{
                list.addAll(children.keySet());
            }

            return list;
        }
    }
}