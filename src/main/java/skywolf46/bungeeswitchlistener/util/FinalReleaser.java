package skywolf46.bungeeswitchlistener.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class FinalReleaser {
    public static final void release(Field f) throws Exception {
        f.setAccessible(true);
        Field mod = Field.class.getDeclaredField("modifiers");
        mod.setAccessible(true);
        mod.setInt(f, f.getModifiers() & ~Modifier.FINAL);
    }
}
