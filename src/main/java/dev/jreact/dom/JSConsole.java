package dev.jreact.dom;

import org.teavm.jso.JSBody;

public class JSConsole {
    private JSConsole() {}

    @JSBody(params = { "msg" }, script = "console.log(msg)")
    public static native void log(String msg);
}
