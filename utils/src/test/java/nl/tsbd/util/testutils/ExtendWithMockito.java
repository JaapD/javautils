package nl.tsbd.util.testutils;

import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ExtendWithMockito {
}
