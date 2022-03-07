package walk;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;

public class RecursiveWalk extends AbstractWalk {
    public static void main(final String[] args) {
        new RecursiveWalk().run(args);
    }

    @Override
    protected FileVisitor<Path> getFileVisitor(final Writer writer, final MessageDigest messageDigest) {
        return new RecursiveVisitor(writer, messageDigest);
    }

    protected static class RecursiveVisitor extends AbstractVisitor {
        public RecursiveVisitor(final Writer writer, final MessageDigest messageDigest) {
            super(writer, messageDigest);
        }

        @Override
        public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
            try (final InputStream reader = Files.newInputStream(file)) {
                messageDigest.reset();
                int readCount;
                while ((readCount = reader.read(buffer)) >= 0) {
                    messageDigest.update(buffer, 0, readCount);
                }
            } catch (final IOException e) {
                writeError(writer, file);
                return FileVisitResult.CONTINUE;
            }
            write(writer, hexFormat.formatHex(this.messageDigest.digest()), file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(final Path file, final IOException exc) throws IOException {
            writeError(this.writer, file);
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) {
            return FileVisitResult.CONTINUE;
        }
    }
}
