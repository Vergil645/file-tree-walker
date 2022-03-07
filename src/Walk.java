package walk;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

public class Walk extends RecursiveWalk {
    public static void main(final String[] args) {
        new Walk().run(args);
    }

    @Override
    protected FileVisitor<Path> getFileVisitor(final Writer writer, final MessageDigest messageDigest) {
        return new Visitor(writer, messageDigest);
    }

    protected static class Visitor extends RecursiveVisitor {
        public Visitor(final Writer writer, final MessageDigest messageDigest) {
            super(writer, messageDigest);
        }

        @Override
        public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
            writeError(this.writer, dir);
            return FileVisitResult.SKIP_SUBTREE;
        }
    }
}
