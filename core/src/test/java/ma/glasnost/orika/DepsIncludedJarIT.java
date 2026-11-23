package ma.glasnost.orika;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DepsIncludedJarIT {

    private static final String JAR_PROPERTY = "orika.depsIncludedJar";
    private static final Pattern MULTI_RELEASE_DIRECTORY = Pattern.compile("^META-INF/versions/\\d+/");

    @Test
    void testHoldsNoClassInAPackageStartingWithComGoogle() throws IOException {
        // given
        List<String> entryNames = depsIncludedJarEntryNames();

        // when
        List<String> comGoogleClasses = classesStartingWith(entryNames, "com/google");

        // then
        assertEquals(Collections.emptyList(), comGoogleClasses);
    }

    @Test
    void testHoldsNoMavenMetadataForTheGuavaGroup() throws IOException {
        // given
        List<String> entryNames = depsIncludedJarEntryNames();

        // when
        List<String> guavaMetadata = entryNames.stream()
                .filter(name -> name.startsWith("META-INF/maven/com.google.guava/"))
                .collect(Collectors.toList());

        // then
        assertEquals(Collections.emptyList(), guavaMetadata);
    }

    static Stream<Arguments> guavaDependencyClassPathPrefixes() {
        return Stream.of(
                Arguments.of("failureaccess", "com/google/common/util/concurrent/internal/"),
                Arguments.of("listenablefuture", "com/google/common/util/concurrent/ListenableFuture.class"),
                Arguments.of("jsr305", "javax/annotation/"),
                Arguments.of("checker-qual", "org/checkerframework/"),
                Arguments.of("error_prone_annotations", "com/google/errorprone/"),
                Arguments.of("j2objc-annotations", "com/google/j2objc/"),
                Arguments.of("jspecify", "org/jspecify/"));
    }

    @ParameterizedTest(name = "holds no class from {0}")
    @MethodSource("guavaDependencyClassPathPrefixes")
    void testHoldsNoClassFromAGuavaDependency(String artifactId, String classPathPrefix) throws IOException {
        // given
        List<String> entryNames = depsIncludedJarEntryNames();

        // when
        List<String> dependencyClasses = classesStartingWith(entryNames, classPathPrefix);

        // then
        assertEquals(Collections.emptyList(), dependencyClasses, artifactId);
    }

    private static List<String> depsIncludedJarEntryNames() throws IOException {
        String jarPath = System.getProperty(JAR_PROPERTY);
        assertNotNull(jarPath, "System property " + JAR_PROPERTY + " must name the deps-included jar");
        try (JarFile jar = new JarFile(jarPath)) {
            return jar.stream().map(ZipEntry::getName).collect(Collectors.toList());
        }
    }

    private static List<String> classesStartingWith(List<String> entryNames, String classPathPrefix) {
        return entryNames.stream()
                .filter(name -> name.endsWith(".class"))
                .filter(name -> MULTI_RELEASE_DIRECTORY.matcher(name).replaceFirst("").startsWith(classPathPrefix))
                .collect(Collectors.toList());
    }
}
