package walk;

import walk.exception.AppException;
import walk.exception.PathException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public abstract class AbstractWalk {
    protected static final Charset INPUT_ENCODING = StandardCharsets.UTF_8;
    protected static final Charset OUTPUT_ENCODING = StandardCharsets.UTF_8;

    protected static final String ALGORITHM = "SHA-1";
    protected static final String ZERO_CODE = "0".repeat(40);

    protected static final HexFormat hexFormat = HexFormat.of();

    protected void run(final String[] args) {
        if (args == null || args.length != 2 || args[0] == null || args[1] == null) {
            System.out.println("Error: requires 2 nonnull arguments");
            return;
        }
        try {
            final Path inputPath = pathFromName(args[0].trim(), false);
            final Path outputPath = pathFromName(args[1].trim(), true);
            run(inputPath, outputPath);
        } catch (final AppException | PathException e) {
            System.out.format("Error: %s\n", e.getMessage());
        }
    }

    private void run(final Path inputPath, final Path outputPath) throws AppException {
        final MessageDigest messageDigest;
        try {
            messageDigest = MessageDigest.getInstance(ALGORITHM);
        } catch (final NoSuchAlgorithmException e) {
            throw new AppException(String.format("algorithm is unsupported: %s", ALGORITHM), e);
        }

        try (final BufferedReader inputReader = createBufferedReader(inputPath, INPUT_ENCODING)) {
            try (final Writer outputWriter = createBufferedWriter(outputPath, OUTPUT_ENCODING)) {
                final FileVisitor<Path> fileVisitor = getFileVisitor(outputWriter, messageDigest);
                try {
                    String name;
                    while ((name = readLine(inputPath, inputReader)) != null) {
                        try {
                            Files.walkFileTree(pathFromName(name, false), fileVisitor);
                        } catch (final PathException e) {
                            writeError(outputWriter, name);
                        }
                    }
                } catch (final IOException e) {
                    throw new AppException(String.format("couldn't write to output file: %s", outputPath), e);
                }
            } catch (final IOException e) {
                throw new AppException(String.format("couldn't close output file: %s", outputPath), e);
            }
        } catch (final IOException e) {
            throw new AppException(String.format("couldn't close input file: %s", inputPath), e);
        }
    }

    private static Path pathFromName(final String name, final boolean createDirectories) throws PathException {
        try {
            final Path path = Path.of(name);
            if (createDirectories) {
                try {
                    final Path parent = path.getParent();
                    if (parent != null) {
                        Files.createDirectories(parent);
                    }
                } catch (final IOException ignored) {
                }
            }
            return path;
        } catch (final InvalidPathException e) {
            throw new PathException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static BufferedReader createBufferedReader(final Path path, final Charset cs) throws AppException {
        try {
            return Files.newBufferedReader(path, cs);
        } catch (final NoSuchFileException e) {
            throw new AppException(String.format("couldn't find input file: %s", path), e);
        } catch (final IOException e) {
            throw new AppException(String.format("couldn't open input file: %s", path), e);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private static BufferedWriter createBufferedWriter(final Path path, final Charset cs) throws AppException {
        try {
            return Files.newBufferedWriter(path, cs);
        } catch (final NoSuchFileException e) {
            throw new AppException(String.format("couldn't find output file: %s", path), e);
        } catch (final IOException e) {
            throw new AppException(String.format("couldn't open output file: %s", path), e);
        }
    }

    private static String readLine(final Path path, final BufferedReader reader) throws AppException {
        try {
            return reader.readLine();
        } catch (final IOException e) {
            throw new AppException(String.format("couldn't read from input file: %s", path), e);
        }
    }

    protected static void write(final Writer writer, final String code, final Object obj) throws IOException {
        writer.write(String.format("%s %s%n", code, obj.toString()));
    }

    protected static void writeError(final Writer writer, final Object obj) throws IOException {
        write(writer, ZERO_CODE, obj.toString());
    }

    @SuppressWarnings("SameParameterValue")
    protected abstract FileVisitor<Path> getFileVisitor(Writer writer, MessageDigest messageDigest);

    protected abstract static class AbstractVisitor extends SimpleFileVisitor<Path> {
        protected final Writer writer;
        protected final byte[] buffer = new byte[1 << 16];
        protected final MessageDigest messageDigest;

        public AbstractVisitor(final Writer writer, final MessageDigest messageDigest) {
            this.writer = writer;
            this.messageDigest = messageDigest;
        }
    }
}
