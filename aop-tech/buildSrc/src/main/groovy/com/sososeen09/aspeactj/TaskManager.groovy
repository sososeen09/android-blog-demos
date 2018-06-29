package com.sososeen09.aspeactj

class TaskManager {
    ArrayList<File> aspectPath = new ArrayList<>()
    ArrayList<File> classPath = new ArrayList<>()
    List<String> ajcArgs = new ArrayList<>()
    String encoding
    String bootClassPath
    String sourceCompatibility
    String targetCompatibility
}