package dev.jreact.data;

import java.util.Objects;
import java.util.function.Consumer;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSPromise;

/**
 * Minimal fetch client. TeaVM 0.15 ships no fetch binding, so the only
 * untyped lines in the library are these @JSBody seams.
 * Upgrade path: replace with org.teavm.jso.fetch when TeaVM ships it.
 */
public final class Http {
    private Http() {}

    public static final class Response implements JSObject {
        @JSProperty("status") public native int status();
        @JSProperty("body") public native String body();

        public boolean ok() { return status() >= 200 && status() < 300; }
        public boolean is2xx() { return ok(); }
    }

    @JSBody(params = { "url" }, script =
        "return fetch(url).then(function(r){return r.text().then(function(t){return {status:r.status, body:t};});});")
    private static native JSPromise<Response> fetchGet(String url);

    @JSBody(params = { "url", "body", "ct" }, script =
        "return fetch(url,{method:'POST',headers:{'Content-Type':ct},body:body})" +
        ".then(function(r){return r.text().then(function(t){return {status:r.status, body:t};});});")
    private static native JSPromise<Response> fetchWith(String method, String url, String body, String ct);

    @JSBody(params = { "url" }, script =
        "return fetch(url,{method:'DELETE'})" +
        ".then(function(r){return r.text().then(function(t){return {status:r.status, body:t};});});")
    private static native JSPromise<Response> fetchDelete(String url);

    /** GET; onComplete receives response (check .ok() / .status()). */
    public static void get(String url, Consumer<Response> onComplete) {
        require(url, onComplete);
        fetchGet(url).then(res -> { onComplete.accept(res); return null; });
    }

    public static void post(String url, String jsonBody, Consumer<Response> onComplete) {
        require(url, onComplete);
        fetchWith("POST", url, jsonBody, "application/json").then(res -> { onComplete.accept(res); return null; });
    }

    public static void put(String url, String jsonBody, Consumer<Response> onComplete) {
        require(url, onComplete);
        fetchWith("PUT", url, jsonBody, "application/json").then(res -> { onComplete.accept(res); return null; });
    }

    public static void patch(String url, String jsonBody, Consumer<Response> onComplete) {
        require(url, onComplete);
        fetchWith("PATCH", url, jsonBody, "application/json").then(res -> { onComplete.accept(res); return null; });
    }

    public static void delete(String url, Consumer<Response> onComplete) {
        require(url, onComplete);
        fetchDelete(url).then(res -> { onComplete.accept(res); return null; });
    }

    private static void require(String url, Consumer<?> cb) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(cb, "callback");
    }
}
