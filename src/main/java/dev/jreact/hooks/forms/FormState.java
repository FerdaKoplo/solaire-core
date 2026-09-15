package dev.jreact.hooks.forms;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.html.HTMLInputElement;

import dev.jreact.hooks.states.LocalState;


public class FormState<T> {
    private final LocalState<T> state;

    public FormState(T initialValue) {
        this.state = new LocalState<>(initialValue);
    }

    public T getData() {
        return state.get();
    }

    public Consumer<Event> register(BiConsumer<T, String> setter) {
        return (Event e) -> {
            HTMLInputElement input = (HTMLInputElement) e.getTarget();

            setter.accept(state.get(), input.getValue());
            state.set(state.get());
        };
    }

    public Consumer<Event> handleSubmit(Consumer<T> onSubmit) {
        return (Event e) -> {
            e.preventDefault();
            onSubmit.accept(state.get());
        };
    }

}
