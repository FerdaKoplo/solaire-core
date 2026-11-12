package dev.jreact.net;

import java.util.Objects;
import java.util.function.Consumer;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSPromise;

public final class Http {
    private Http() {}

    public static final int OK = 200;

    // Must extend JSObject with @JSProperty so TeaVM bridges the JS
    // {status, body} object returned by fetch. A plain interface fails
    // the JS->Java cast and every accessor throws "not a function".
    public interface Response extends JSObject {
        @JSProperty("status") int status();
        @JSProperty("body") String body();
    }

    // @JSBody seams: raw JS mirrors the modern fetch API. TeaVM 0.15 ships no
    // fetch binding, so these are the only untyped lines in the library.
    // Upgrade path: replace with org.teavm.jso.fetch when TeaVM ships it.

    @JSBody(params = { "url" }, script =
        "return fetch(url).then(function(r) { return r.text().then(function(t) { return { status: r.status, body: t }; }); });"
    )
    private static native JSPromise<Response> fetchGet(String url);

    @JSBody(params = { "url", "body", "contentType" }, script =
        "return fetch(url, { method: 'POST', headers: { 'Content-Type': contentType }, body: body })" +
        ".then(function(r) { return r.text().then(function(t) { return { status: r.status, body: t }; }); });"
    )
    private static native JSPromise<Response> fetchPost(String url, String body, String contentType);

    @JSBody(params = { "url", "body", "contentType" }, script =
        "return fetch(url, { method: 'PUT', headers: { 'Content-Type': contentType }, body: body })" +
        ".then(function(r) { return r.text().then(function(t) { return { status: r.status, body: t }; }); });"
    )
    private static native JSPromise<Response> fetchPut(String url, String body, String contentType);

    @JSBody(params = { "url", "body", "contentType" }, script =
        "return fetch(url, { method: 'PATCH', headers: { 'Content-Type': contentType }, body: body })" +
        ".then(function(r) { return r.text().then(function(t) { return { status: r.status, body: t }; }); });"
    )
    private static native JSPromise<Response> fetchPatch(String url, String body, String contentType);

    @JSBody(params = { "url" }, script =
        "return fetch(url, { method: 'DELETE' })" +
        ".then(function(r) { return r.text().then(function(t) { return { status: r.status, body: t }; }); });"
    )
    private static native JSPromise<Response> fetchDelete(String url);

    /** GET {@code url}; {@code onComplete} runs when the full body is read. */
    public static void get(String url, Consumer<Response> onComplete) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(onComplete, "onComplete");
        fetchGet(url).then(res -> { onComplete.accept(res); return null; });
    }

    public static void post(String url, String body, Consumer<Response> onComplete) {
        send(fetchPost(url, body, "application/json"), onComplete);
    }

    public static void put(String url, String body, Consumer<Response> onComplete) {
        send(fetchPut(url, body, "application/json"), onComplete);
    }

    public static void patch(String url, String body, Consumer<Response> onComplete) {
        send(fetchPatch(url, body, "application/json"), onComplete);
    }

    public static void delete(String url, Consumer<Response> onComplete) {
        Objects.requireNonNull(url, "url");
        Objects.requireNonNull(onComplete, "onComplete");
        fetchDelete(url).then(res -> { onComplete.accept(res); return null; });
    }

    private static void send(JSPromise<Response> promise, Consumer<Response> onComplete) {
        Objects.requireNonNull(onComplete, "onComplete");
        promise.then(res -> { onComplete.accept(res); return null; });
    }
}
