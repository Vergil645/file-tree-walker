# file-tree-walker
Console application that walk throw the file system and calculates the hash of files using [SHA-1](https://en.wikipedia.org/wiki/SHA-1) algorithm.

There is to applications inside:

`Walk` - watch only files that listed in input file and print null hash for the directories.

`RecursiveWalk` - walk throw the file system tree recursively, going down into directories.

Both versions print null hash when cannot read open each file or directory.

You can find examples of input and output files in [examples](examples).

### Running application

`input file` - file with list of target files and directories in format 'one file/directory per line'

`output file` - file to which the result will be written

Run:
```shell
make build
java -cp build <walk.Walk/walk.RecusriveWalk> <input file> <output file>
```

Run using [JAR](https://en.wikipedia.org/wiki/JAR_(file_format)):
```shell
make build-to-jar
java -jar <Walk.jar/Recursive.jar> <input file> <output file>
```

Clean:
```shell
make clean
```
